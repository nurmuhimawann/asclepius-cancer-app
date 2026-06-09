package com.dicoding.asclepius.utils

import androidx.recyclerview.widget.DiffUtil
import com.dicoding.asclepius.data.local.Asclepius

class AsclepiusDiffCallback(private val oldAsclepiusList: List<Asclepius>, private val newAsclepiusList: List<Asclepius>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldAsclepiusList.size
    override fun getNewListSize(): Int = newAsclepiusList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldAsclepiusList[oldItemPosition].id == newAsclepiusList[newItemPosition].id
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldAsclepius = oldAsclepiusList[oldItemPosition]
        val newAsclepius= newAsclepiusList[newItemPosition]
        return oldAsclepius.result == newAsclepius.result && oldAsclepius.confidenceScore == newAsclepius.confidenceScore && oldAsclepius.imageUri == newAsclepius.imageUri
    }
}
