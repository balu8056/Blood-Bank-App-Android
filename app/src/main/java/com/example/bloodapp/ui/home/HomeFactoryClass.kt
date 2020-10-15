package com.example.bloodapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bloodapp.data.repository.auth.AuthenticationRepository

@Suppress("UNCHECKED_CAST")
class HomeFactoryClass(private val authenticationRepository: AuthenticationRepository): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(authenticationRepository) as T
    }
}