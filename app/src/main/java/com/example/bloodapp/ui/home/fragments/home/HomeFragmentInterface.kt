package com.example.bloodapp.ui.home.fragments.home

import com.example.bloodapp.data.UserData

interface HomeFragmentInterface {
    fun getUser(userData: UserData)
}

interface HomeFragmentViewInter {
    fun userList(users: UserData)
}