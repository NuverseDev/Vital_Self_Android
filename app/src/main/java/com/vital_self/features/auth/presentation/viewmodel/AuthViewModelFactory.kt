package com.vital_self.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vital_self.features.auth.data.repository.AuthRepository
import com.vital_self.features.scan.data.repository.ScanRepository
import com.vital_self.features.auth.presentation.viewmodel.AuthViewModel
import com.vital_self.features.scan.presentation.viewmodel.ScanViewModel

class AuthViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}