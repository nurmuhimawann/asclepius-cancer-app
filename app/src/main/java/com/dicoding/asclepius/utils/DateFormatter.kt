package com.dicoding.asclepius.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun formatNewsDate(input: String?): String {
    if (input.isNullOrEmpty()) return "Unknown Date"
    return try {
        val sdfInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
        sdfInput.timeZone = TimeZone.getTimeZone("UTC")

        val sdfOutput = SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH)
        val date = sdfInput.parse(input)
        if (date != null) {
            sdfOutput.format(date)
        } else {
            input
        }
    } catch (e: Exception) {
        input
    }
}

fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
    val date = Date()
    return dateFormat.format(date)
}
