package com.example.partyfinder.ui.activities

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.partyfinder.R
import com.example.partyfinder.ui.fragments.MapFragment
import com.example.partyfinder.utils.Constants

class DashboardActivity : BaseActivity() {

    private lateinit var tbDashboard: androidx.appcompat.widget.Toolbar
    lateinit var tvTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        tbDashboard = findViewById(R.id.toolbar_dashboard_activity)
        tvTitle = findViewById(R.id.tv_title)

        setupActionBar()

        val navController = findNavController(R.id.nav_host_fragment_activity_dashboard)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_map, R.id.navigation_new_party, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    @Deprecated("Deprecated in Java", ReplaceWith("doubleBackToExit()"))
    override fun onBackPressed() {

        doubleBackToExit()
    }

    private fun setupActionBar() {

        setSupportActionBar(tbDashboard)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
        }
    }

    // TODO: Check missing permissions, refactor code

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST_CODE) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, update the map

                val mapFragment = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_dashboard)
                    ?.childFragmentManager?.fragments?.get(0) as MapFragment?)

                mapFragment?.let {

                    it.map.isMyLocationEnabled = true
                    it.zoomToCurrentLocation()
                }
            } else {

                Toast.makeText(
                    this,
                    resources.getString(R.string.location_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}