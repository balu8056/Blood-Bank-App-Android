package com.example.bloodapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bloodapp.data.repository.auth.AuthenticationRepository

@Suppress("UNCHECKED_CAST")
class AuthFactoryClass(private val authenticationRepository: AuthenticationRepository): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AuthViewModel(authenticationRepository) as T
    }
}