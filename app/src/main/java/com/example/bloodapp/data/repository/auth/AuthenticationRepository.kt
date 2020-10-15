package com.example.bloodapp.data.repository.auth

import android.net.Uri
import android.util.Log
import com.example.bloodapp.data.UserData
import com.example.bloodapp.ui.home.UserDetailInter
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.tasks.await
import java.util.*

const val AuthRepTag = "AuthRep"

class AuthenticationRepository{

    private var authLoginSignup = FirebaseAuth.getInstance()
    private var userDatabase = FirebaseDatabase.getInstance().getReference("User")
    private var userProfileStorage = FirebaseStorage.getInstance().getReference("/User/Profiles")

    //user detail interface
    var userDetailInterFace: UserDetailInter? = null

    suspend fun signUpUploadDatabase(signUpImage: String, signUpName: String, signUpBloodGroup: String, signUpMobile: String, signUpEmail: String, signUpUid: String): String {
        var isSuccess = ""
        return try {
            val userDetail = UserData(signUpImage, signUpName, signUpBloodGroup, signUpMobile, signUpEmail, signUpUid)
            userDatabase.child("${signUpUid}/userData").setValue(userDetail)
                .addOnSuccessListener {
                    isSuccess = ""
                    Log.e(AuthRepTag, "finishing up creating account")
                }.await()
            isSuccess
        }catch (e: Exception) {
            isSuccess = e.message.toString()
            isSuccess
        }
    }

    suspend fun signUpGetReadyImageUrl(uploadImage: UploadTask.TaskSnapshot): Pair<Uri?, String?> {
        return try {
            Pair(
                uploadImage.storage.downloadUrl
                    .addOnSuccessListener {
                }.await(),
                "URL is ready"
            )
        }catch (e: Exception){
            Log.e(AuthRepTag, "${e.message} while URL readying!!!")
            Pair(null, e.message.toString())
        }
    }


    suspend fun signUpImageUpload(authResult: AuthResult, image: Uri): Pair<UploadTask.TaskSnapshot?, String?>{
        val randomFileName = generateRandomString()
        return try {
            Pair(
                userProfileStorage.child("/${authResult.user?.uid}$randomFileName").putFile(image)
                    .addOnSuccessListener {
                        Log.e(AuthRepTag, "sign up image is uploaded!!!")
                    }.await(),
                "image Uploaded!!!"
            )
        }catch (e: Exception){
            Log.e(AuthRepTag, "${e.message} while uploading sign up image!!!")
            Pair(null, "${e.message} ")
        }
    }

    suspend fun signUp(email: String, pass: String): Pair<AuthResult?, String?>{
        return try {
            Pair(
                authLoginSignup.createUserWithEmailAndPassword(email, pass)
                    .addOnSuccessListener {
                        Log.e(AuthRepTag, "${it.user?.email} is Created!!!")
                    }.await(),
                "Successfully account created!!!"
            )
        }catch (e: Exception){
            Log.e(AuthRepTag, "${e.message} while creating account!!!")
            Pair(null, e.message.toString())
        }
    }

    suspend fun login(email: String, pass: String): Pair<AuthResult?, String?>{
        return try {
            Pair(
                authLoginSignup.signInWithEmailAndPassword(email, pass)
                    .addOnSuccessListener {
                        Log.e(AuthRepTag, "${it.user} logged in!!!")
                    }.await()
                    ,
                "Successfully logged in!!!")
        }catch (e: Exception){
            Pair(null, e.message.toString())
        }
    }

    fun checkUserIsThere(): FirebaseUser?{
        return try {
            authLoginSignup.currentUser
        }catch (e: Exception){
            Log.e(AuthRepTag, "${e.message} while checking logged or what")
            null
        }
    }

    fun getUserDetail(){
        try {
            val userId = getUserId()
            userDatabase.child("$userId/userData").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.e(AuthRepTag, "${p0.message} in getting User Details!!!")
                }
                override fun onDataChange(p0: DataSnapshot) {
                    Log.e(AuthRepTag, "${p0.getValue(UserData::class.java)?.email} from datachange getUSerDetail()")
                    if (p0.exists()) {
                        val p = p0.getValue(UserData::class.java)
                        userDetailInterFace?.giveData(p!!)
                    }
                }
            })
        }catch (e: Exception){
            Log.e(AuthRepTag, "${e.message} in getting user Details!!!")
        }
    }

    fun logOut(): Boolean{
        return try {
            authLoginSignup.signOut()
            true
        }catch (e: Exception){
            Log.e(AuthRepTag, "${e.message.toString()} while signOut!!!")
            false
        }
    }

    suspend fun resetPassWordEmail(): Boolean{
        var isEmailSend = true
        return try {
            authLoginSignup.sendPasswordResetEmail(getUserEmail())
                .addOnSuccessListener {
                    isEmailSend = true
                }.addOnFailureListener {
                    isEmailSend = false
                }.await()
            isEmailSend
        }catch (e: Exception){
            false
        }
    }

    suspend fun updateProfile(uri: Uri): UploadTask.TaskSnapshot?{
        return try {
            val userId = getUserId()
            val randomFileName = generateRandomString()
            userProfileStorage.child("/$userId$randomFileName").putFile(uri).addOnSuccessListener{
                Log.e(AuthRepTag, "Image updated !!!")
            }.await()
        }catch (e: Exception){
            null
        }
    }

    suspend fun updateUserDatabase(image: Uri, userData: UserData): String{
        var isUpdated = ""
        return try {
            val userId = getUserId()
            userData.imageUrl = image.toString()
            userDatabase.child("${userId}/userData").setValue(userData).addOnSuccessListener {
                Log.e(AuthRepTag, "successfully updated")
            }.addOnFailureListener {
                isUpdated = it.message.toString()
            }.await()
            isUpdated
        }catch (e: Exception){
            isUpdated = e.message.toString()
            isUpdated
        }
    }

    private fun getUserId(): String = authLoginSignup.currentUser?.uid!!

    private fun getUserEmail(): String = authLoginSignup.currentUser?.email!!

    private fun generateRandomString(): String = UUID.randomUUID().toString()

}