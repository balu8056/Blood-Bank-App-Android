package com.example.bloodapp.ui.home.fragments.home

import android.location.Location
import androidx.lifecycle.ViewModel
import com.example.bloodapp.data.Loc
import com.example.bloodapp.data.UserData
import com.example.bloodapp.data.repository.homerep.HomeFragmentRepository

class HomeFragmentViewModel(private val homeFragmentRepository: HomeFragmentRepository) : ViewModel(), HomeFragmentInterface {

    var homeFragmentViewInter: HomeFragmentViewInter? = null

    init {
        homeFragmentRepository.homeFragmentInterface = this
    }

    fun requestForUserList(){
        homeFragmentRepository.getUser()
    }

    override fun getUser(userData: UserData) {
        homeFragmentViewInter?.userList(userData)
    }

    fun setUserLocation(location: Location){
        val loc = Loc(location.latitude, location.longitude)
        homeFragmentRepository.setUserLocation(loc)
    }

    override fun onCleared() {
        homeFragmentViewInter = null
        super.onCleared()
    }

}