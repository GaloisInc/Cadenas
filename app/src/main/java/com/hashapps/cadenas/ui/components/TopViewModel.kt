package com.hashapps.cadenas.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashapps.cadenas.data.models.ModelRepository
import kotlinx.coroutines.launch

open class TopViewModel(
    private val modelRepository: ModelRepository,
) : ViewModel() {

    /**
     * Delete all channels
     */
    fun deleteAllModels() {
        viewModelScope.launch {
            modelRepository.deleteAllModels()
        }
    }

}