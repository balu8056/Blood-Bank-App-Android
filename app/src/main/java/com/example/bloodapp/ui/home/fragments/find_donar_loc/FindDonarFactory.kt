package com.example.bloodapp.ui.home.fragments.find_donar_loc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bloodapp.data.repository.homerep.HomeFragmentRepository

@Suppress("UNCHECKED_CAST")
class FindDonarFactory(private val homeFragmentRepository: HomeFragmentRepository): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FindDonarViewModel(homeFragmentRepository) as T
    }

}