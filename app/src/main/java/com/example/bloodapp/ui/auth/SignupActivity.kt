@file:Suppress("DEPRECATION")

package com.example.bloodapp.ui.auth

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.bloodapp.R
import com.example.bloodapp.databinding.ActivitySignupBinding
import com.example.bloodapp.util.bloodGroup
import com.example.bloodapp.util.snack
import com.example.bloodapp.util.toast
import com.google.firebase.auth.FirebaseUser
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_signup.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class SignupActivity : AppCompatActivity(), AuthInterface, KodeinAware {

    override val kodein: Kodein by kodein()
    private val factory : AuthFactoryClass by instance<AuthFactoryClass>()

    private var viewModel: AuthViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivitySignupBinding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        binding.viewModel = viewModel
        viewModel?.authInterface = this

        signup_title.typeface = Typeface.createFromAsset(assets, "fonts/pacifico.ttf")

        val arrayBloodGroup = bloodGroup
        signupBloodGroup.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position != 0) viewModel?.signUpBloodGroup = arrayBloodGroup[position]
                else viewModel?.signUpBloodGroup = null
            }
        }

        signupImage.setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_PICK).apply {
                this.type = "image/*"
            }, 1234)
        }

        login.setOnClickListener {
            startActivity(Intent(applicationContext, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
            finish()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1234){
            try {
                CropImage.activity(data!!.data)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this)
            }catch (e: Exception){ toast("image not supported!!!") }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            try {
                val imageFromCropImage = CropImage.getActivityResult(data).uri
                signupImage.setImageURI(imageFromCropImage)
                viewModel?.signUpImage = imageFromCropImage
            }catch (e: Exception){ toast("unsupported image!!!") }
        }
    }

    override fun start() {
        signupProgress.visibility = View.VISIBLE
    }

    override fun mistake(msg: String) {
        signupProgress.visibility = View.INVISIBLE
        signupAct.snack(msg)
    }

    override fun currentUser(user: FirebaseUser?) {
        toast(user.toString())
    }

    override fun success(msg: String) {
        signupProgress.visibility = View.INVISIBLE
        signupAct.snack(msg)
    }

    override fun failure(msg: String) {
        signupProgress.visibility = View.INVISIBLE
        signupAct.snack(msg)
    }

    override fun onDestroy() {
        viewModel = null
        releaseInstance()
        clearFindViewByIdCache()
        super.onDestroy()
    }

}
