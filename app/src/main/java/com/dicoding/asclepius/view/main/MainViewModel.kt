package com.dicoding.asclepius.view.main

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.local.Asclepius
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.data.repository.AsclepiusRepository
import com.dicoding.asclepius.utils.getCurrentDate
import kotlinx.coroutines.launch
import org.tensorflow.lite.task.vision.classifier.Classifications

class MainViewModel(private val repository: AsclepiusRepository) : ViewModel() {

    private val _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> = _currentImageUri

    private val _listArticle = MutableLiveData<List<ArticlesItem>>()
    val listArticle: LiveData<List<ArticlesItem>> = _listArticle

    private val _pagedArticles = MutableLiveData<List<ArticlesItem>>()
    val pagedArticles: LiveData<List<ArticlesItem>> = _pagedArticles

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _classificationResult = MutableLiveData<Pair<String, Float>?>()
    val classificationResult: LiveData<Pair<String, Float>?> = _classificationResult

    private var allArticles: List<ArticlesItem> = emptyList()
    private var currentCount = 0
    private val PAGE_SIZE = 10
    private val MAX_ITEMS = 100

    init {
        getNews()
    }

    fun setImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }

    fun insert(asclepius: Asclepius) {
        viewModelScope.launch {
            repository.insert(asclepius)
        }
    }

    private fun getNews() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val articles = repository.getNews()
                allArticles = articles.filter {
                    it.title != "[Removed]" && it.description != "[Removed]"
                }.sortedByDescending { it.publishedAt }.take(MAX_ITEMS)
                
                _listArticle.value = allArticles
                resetPagination()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetPagination() {
        currentCount = if (allArticles.size >= PAGE_SIZE) PAGE_SIZE else allArticles.size
        _pagedArticles.value = allArticles.take(currentCount)
    }

    fun loadMore() {
        if (currentCount < allArticles.size) {
            val nextCount = currentCount + PAGE_SIZE
            currentCount = if (nextCount > allArticles.size) allArticles.size else nextCount
            _pagedArticles.value = allArticles.take(currentCount)
        }
    }

    fun isLoadMoreAvailable(): Boolean {
        return currentCount < allArticles.size
    }

    fun processResults(results: List<Classifications>?) {
        if (!results.isNullOrEmpty() && results[0].categories.isNotEmpty()) {
            val sortedCategories = results[0].categories.sortedByDescending { it?.score }
            val topCategory = sortedCategories.firstOrNull()

            if (topCategory != null) {
                val label = topCategory.label
                val confidenceScore = topCategory.score * 100
                _classificationResult.value = Pair(label, confidenceScore)
            }
        }
    }

    fun saveAnalysisResult(resultText: String, confidenceScore: Float) {
        currentImageUri.value?.let { uri ->
            val asclepius = Asclepius(
                result = resultText,
                confidenceScore = confidenceScore,
                imageUri = uri.toString(),
                date = getCurrentDate()
            )
            insert(asclepius)
        }
    }

    fun resetClassificationResult() {
        _classificationResult.value = null
    }
}
