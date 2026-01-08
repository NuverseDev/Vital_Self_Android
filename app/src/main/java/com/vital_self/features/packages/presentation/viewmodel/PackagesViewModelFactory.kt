package com.vital_self.features.packages.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vital_self.features.packages.data.repository.PackagesRepository
import com.vital_self.features.packages.presentation.viewmodel.PackagesViewModel

class PackagesViewModelFactory(private val repository: PackagesRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PackagesViewModel::class.java)) {
            return PackagesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
