package com.dicoding.asclepius.view.news

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat.getParcelableExtra
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.databinding.ActivityDetailNewsBinding
import com.dicoding.asclepius.utils.formatNewsDate

class DetailNewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailNewsBinding.inflate(layoutInflater)
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

    @SuppressLint("SetTextI18n")
    private fun setDetail() {
        val news = getParcelableExtra(intent, EXTRA_NEWS, ArticlesItem::class.java)
        if (news != null) {
            binding.apply {
                Glide.with(this@DetailNewsActivity)
                    .load(news.urlToImage)
                    .placeholder(R.drawable.ic_place_holder)
                    .error(R.drawable.ic_place_holder)
                    .into(ivNews)

                textTitle.text = news.title ?: "No Title"
                textAuthor.text = resources.getString(R.string.by, news.author ?: "Unknown")
                textDescription.text = news.description ?: "No Description"
                textPublishedAt.text = formatNewsDate(news.publishedAt)
            }
        }
    }

    companion object {
        const val EXTRA_NEWS = "extra_news"
    }
}