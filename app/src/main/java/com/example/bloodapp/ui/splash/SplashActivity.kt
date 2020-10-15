package com.example.bloodapp.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AlphaAnimation
import android.view.animation.DecelerateInterpolator
import com.example.bloodapp.R
import com.example.bloodapp.ui.auth.LoginActivity
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    private var fadein: AlphaAnimation? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        fadein = AlphaAnimation(0F, 1F)
        fadein?.interpolator = DecelerateInterpolator()
        fadein?.duration = 2000
        fadein?.fillAfter = true
        splash_img.startAnimation(fadein)

        Handler().postDelayed({
            startActivity(Intent(applicationContext, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
            finish()
        }, 2000)
    }

    override fun onDestroy() {
        fadein = null
        releaseInstance()
        clearFindViewByIdCache()
        super.onDestroy()
    }

}