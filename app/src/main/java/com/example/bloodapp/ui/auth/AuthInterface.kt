package com.example.bloodapp.ui.auth

import com.google.firebase.auth.FirebaseUser

interface AuthInterface {
    fun start()
    fun mistake(msg: String)
    fun currentUser(user: FirebaseUser?)
    fun success(msg: String)
    fun failure(msg: String)
}