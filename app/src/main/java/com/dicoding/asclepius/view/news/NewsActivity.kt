package com.dicoding.asclepius.view.news

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.databinding.ActivityNewsBinding
import com.dicoding.asclepius.view.ViewModelFactory
import com.dicoding.asclepius.view.main.MainViewModel

class NewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setToolbar()
        setupViewModel()
        setupRecyclerView()
    }

    private fun setToolbar() {
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory.getInstance(this)
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    private fun setupRecyclerView() {
        val adapter = VerticalNewsAdapter()
        adapter.onItemClick = { selectedData: ArticlesItem ->
            val intent = Intent(this, DetailNewsActivity::class.java)
            intent.putExtra(DetailNewsActivity.EXTRA_NEWS, selectedData)
            startActivity(intent)
        }
        adapter.onLoadMoreClick = {
            mainViewModel.loadMore()
        }

        binding.rvNews.layoutManager = LinearLayoutManager(this)
        binding.rvNews.adapter = adapter

        mainViewModel.pagedArticles.observe(this) { pagedNews ->
            adapter.submitList(pagedNews)
            adapter.showLoadMore = mainViewModel.isLoadMoreAvailable()
        }

        mainViewModel.isLoading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }
}
