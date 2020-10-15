package com.example.bloodapp.ui.home.fragments.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bloodapp.data.repository.auth.AuthenticationRepository

@Suppress("UNCHECKED_CAST")
class AccountsFactory(private val authenticationRepository: AuthenticationRepository): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AccountsViewModel(authenticationRepository) as T
    }
}