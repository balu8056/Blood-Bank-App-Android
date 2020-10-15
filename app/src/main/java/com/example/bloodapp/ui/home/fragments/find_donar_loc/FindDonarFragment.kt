@file:Suppress("DEPRECATION")

package com.example.bloodapp.ui.home.fragments.find_donar_loc

import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.bloodapp.R
import com.example.bloodapp.data.Loc
import com.example.bloodapp.data.UserData
import com.example.bloodapp.util.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_find_donar.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

const val findDonarTag = "findDonarTag"

@RequiresApi(Build.VERSION_CODES.Q)
class FindDonarFragment : Fragment(), KodeinAware , OnMapReadyCallback, FindDonarInterFace2{
    private var mMap: GoogleMap? = null
    private var map: SupportMapFragment? = null

    override val kodein: Kodein by kodein()
    private val findDonarFactory : FindDonarFactory by instance<FindDonarFactory>()
    private var findDonarViewModel: FindDonarViewModel? = null

    private var imageFound: ImageView? = null
    private var nameFound: TextView? = null
    private var mobileFound: TextView? = null
    private var bloodFound: Button? = null

    private var clipManager: ClipboardManager? = null

    private var foundUserName: String? = null
    private var foundUserUid: String? = null
    private var isTrackingDonar= false

    private var latLng: LatLng? = null
    private var handlerFor10Sec = Handler()
    private var runHandler = object : Runnable{
        override fun run() {
            findDonarViewModel?.getUserLocation(foundUserUid!!)
            handlerFor10Sec.postDelayed(this, 10000)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        findDonarViewModel = ViewModelProviders.of(this, findDonarFactory).get(FindDonarViewModel::class.java)
        findDonarViewModel!!.findDonarInterFace2 = this
        return inflater.inflate(R.layout.fragment_find_donar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clipManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        imageFound = findDonarFoundDonar.findViewById(R.id.userListImage)
        nameFound = findDonarFoundDonar.findViewById(R.id.userListName)
        mobileFound = findDonarFoundDonar.findViewById(R.id.userListMobile)
        bloodFound = findDonarFoundDonar.findViewById(R.id.userListBloodGroup)

        map = childFragmentManager.findFragmentById(R.id.findDonarMap) as SupportMapFragment
        map!!.getMapAsync(this)


        val abc = clipManager!!.primaryClip
        if (abc != null) {
            val copiedText = abc.getItemAt(0)
            findDonarUid.setText(copiedText?.text.toString())
        }

        findDonarFindBtn.setOnClickListener {
            if (findDonarUid.text.isNotEmpty()) {
                findDonarViewModel?.getUserByUid(findDonarUid.text.toString())
                isTrackingDonar = false
            } else
                context?.toast("Donar id is empty!!!")
        }

        track.setOnClickListener {
            trackDonar()
        }
        unTrack.setOnClickListener {
            unTrackDonar()
        }
        cancelFoundDonar.setOnClickListener {
            cancelDonarAndLocation()
        }

        floatingFullScreen.setOnClickListener {
            findDonarRelative1.visibilityGone()
            if (foundUserName != null)
                foundDonarRel.visibilityGone()
            floatingFullScreen.visibilityGone()
            floatingExitFullScreen.visibilityOn()
        }
        floatingExitFullScreen.setOnClickListener {
            findDonarRelative1.visibilityOn()
            if (foundUserName != null)
                foundDonarRel.visibilityOn()
            floatingFullScreen.visibilityOn()
            floatingExitFullScreen.visibilityGone()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    override fun returnUser(userData: UserData) {
        Log.e(findDonarTag, userData.name)
        try { Picasso.get().load(userData.imageUrl).into(imageFound)
        }catch (e: Exception){e.printStackTrace()}
        nameFound?.text = userData.name
        mobileFound?.text = userData.mobile
        bloodFound?.text = userData.bloodGroup
        foundUserName = userData.name
        foundUserUid = userData.uid
        foundDonarRel.visibilityOn()
        findDonarFoundDonar.setOnClickListener {
            selectAfterDonar()
            findDonarViewModel?.getUserLocation(userData.uid)
        }
    }

    override fun returnLocation(location: Loc) {
        Log.e(findDonarTag, "Location came to FindDonar")
        mMap?.clear()
        latLng = LatLng(location.latitude, location.longitude)
        mMap?.addMarker(MarkerOptions().position(latLng!!).title(foundUserName))?.showInfoWindow()
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0F))
        findDonarMapProgress.visibilityGone()
    }

    private fun cancelHandler(){
        if (handlerFor10Sec.hasCallbacks(runHandler))
            handlerFor10Sec.removeCallbacks(runHandler)
    }

    private fun cancelDonarAndLocation(){
        foundDonarRel.visibilityGone()
        cancelFoundDonar.visibilityGone()
        trackingRel.visibilityGone()

        foundUserName = null
        foundUserUid = null
        unTrackDonar()
        mMap?.clear()
    }

    private fun selectAfterDonar(){
        cancelFoundDonar.visibilityOn()
        trackingRel.visibilityOn()

        findDonarFindBtn.enabledFalse()
        findDonarMapProgress.visibilityOn()
    }

    private fun trackDonar(){
        handlerFor10Sec.postDelayed(runHandler,0)
        unTrack.visibilityOn()
        track.visibilityGone()
        findDonarFoundDonar.enabledFalse()
        context?.toast("Tracking Enabled!!!")
    }

    private fun unTrackDonar(){
        cancelHandler()
        unTrack.visibilityGone()
        track.visibilityOn()
        findDonarFoundDonar.enabledTrue()
        findDonarFindBtn.enabledTrue()
        context?.toast("Tracking Disabled!!!")
    }

    override fun onDestroy() {
        cancelHandler()
        map?.onDestroy()
        findDonarViewModel = null
        mMap = null
        map = null
        imageFound = null
        nameFound = null
        mobileFound = null
        bloodFound = null
        clipManager = null
        foundUserName = null
        foundUserUid = null
        latLng = null
        clearFindViewByIdCache()
        super.onDestroy()
    }

}
