package com.example.partyfinder.ui.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.partyfinder.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.partyfinder.databinding.ActivityLocationPickerBinding
import com.example.partyfinder.utils.Constants
import com.google.android.gms.maps.model.MapStyleOptions

@Suppress("DEPRECATION")
class LocationPickerActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityLocationPickerBinding

    private lateinit var tbLocationPicker: androidx.appcompat.widget.Toolbar

    var mSelectedImageFileUri: Uri? = null
    var mPartyName: String? = null
    var mSelectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLocationPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.PARTY_DETAILS)) {

            val partyDetails = intent.getSerializableExtra(Constants.PARTY_DETAILS) as HashMap<*, *>
            mSelectedImageFileUri = Uri.parse(partyDetails[Constants.PARTY_IMAGE] as String)
            mPartyName = partyDetails[Constants.PARTY_NAME] as String
            mSelectedDate = partyDetails[Constants.PARTY_DATE] as String
        }

        tbLocationPicker = findViewById(R.id.toolbar_location_picker_activity)

        setupActionBar()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val mapStyleOptions = MapStyleOptions.loadRawResourceStyle(this@LocationPickerActivity, R.raw.map_style)
        googleMap.setMapStyle(mapStyleOptions)

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private fun setupActionBar() {

        setSupportActionBar(tbLocationPicker)

        val actionBar = supportActionBar
        if (actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24)
            actionBar.setDisplayShowTitleEnabled(false)
        }

        tbLocationPicker.setNavigationOnClickListener { onBackPressed() }
    }
}