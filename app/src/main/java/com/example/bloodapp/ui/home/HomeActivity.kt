@file:Suppress("DEPRECATION")

package com.example.bloodapp.ui.home

import android.content.*
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.bloodapp.R
import com.example.bloodapp.data.UserData
import com.example.bloodapp.ui.auth.LoginActivity
import com.example.bloodapp.util.checkInternetIsAvail
import com.example.bloodapp.util.snack
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class HomeActivity: AppCompatActivity(), KodeinAware, ForSettingUserData{

    override val kodein: Kodein by kodein()
    private val factory : HomeFactoryClass by instance<HomeFactoryClass>()

    private var appBarConfiguration: AppBarConfiguration? = null
    private var homeViewModel: HomeViewModel? = null
    private var drawerLayout: DrawerLayout? = null
    private var navView: NavigationView? = null

    private var netAvailReceiver:BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (!checkInternetIsAvail()) drawer_layout.snack("please check network!!!", Snackbar.LENGTH_INDEFINITE)
            else drawer_layout.snack("please wait!!!")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        homeViewModel = ViewModelProviders.of(this, factory).get(HomeViewModel::class.java)
        homeViewModel?.forSettingUserData = this

        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_home, R.id.nav_gallery, R.id.nav_accounts), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration!!)
        navView?.setupWithNavController(navController)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout?.setDrawerListener(toggle)

        homeViewModel?.getUserDetailsFromRepository()

        //check Internet
        registerReceiver(netAvailReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuSignOut -> {
                AlertDialog.Builder(this)
                    .setTitle("Are you sure!!!")
                    .setMessage("Do you want to sign out?")
                    .setCancelable(false)
                    .setPositiveButton("Sign out") { dialogInterface: DialogInterface, i: Int ->
                        print("$dialogInterface $i")
                        if (homeViewModel?.signOut()!!) {
                            startActivity(Intent(applicationContext, LoginActivity::class.java).also { intent ->
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            })
                            finish()
                        }
                        dialogInterface.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int ->
                        print("$i")
                        dialogInterface.dismiss()
                    }.show()
            }
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration!!) || super.onSupportNavigateUp()
    }

    override fun onResume() {
        navView?.menu?.getItem(2)?.isCheckable = false
        navView?.checkedItem?.isChecked = true
        super.onResume()
    }

    override fun setUserData(userData: UserData) {
        val headerLayout = navView?.getHeaderView(0)
        val profileImage = headerLayout?.findViewById<ImageView>(R.id.homeUserImage)
        val userName = headerLayout?.findViewById<TextView>(R.id.homeUserName)
        val userEmail = headerLayout?.findViewById<TextView>(R.id.homeUserEmail)
        try {
            Picasso.get().load(userData.imageUrl).into(profileImage)
        }catch (e: Exception){
            e.printStackTrace()
        }
        userName?.text = userData.name
        userEmail?.text = userData.email
    }

    override fun onDestroy() {
        unregisterReceiver(netAvailReceiver)
        appBarConfiguration = null
        homeViewModel = null
        drawerLayout = null
        navView = null
        netAvailReceiver = null
        releaseInstance()
        clearFindViewByIdCache()
        super.onDestroy()
    }

}
