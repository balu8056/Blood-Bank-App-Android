package com.example.bloodapp.ui.home

import com.example.bloodapp.data.UserData

interface UserDetailInter {
    fun giveData(userData: UserData)
}

interface ForSettingUserData{
    fun setUserData(userData: UserData)
}