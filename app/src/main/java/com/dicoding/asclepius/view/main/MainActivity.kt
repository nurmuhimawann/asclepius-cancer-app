package com.dicoding.asclepius.view.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.R
import com.dicoding.asclepius.view.ViewModelFactory
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.utils.ImageClassifierHelper
import com.dicoding.asclepius.view.news.NewsActivity
import com.dicoding.asclepius.view.news.DetailNewsActivity
import com.dicoding.asclepius.view.news.NewsAdapter
import com.dicoding.asclepius.view.history.HistoryActivity
import com.dicoding.asclepius.view.result.ResultActivity
import com.dicoding.asclepius.view.result.ResultActivity.Companion.EXTRA_CONFIDENCE_SCORE
import com.dicoding.asclepius.view.result.ResultActivity.Companion.EXTRA_IMAGE_URI
import com.dicoding.asclepius.view.result.ResultActivity.Companion.EXTRA_RESULT_TEXT
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var newsAdapter: NewsAdapter

    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mainViewModel = obtainViewModel(this)

        setupImageClassifier()
        setupRecyclerView()
        observeViewModel()
        setListeners()
        optionMenu()
    }

    private fun setupImageClassifier() {
        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    runOnUiThread {
                        showProgressIndicator(false)
                        showToast("Error: $error")
                    }
                }

                override fun onResult(result: List<Classifications>?, inferenceTime: Long) {
                    runOnUiThread {
                        showProgressIndicator(false)
                        mainViewModel.processResults(result)
                    }
                }
            }
        )
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter().apply {
            onItemClick = { selectedData ->
                val intent = Intent(this@MainActivity, DetailNewsActivity::class.java)
                intent.putExtra(DetailNewsActivity.EXTRA_NEWS, selectedData)
                startActivity(intent)
            }
            onMoreClick = {
                val intent = Intent(this@MainActivity, NewsActivity::class.java)
                startActivity(intent)
            }
        }
        binding.rvNews.adapter = newsAdapter
    }

    private fun observeViewModel() {
        mainViewModel.currentImageUri.observe(this) { uri ->
            currentImageUri = uri
            if (uri != null) {
                binding.previewImageView.setImageURI(uri)
            } else {
                binding.previewImageView.setImageResource(R.drawable.ic_place_holder)
            }
        }

        mainViewModel.listArticle.observe(this) { news ->
            val filteredNews = news.filter {
                it.title != "[Removed]" && it.description != "[Removed]"
            }.take(8)
            newsAdapter.submitList(filteredNews)
        }

        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        mainViewModel.error.observe(this) { error ->
            error?.let { showToast(it) }
        }

        mainViewModel.classificationResult.observe(this) { result ->
            result?.let { (label, confidenceScore) ->
                val resultText = if (label.equals("Cancer", ignoreCase = true)) {
                    getString(R.string.cancer)
                } else {
                    getString(R.string.non_cancer)
                }
                moveToResult(resultText, confidenceScore)
                mainViewModel.saveAnalysisResult(resultText, confidenceScore)
                mainViewModel.resetClassificationResult()
                mainViewModel.setImageUri(null)
            }
        }
    }

    private fun setListeners() {
        binding.apply {
            galleryButton.setOnClickListener {
                startGallery()
            }
            analyzeButton.setOnClickListener {
                analyzeImage()
            }
            tvMoreNews.setOnClickListener {
                val intent = Intent(this@MainActivity, NewsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            startCrop(uri)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private val cropImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val resultUri = result.data?.let { UCrop.getOutput(it) }
            if (resultUri != null) {
                mainViewModel.setImageUri(resultUri)
            } else {
                showToast(getString(R.string.crop_failed))
            }
        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            val cropError = result.data?.let { UCrop.getError(it) }
            cropError?.let { showToast("Crop error: ${it.message}") }
        }
    }

    private fun startCrop(uri: Uri) {
        val destinationUri = Uri.fromFile(File(cacheDir, "cropped_img_${System.currentTimeMillis()}.jpg"))
        val options = UCrop.Options().apply {
            setCompressionQuality(80)
            setToolbarTitle(getString(R.string.crop_image))
            setToolbarColor(getColor(R.color.md_theme_primary))
            setStatusBarColor(getColor(R.color.md_theme_primary))
            setToolbarWidgetColor(getColor(R.color.md_theme_onPrimary))
        }

        val uCropIntent = UCrop.of(uri, destinationUri)
            .withOptions(options)
            .getIntent(this)

        cropImageLauncher.launch(uCropIntent)
    }

    private fun analyzeImage() {
        currentImageUri?.let { uri ->
            showProgressIndicator(true)
            imageClassifierHelper.classifyImage(uri)
        } ?: run {
            showToast(getString(R.string.select_an_image))
        }
    }

    private fun moveToResult(resultText: String, confidenceScore: Float) {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra(EXTRA_RESULT_TEXT, resultText)
            putExtra(EXTRA_CONFIDENCE_SCORE, confidenceScore)
            putExtra(EXTRA_IMAGE_URI, currentImageUri.toString())
        }
        startActivity(intent)
    }

    private fun optionMenu() {
        binding.topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuHistory -> {
                    val intent = Intent(this, HistoryActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showProgressIndicator(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun obtainViewModel(activity: AppCompatActivity): MainViewModel {
        val factory = ViewModelFactory.getInstance(activity)
        return ViewModelProvider(activity, factory)[MainViewModel::class.java]
    }
}
