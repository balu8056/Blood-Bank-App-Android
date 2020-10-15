package com.example.bloodapp.ui.home.fragments.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bloodapp.data.repository.homerep.HomeFragmentRepository

@Suppress("UNCHECKED_CAST")
class HomeFragmentFactory (private val homeRepository: HomeFragmentRepository): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeFragmentViewModel(homeRepository) as T
    }
}