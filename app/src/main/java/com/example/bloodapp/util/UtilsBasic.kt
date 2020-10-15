@file:Suppress("DEPRECATION")

package com.example.bloodapp.util

import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

fun View.snack(msg: String, duration: Int = Snackbar.LENGTH_SHORT){
    Snackbar.make(this, msg, duration).show()
}

fun Context.toast(msg: String){
   Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.checkInternetIsAvail(): Boolean{
    val conMan = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netInfo = conMan.activeNetworkInfo
    return netInfo!=null && netInfo.isConnected
}

fun View.visibilityOn(){
    visibility = View.VISIBLE
}

fun View.visibilityGone(){
    visibility = View.GONE
}

fun View.enabledTrue(){
    isEnabled = true
}

fun View.enabledFalse(){
    isEnabled = false
}