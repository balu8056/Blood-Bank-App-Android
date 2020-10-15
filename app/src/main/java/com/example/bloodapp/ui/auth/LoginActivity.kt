@file:Suppress("DEPRECATION")

package com.example.bloodapp.ui.auth

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.bloodapp.R
import com.example.bloodapp.databinding.ActivityLoginBinding
import com.example.bloodapp.ui.home.HomeActivity
import com.example.bloodapp.util.snack
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_login.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class LoginActivity : AppCompatActivity(), AuthInterface, KodeinAware {

    override val kodein: Kodein by kodein()
    private val factory : AuthFactoryClass by instance<AuthFactoryClass>()

    private var viewModel: AuthViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProviders.of(this, factory).get(AuthViewModel::class.java)
        binding.viewModel = viewModel
        viewModel?.authInterface = this

        loginTitle.typeface = Typeface.createFromAsset(assets, "fonts/pacifico.ttf")

        signupBtn.setOnClickListener {
            startActivity(Intent(applicationContext, SignupActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            })
            finish()
        }
    }

    private fun startHome(){
        startActivity(Intent(applicationContext, HomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
        finish()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel?.checkIfThereIsUser() != null)
            startHome()
    }

    override fun start() {
        loginProgress.visibility = View.VISIBLE
    }

    override fun mistake(msg: String) {
        loginProgress.visibility = View.INVISIBLE
        loginAct.snack(msg)
    }

    override fun currentUser(user: FirebaseUser?) {
        if (user != null)
            startHome()
    }

    override fun success(msg: String) {
        loginProgress.visibility = View.INVISIBLE
        loginAct.snack(msg)
    }

    override fun failure(msg: String) {
        loginProgress.visibility = View.INVISIBLE
        loginAct.snack(msg)
    }

    override fun onDestroy() {
        viewModel = null
        releaseInstance()
        clearFindViewByIdCache()
        super.onDestroy()
    }

}