package com.dicoding.asclepius.di

import android.content.Context
import com.dicoding.asclepius.data.local.AsclepiusRoomDatabase
import com.dicoding.asclepius.data.remote.retrofit.ApiConfig
import com.dicoding.asclepius.data.repository.AsclepiusRepository

object Injection {
    fun provideRepository(context: Context): AsclepiusRepository {
        val database = AsclepiusRoomDatabase.getDatabase(context)
        val dao = database.asclepiusDao()
        val apiService = ApiConfig.getApiService()
        return AsclepiusRepository.getInstance(dao, apiService)
    }
}