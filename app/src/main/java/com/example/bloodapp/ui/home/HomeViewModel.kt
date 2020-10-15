package com.example.bloodapp.ui.home

import androidx.lifecycle.ViewModel
import com.example.bloodapp.data.UserData
import com.example.bloodapp.data.repository.auth.AuthenticationRepository

const val homeViewModelTag = "homeViewModelTag"

class HomeViewModel(private val authenticationRepository: AuthenticationRepository) : ViewModel(), UserDetailInter {

    init {
        authenticationRepository.userDetailInterFace = this
    }

    var forSettingUserData: ForSettingUserData? = null

    fun getUserDetailsFromRepository(){
        authenticationRepository.getUserDetail()
    }

    override fun giveData(userData: UserData) {
        forSettingUserData?.setUserData(userData)
    }

    fun signOut(): Boolean{
        return authenticationRepository.logOut()
    }

    override fun onCleared() {
        forSettingUserData = null
        super.onCleared()
    }

}