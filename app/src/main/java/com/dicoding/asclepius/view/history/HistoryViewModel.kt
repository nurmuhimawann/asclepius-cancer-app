package com.dicoding.asclepius.view.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.local.Asclepius
import com.dicoding.asclepius.data.repository.AsclepiusRepository
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: AsclepiusRepository) : ViewModel() {

    fun getAllAsclepius(): LiveData<List<Asclepius>> = repository.getAllAsclepius()

    fun delete(asclepius: Asclepius) {
        viewModelScope.launch {
            repository.delete(asclepius)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }
}
