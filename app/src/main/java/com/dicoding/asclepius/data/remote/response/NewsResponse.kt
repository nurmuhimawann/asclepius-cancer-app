package com.dicoding.asclepius.data.remote.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class NewsResponse(
	val totalResults: Int,
	val articles: List<ArticlesItem>,
	val status: String
)

@Parcelize
data class ArticlesItem(
	val publishedAt: String? = null,
	val author: String? = null,
	val urlToImage: String? = null,
	val description: String? = null,
	val title: String? = null,
	val url: String? = null,
	val content: String? = null
): Parcelable
