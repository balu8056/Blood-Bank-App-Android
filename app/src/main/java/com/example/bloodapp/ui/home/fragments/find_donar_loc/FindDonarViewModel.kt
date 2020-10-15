package com.example.bloodapp.ui.home.fragments.find_donar_loc

import androidx.lifecycle.ViewModel
import com.example.bloodapp.data.Loc
import com.example.bloodapp.data.UserData
import com.example.bloodapp.data.repository.homerep.HomeFragmentRepository

class FindDonarViewModel(private val homeFragmentRepository: HomeFragmentRepository) : ViewModel(), FindDonarInterFace {

    init {
        homeFragmentRepository.findDonarInterFace = this
    }

    var findDonarInterFace2: FindDonarInterFace2? = null

    fun getUserByUid(uid: String){
        homeFragmentRepository.getUserByUid(uid)
    }

    override fun returnUserByUid(userData: UserData) {
        findDonarInterFace2?.returnUser(userData)
    }

    fun getUserLocation(uid: String){
        homeFragmentRepository.getUserLocation(uid)
    }

    override fun returnLocationString(location: Loc) {
        findDonarInterFace2?.returnLocation(location)
    }

    override fun onCleared() {
        findDonarInterFace2 = null
        super.onCleared()
    }

}