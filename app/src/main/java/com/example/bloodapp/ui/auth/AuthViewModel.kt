package com.example.bloodapp.ui.auth

import android.net.Uri
import android.util.Log
import android.view.View
import androidx.lifecycle.*
import com.example.bloodapp.data.repository.auth.AuthenticationRepository
import com.example.bloodapp.util.CoroutinesUtil
import com.google.firebase.auth.FirebaseUser

const val AuthViewModelTag = "AuthViewModelTag"

class AuthViewModel(private val authenticationRepository: AuthenticationRepository): ViewModel() {

    var authInterface: AuthInterface? = null

    // sign up fields
    var signUpImage: Uri? = null
    var signUpName: String? = null
    var signUpBloodGroup: String? = null
    var signUpMobile: String? = null

    // signUpEmailPass and LoginEmailPass
    var email : String? = null
    var pass: String? = null

    fun signUpBtn(view: View) {
        Log.e(AuthViewModelTag, "$view")
        authInterface?.start()

        if (signUpImage == null) {
            authInterface?.mistake("Set your image!!!")
            return
        }
        if (signUpName.isNullOrEmpty()) {
            authInterface?.mistake("Name is empty!!!")
            return
        }
        if (signUpBloodGroup.isNullOrEmpty()) {
            authInterface?.mistake("Select your blood group!!!")
            return
        }
        if (signUpMobile.isNullOrEmpty()) {
            authInterface?.mistake("Mobile number is empty!!!")
            return
        }
        if (email.isNullOrEmpty()) {
            authInterface?.mistake("Email is empty!!!")
            return
        }
        if (pass.isNullOrEmpty()) {
            authInterface?.mistake("Password is empty!!!")
            return
        }

        CoroutinesUtil.main {
            val response = authenticationRepository.signUp(email!!, pass!!)
            if (response.first != null){
                CoroutinesUtil.main {
                    val responseImageUpload = authenticationRepository.signUpImageUpload(response.first!!, signUpImage!!)
                    if (responseImageUpload.first != null)
                        CoroutinesUtil.main {
                            val responseReadyUrl = authenticationRepository.signUpGetReadyImageUrl(responseImageUpload.first!!)
                            if (responseReadyUrl.first != null)
                                CoroutinesUtil.main {
                                    val dataUploaded = authenticationRepository.signUpUploadDatabase(responseReadyUrl.first.toString(), signUpName!!, signUpBloodGroup!!, signUpMobile!!, email!!, response.first!!.user?.uid!!)
                                    if (dataUploaded.isEmpty())
                                        authInterface?.success("Successfully created!!!")
                                    else
                                        authInterface?.failure("Error while creating account!!!")
                                }
                            else
                                authInterface?.failure(responseReadyUrl.second.toString())
                        }
                    else
                        authInterface?.failure(responseImageUpload.second.toString())
                }
            }else
                authInterface?.failure(response.second.toString())
        }
    }

    fun loginBtn(view: View) {
        Log.e(AuthViewModelTag, "$view")
        authInterface?.start()

        if (email.isNullOrEmpty()) {
            authInterface?.mistake("Email is empty!!!")
            return
        }
        if (pass.isNullOrEmpty()) {
            authInterface?.mistake("Password is empty!!!")
            return
        }

        CoroutinesUtil.main {
            val response = authenticationRepository.login(email!!, pass!!)
            if (response.first != null) {
                authInterface?.success(response.second!!)
                authInterface?.currentUser(authenticationRepository.checkUserIsThere())
            } else authInterface?.failure(response.second!!)
        }
    }

    fun checkIfThereIsUser(): FirebaseUser? {
        return authenticationRepository.checkUserIsThere()
    }

    override fun onCleared() {
        signUpBloodGroup = null
        signUpMobile = null
        signUpName = null
        signUpImage = null
        email = null
        pass = null
        authInterface = null
        super.onCleared()
    }

}