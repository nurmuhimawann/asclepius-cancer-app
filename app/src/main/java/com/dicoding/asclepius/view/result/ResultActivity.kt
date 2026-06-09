package com.dicoding.asclepius.view.result

import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setToolbar()
        setDetail()
    }

    private fun setToolbar() {
        binding.apply {
            topAppBar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun setDetail() {
        val resultText = intent.getStringExtra(EXTRA_RESULT_TEXT)
        val confidenceScore = intent.getFloatExtra(EXTRA_CONFIDENCE_SCORE, 0f)
        val imageUriString = intent.getStringExtra(EXTRA_IMAGE_URI)

        val confidenceScoreText = getString(R.string.confidence_score_format, confidenceScore)
        binding.resultText.text = getString(R.string.result_analyze, resultText, confidenceScoreText)

        imageUriString?.let {
            val imageUri = Uri.parse(it)
            binding.resultImage.setImageURI(imageUri)
        }
    }

    companion object {
        const val EXTRA_RESULT_TEXT = "extra_result_text"
        const val EXTRA_CONFIDENCE_SCORE = "extra_confidence_score"
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }
}