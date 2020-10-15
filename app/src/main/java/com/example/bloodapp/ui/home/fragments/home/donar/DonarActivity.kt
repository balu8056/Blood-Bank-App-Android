package com.example.bloodapp.ui.home.fragments.home.donar

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isGone
import com.example.bloodapp.R
import com.example.bloodapp.util.donarArray
import com.example.bloodapp.util.toast
import com.example.bloodapp.util.visibilityGone
import com.example.bloodapp.util.visibilityOn
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_donar.*

class DonarActivity : AppCompatActivity() {

    private var donarUid : String? = null

    private var clipBoard: ClipboardManager? = null

    private var target: Target? = object : Target{
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
        override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {}
        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            viewingDonarImage.setImageBitmap(bitmap)
            viewingImage2.setImageBitmap(bitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donar)

        clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val stringArray = intent.extras?.getStringArrayList(donarArray)
        try { Picasso.get().load(stringArray?.get(0)).into(target!!)
        }catch (e: Exception){ Log.e("DonarActivity", e.message.toString()) }
        viewingDonarName.text = stringArray?.get(1)
        viewingDonarMobile.text = stringArray?.get(2)
        viewingDonarBloodGroup.text = stringArray?.get(3)
        viewingDonarEmail.text = stringArray?.get(4)

        donarUid = stringArray?.get(5).toString()

        viewingDonarName.setOnClickListener {
            val copy = ClipData.newPlainText("uidCopy", donarUid)
            clipBoard!!.setPrimaryClip(copy)
            toast("Copied!!!")
        }

        viewingDonarImage.setOnClickListener {
            if (viewingImageContainer.isGone) viewingImageContainer.visibilityOn()
            else viewingImageContainer.visibilityGone()
        }

        viewingCallDonar.setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${stringArray?.get(2)}")
            })
        }

        viewingMessageDonar.setOnClickListener {
            startActivity(Intent.createChooser(Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("sms:${stringArray?.get(2)}")
            }, "Message via..."))
        }

        emailDonar.setOnClickListener {
            startActivity(Intent.createChooser(Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:${stringArray?.get(4)}")
            }, "Send via.."))
        }
    }

    override fun onDestroy() {
        Picasso.get().cancelRequest(target!!)
        donarUid = null
        clipBoard = null
        target = null
        clearFindViewByIdCache()
        releaseInstance()
        super.onDestroy()
    }

}