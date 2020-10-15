package com.example.bloodapp

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.example.bloodapp.data.Loc
import com.example.bloodapp.data.repository.homerep.HomeFragmentRepository
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class LocService: Service(), KodeinAware {

    override val kodein: Kodein by kodein()
    private val homeRep by instance<HomeFragmentRepository>()

    private var locationManager: LocationManager? = null
    private var locationListener = object: LocationListener {
        override fun onLocationChanged(location: Location?) {
            Log.e("service", "got location")
            if (location!=null) setLatLon(location)
        }
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String?) {}
        override fun onProviderDisabled(provider: String?) {}
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            Log.e("service", "onStartCommand")
            locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let { setLatLon(it) }
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10.0F, locationListener)
        }catch (e: Exception){ stopSelf() }
    }

    private fun setLatLon(location: Location){
        Log.e("service", "setLatLog updated")
        homeRep.setUserLocation(Loc(location.latitude, location.longitude))
    }

    override fun onDestroy() {
        locationManager?.removeUpdates(locationListener)
        super.onDestroy()
    }

}