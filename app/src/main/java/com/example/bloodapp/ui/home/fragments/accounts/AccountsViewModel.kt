package com.example.bloodapp.ui.home.fragments.accounts

import android.net.Uri
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bloodapp.data.UserData
import com.example.bloodapp.data.repository.auth.AuthenticationRepository
import com.example.bloodapp.ui.home.UserDetailInter
import com.example.bloodapp.util.CoroutinesUtil

class AccountsViewModel(private val authenticationRepository: AuthenticationRepository) : ViewModel(), UserDetailInter {

    var accountProfileImage: Uri? = null
    var accountName: String? = null
    var accountBloodGroup: String? = null
    var accountMobile: String? = null
    var accountEmail : String? = null
    var accountUid: String? = null

    var accountsInterFace: AccountsInterFace? = null
    private var oldAccountData: UserData? = null

    init {
        authenticationRepository.userDetailInterFace = this
    }

    private val _accountDataFromRep = MutableLiveData<UserData>()
    var accountDataFromRep: LiveData<UserData> = _accountDataFromRep

    fun getAccountDetail(){
        authenticationRepository.getUserDetail()
    }

    override fun giveData(userData: UserData) {
        oldAccountData = userData
        _accountDataFromRep.value = userData
        Log.e("Accounts", "${userData.name} from accounts fragment")
    }

    fun updateAccount(view: View){
        print(view)
        if (accountProfileImage == null) {
            accountsInterFace?.info("Profile is empty!!!")
            return
        }
        if (accountName.isNullOrEmpty()){
            accountsInterFace?.info("Name is empty!!!")
            return
        }
        if (accountBloodGroup.isNullOrEmpty()){
            accountsInterFace?.info("Blood is empty!!!")
            return
        }
        if (accountMobile.isNullOrEmpty()){
            accountsInterFace?.info("Mobile is empty!!!")
            return
        }

        val updateTo = UserData(accountProfileImage.toString(), accountName!!, accountBloodGroup!!, accountMobile!!, accountEmail!!, accountUid!!)
        if (oldAccountData?.imageUrl == updateTo.imageUrl && oldAccountData?.name == updateTo.name && oldAccountData?.bloodGroup == updateTo.bloodGroup && oldAccountData?.mobile == updateTo.mobile && oldAccountData?.email == updateTo.email)
            accountsInterFace?.info("Already up-to-date!!!")
        else {
            accountsInterFace?.info("Updating!!!")
            if(updateTo.imageUrl != oldAccountData?.imageUrl)
                CoroutinesUtil.main {
                    Log.e("AccTag", "update with photo")
                    val response = authenticationRepository.updateProfile(accountProfileImage!!)
                    response?.storage?.downloadUrl?.addOnSuccessListener { imageUri ->
                        CoroutinesUtil.main {
                            val res = authenticationRepository.updateUserDatabase(imageUri, updateTo)
                            if (res.isEmpty() || res == ""){
                                accountsInterFace?.info("Update finished.")
                                getAccountDetail()
                            } else
                                accountsInterFace?.info("update canceled, please try again later...")
                        }
                    }
                }
            else
                CoroutinesUtil.main {
                    Log.e("AccTag", "update Without photo")
                    val res = authenticationRepository.updateUserDatabase(oldAccountData?.imageUrl!!.toUri(), updateTo)
                    if (res.isEmpty() || res == ""){
                        accountsInterFace?.info("Update finished.")
                        getAccountDetail()
                    } else
                        accountsInterFace?.info("update canceled, please try again later...")
                }
        }
//        Log.e("AccountTag", "${oldAccountData?.imageUrl} == ${updateTo.imageUrl} \n ${oldAccountData.name} == ${updateTo.name} \n ${oldAccountData.bloodGroup} == ${updateTo.bloodGroup} \n ${oldAccountData.mobile} == ${updateTo.mobile} \n ${oldAccountData.email} == ${updateTo.email}")
    }

    fun resetAccountPass(){
        CoroutinesUtil.main {
            if (authenticationRepository.resetPassWordEmail())
                accountsInterFace?.info("Check your email!!!")
            else
                accountsInterFace?.info("Something went wrong, please try again later!!!")
        }
    }

    override fun onCleared() {
        accountProfileImage = null
        accountName = null
        accountUid = null
        accountBloodGroup = null
        accountEmail = null
        accountMobile = null
        
        accountsInterFace = null
        oldAccountData = null
        super.onCleared()
    }

}