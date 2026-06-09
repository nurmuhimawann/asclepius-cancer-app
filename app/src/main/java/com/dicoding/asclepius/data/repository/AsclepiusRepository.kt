package com.dicoding.asclepius.data.repository

import androidx.lifecycle.LiveData
import com.dicoding.asclepius.BuildConfig
import com.dicoding.asclepius.data.local.Asclepius
import com.dicoding.asclepius.data.local.AsclepiusDao
import com.dicoding.asclepius.data.remote.retrofit.ApiService
import com.dicoding.asclepius.data.remote.response.ArticlesItem

class AsclepiusRepository private constructor(
    private val asclepiusDao: AsclepiusDao,
    private val apiService: ApiService
) {
    fun getAllAsclepius(): LiveData<List<Asclepius>> = asclepiusDao.getAllAsclepius()

    suspend fun insert(asclepius: Asclepius) {
        asclepiusDao.insert(asclepius)
    }

    suspend fun delete(asclepius: Asclepius) {
        asclepiusDao.delete(asclepius)
    }

    suspend fun deleteAll() {
        asclepiusDao.deleteAll()
    }

    suspend fun getNews(): List<ArticlesItem> {
        val response = apiService.getNews("health", "en", BuildConfig.API_KEY, 100)
        return response.articles ?: emptyList()
    }

    companion object {
        @Volatile
        private var instance: AsclepiusRepository? = null
        fun getInstance(
            asclepiusDao: AsclepiusDao,
            apiService: ApiService
        ): AsclepiusRepository =
            instance ?: synchronized(this) {
                instance ?: AsclepiusRepository(asclepiusDao, apiService)
            }.also { instance = it }
    }
}
