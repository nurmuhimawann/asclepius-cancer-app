package com.dicoding.asclepius.view.history

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.R
import com.dicoding.asclepius.view.ViewModelFactory
import com.dicoding.asclepius.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {

    private lateinit var adapter: HistoryAdapter
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        historyViewModel = obtainViewModel(this)

        setupToolbar()
        setupRecyclerView()
        observeHistory()
        setupMenu()
    }

    private fun setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter()
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            addItemDecoration(DividerItemDecoration(this@HistoryActivity, DividerItemDecoration.VERTICAL))
            setHasFixedSize(true)
            this.adapter = this@HistoryActivity.adapter
        }
    }

    private fun observeHistory() {
        historyViewModel.getAllAsclepius().observe(this) { asclepiusList ->
            if (!asclepiusList.isNullOrEmpty()) {
                adapter.setListAsclepius(asclepiusList)
                binding.rvHistory.visibility = View.VISIBLE
                binding.labelNoData.visibility = View.GONE
            } else {
                binding.rvHistory.visibility = View.GONE
                binding.labelNoData.visibility = View.VISIBLE
            }
        }
    }

    private fun setupMenu() {
        binding.topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuDeleteAll -> {
                    historyViewModel.deleteAll()
                    true
                }
                else -> false
            }
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): HistoryViewModel {
        val factory = ViewModelFactory.getInstance(activity)
        return ViewModelProvider(activity, factory)[HistoryViewModel::class.java]
    }
}
