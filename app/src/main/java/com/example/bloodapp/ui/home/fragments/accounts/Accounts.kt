@file:Suppress("DEPRECATION")

package com.example.bloodapp.ui.home.fragments.accounts

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.bloodapp.R
import com.example.bloodapp.databinding.ActivityAccountsBinding
import com.example.bloodapp.ui.auth.LoginActivity
import com.example.bloodapp.util.*
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_accounts.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import kotlin.Exception

class Accounts : AppCompatActivity(), AccountsInterFace, KodeinAware {

    override val kodein: Kodein by kodein()
    private val accountsFactory: AccountsFactory by instance<AccountsFactory>()

    private var accountsViewModel: AccountsViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val accountsBinding: ActivityAccountsBinding = DataBindingUtil.setContentView(this, R.layout.activity_accounts)
        accountsViewModel = ViewModelProviders.of(this, accountsFactory).get(AccountsViewModel::class.java)
        accountsBinding.accountViewModel = accountsViewModel
        accountsViewModel!!.accountsInterFace = this

        accountBloodGroup.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long){
                if (position != 0) accountsViewModel!!.accountBloodGroup = bloodGroup[position]
                else accountsViewModel!!.accountBloodGroup = null
            }
        }

        accountImage.setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }, 1234)
        }

        accountsViewModel!!.getAccountDetail()

        accountProgress.visibilityOn()
        resetPass.enabledFalse()
        updateAccount.enabledFalse()
        accountsViewModel!!.accountDataFromRep.observe(this, Observer {
            try {
                Picasso.get().load(it.imageUrl).into(accountImage)
                accountName.setText(it.name)
                accountBloodGroup.setSelection(bloodGroup.indexOf(it.bloodGroup))
                accountMobileNumber.setText(it.mobile)
                accountEmail.setText(it.email)
                accountPass.setText(it.email)           // summa kachi

                accountsViewModel!!.accountProfileImage = it.imageUrl.toUri()
                accountsViewModel!!.accountBloodGroup = it.bloodGroup
                accountsViewModel!!.accountUid = it.uid
                accountProgress.visibilityGone()
                resetPass.enabledTrue()
                updateAccount.enabledTrue()
            }catch (e: Exception){
                toast("Image is unsupported!!!")
            }
        })

        resetPass.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Reset password")
                .setMessage("Confirm to send email...")
                .setCancelable(false)
                .setPositiveButton("Ok") {dialog, which ->
                    print(which)
                    accountsViewModel!!.resetAccountPass()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") {dialog, which ->
                    print(which)
                    dialog.dismiss()
                }.show()
        }
    }

    override fun info(msg: String) {
        updateAccount.snack(msg)
        if (msg == "Update finished.")
            startActivity(Intent(applicationContext, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1234 && resultCode == Activity.RESULT_OK){
            try {
                CropImage.activity(data!!.data)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this)
            }catch (e: Exception){ toast("image not supported!!!") }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            try {
                val croppedImage = CropImage.getActivityResult(data).uri
                accountImage.setImageURI(croppedImage)
                accountsViewModel?.accountProfileImage = croppedImage
            }catch (e: Exception){ toast("image not supported!!!") }
        }
    }

    override fun onDestroy() {
        releaseInstance()
        accountsViewModel = null
        clearFindViewByIdCache()
        super.onDestroy()
    }
}
