@file:Suppress("DEPRECATION")

package com.example.bloodapp.ui.home.fragments.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bloodapp.LocService
import com.example.bloodapp.R
import com.example.bloodapp.data.UserData
import com.example.bloodapp.ui.home.fragments.home.donar.DonarActivity
import com.example.bloodapp.util.bloodGroup
import com.example.bloodapp.util.donarArray
import com.example.bloodapp.util.visibilityGone
import com.example.bloodapp.util.visibilityOn
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

const val homeFragmentTag = "homeFragmentTag"
const val locationReqCode = 1234

class CustomAdapterForRecycler(private val array: MutableList<UserData>): RecyclerView.Adapter<CustomAdapterForRecycler.ViewHolder>(){
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val image : ImageView = itemView.findViewById(R.id.userListImage)
        val name : TextView = itemView.findViewById(R.id.userListName)
        val mobile: TextView = itemView.findViewById(R.id.userListMobile)
        val bloodGroup: Button = itemView.findViewById(R.id.userListBloodGroup)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.user_item_list, parent, false))
    }
    override fun getItemCount(): Int {
        return array.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = array[position]
        try{ Picasso.get().load(user.imageUrl).resize(50, 50).into(holder.image)
        }catch(e: Exception){ Log.e(homeFragmentTag, e.message.toString()) }
        holder.name.text = user.name
        holder.mobile.text = user.mobile
        holder.bloodGroup.text = user.bloodGroup
        holder.itemView.setOnClickListener {
            it.context.startActivity(Intent(it.context, DonarActivity::class.java).apply {
                putExtra(donarArray, arrayListOf(user.imageUrl, user.name, user.mobile, user.bloodGroup, user.email, user.uid))
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            })
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        recyclerView.clearFindViewByIdCache()
        super.onDetachedFromRecyclerView(recyclerView)
    }
}

class HomeFragment : Fragment(), KodeinAware, HomeFragmentViewInter {

    override val kodein: Kodein by kodein()
    private val homeFragmentFactory by instance<HomeFragmentFactory>()

    private val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    private var homeFragmentViewModel: HomeFragmentViewModel? = null

    private val usersList = mutableListOf<UserData>()
    private val tempUserList = mutableListOf<UserData>()

    private var locationManager: LocationManager? = null
    private var hasGps = false

    private val onGpsChangeReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) refresh()
        }
    }
/*
    private var locationListener = object: LocationListener{
        override fun onLocationChanged(location: Location?) {
            if (location != null) setLatLon(location)
        }
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String?) {}
        override fun onProviderDisabled(provider: String?) {}
    }

 */

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeFragmentViewModel = ViewModelProviders.of(this, homeFragmentFactory).get(HomeFragmentViewModel::class.java)
        homeFragmentViewModel?.homeFragmentViewInter = this
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerViewUser.layoutManager = layoutManager
        recyclerViewUser.adapter = CustomAdapterForRecycler(usersList)

        homeSearchBlood.setOnClickListener {
            val pos = homeBloodSpinner.selectedItemPosition
            if (pos == 0) updateInAdapter(null)
            else updateInAdapter(bloodGroup[pos])
        }

        locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        requireContext().registerReceiver(onGpsChangeReceiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION).apply {
            addAction(Intent.ACTION_PROVIDER_CHANGED)
        })

    }

    private fun updateInAdapter(str: String?) {
        homeFProgress.visibilityOn()
        usersList.clear()
        if (!str.isNullOrEmpty())
            tempUserList.forEach {
                if (it.bloodGroup == str)
                    usersList.add(it)
            }
        else usersList.addAll(tempUserList)
        recyclerViewUser.adapter!!.notifyDataSetChanged()
        homeFProgress.visibilityGone()
    }

    override fun userList(users: UserData) {
        usersList.add(users)
        tempUserList.add(users)
        recyclerViewUser.adapter!!.notifyDataSetChanged()
        homeFProgress.visibilityGone()
    }

    private fun gettingUpdate(){
        usersList.clear()
        tempUserList.clear()
        homeFProgress.visibilityOn()
        homeFragmentViewModel?.requestForUserList()
    }

    private fun refresh(){
        AlertDialog.Builder(requireContext())
            .setTitle("Refresh")
            .setMessage("Click to refresh")
            .setPositiveButton("Ok"){ dialog2: DialogInterface?, _: Int ->
                dialog2?.dismiss()
                check()
            }
            .setCancelable(false)
            .show()
    }

    private fun check(){
        if (!hasGpsNetEnabled()){
            AlertDialog.Builder(requireContext())
                            .setTitle("GPS")
                .setMessage("Enable location to continue...")
                .setPositiveButton("Ok"){ dialog: DialogInterface?, _: Int ->
                    dialog?.dismiss()
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    refresh()
                }.setNegativeButton("Cancel") {dialog: DialogInterface?, _: Int ->
                    dialog?.dismiss()
                }
                .setCancelable(false)
                .show()
        }
    }

    private fun hasGpsNetEnabled(): Boolean{
        hasGps = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!
        return if (hasGps){
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), permission,locationReqCode)
            }else
                requireContext().startService(Intent(requireContext(), LocService::class.java))
            /*
            else{
                getLocalLocation()
                locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0.0F, locationListener)
            }
             */
            true
        }else
            false
    }

/*
    private fun setLatLon(location: Location){
        homeFragmentViewModel?.setUserLocation(location)
    }

    @SuppressLint("MissingPermission")
    private fun getLocalLocation(){
        val localGpsLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (localGpsLocation != null) setLatLon(localGpsLocation)
    }

 */

    override fun onStart() {
        check()
        gettingUpdate()
        super.onStart()
    }

    override fun onDestroy() {
        homeFragmentViewModel = null
        requireContext().stopService(Intent(requireContext(), LocService::class.java))
        //locationManager?.removeUpdates(locationListener)
        locationManager = null
        requireContext().unregisterReceiver(onGpsChangeReceiver)
        clearFindViewByIdCache()
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            locationReqCode ->{
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    check()
                else Log.e("permission denied" , "$permissions")
            }
        }
    }

}