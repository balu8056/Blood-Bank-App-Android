package com.example.bloodapp.data.repository.homerep

import android.util.Log
import com.example.bloodapp.data.Loc
import com.example.bloodapp.data.UserData
import com.example.bloodapp.ui.home.fragments.find_donar_loc.FindDonarInterFace
import com.example.bloodapp.ui.home.fragments.home.HomeFragmentInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

const val homeFragTag = "homeFragTag"

class HomeFragmentRepository {

    private var usersDb = FirebaseDatabase.getInstance().getReference("User")

    var homeFragmentInterface: HomeFragmentInterface? = null
    var findDonarInterFace: FindDonarInterFace? = null

    fun getUser(){
        try {
            usersDb.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    Log.e(homeFragTag, p0.message)
                }
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()){
                        try{
                            for (i in p0.children){
                                Log.e(homeFragTag, "${i.child("userData").getValue(UserData::class.java)!!.email} homeFragment")
                                homeFragmentInterface?.getUser(i.child("userData").getValue(UserData::class.java)!!)
                            }
                        }catch (e: Exception){
                            Log.e(homeFragTag, e.message.toString())
                        }
                    }
                }
            })
        }catch (e: Exception){
            Log.e(homeFragTag, e.message.toString())
        }
    }

    private fun getUserId(): String? = FirebaseAuth.getInstance().currentUser?.uid

    fun setUserLocation(location: Loc){
        try {
            val userId = getUserId()
            if (userId != null)
                usersDb.child("$userId/userLocation").setValue(location).addOnSuccessListener {
                    Log.e(homeFragTag, "successfully location updated!!!")
                }
        }catch (e: Exception){
            Log.e(homeFragTag, e.message!!)
        }
    }

    fun getUserLocation(uid: String){
        try {
            usersDb.child("$uid/userLocation").addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError) { Log.e(homeFragTag, p0.message) }
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        try{
                            findDonarInterFace?.returnLocationString(p0.getValue(Loc::class.java)!!)
                            Log.e(homeFragTag, p0.getValue(Loc::class.java)!!.latitude.toString())
                        }catch (e: Exception){
                            Log.e(homeFragTag, e.message.toString())
                        }
                    }
                }
            })
        }catch (e: Exception){
            Log.e(homeFragTag, e.message!!)
        }
    }

    fun getUserByUid(uid: String){
        try {
            usersDb.child("$uid/userData").addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) { Log.e(homeFragTag, p0.message) }
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()){
                        try {
                            findDonarInterFace?.returnUserByUid(p0.getValue(UserData::class.java)!!)
                            Log.e(homeFragTag, "${p0.getValue(UserData::class.java)!!.email} found User by UID!!!")
                        }catch (e: Exception){
                            Log.e(homeFragTag, e.message!!)
                        }
                    }
                }
            })
        }catch (e: Exception){
            Log.e(homeFragTag, e.message!!)
        }
    }

}