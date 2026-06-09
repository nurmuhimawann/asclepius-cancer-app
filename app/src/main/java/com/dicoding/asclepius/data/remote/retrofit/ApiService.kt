package com.dicoding.asclepius.data.remote.retrofit

import com.dicoding.asclepius.data.remote.response.NewsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("v2/top-headlines")
    suspend fun getNews(
        @Query("category") category: String,
        @Query("language") language: String,
        @Query("apiKey") apiKey: String,
        @Query("pageSize") pageSize: Int = 100
    ): NewsResponse
}