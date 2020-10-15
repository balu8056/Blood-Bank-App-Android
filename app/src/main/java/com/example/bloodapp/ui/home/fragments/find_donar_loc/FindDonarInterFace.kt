package com.example.bloodapp.ui.home.fragments.find_donar_loc

import android.location.Location
import com.example.bloodapp.data.Loc
import com.example.bloodapp.data.UserData

interface FindDonarInterFace {
    fun returnLocationString(location: Loc)
    fun returnUserByUid(userData: UserData)
}

interface FindDonarInterFace2 {
    fun returnLocation(location: Loc)
    fun returnUser(userData: UserData)
}