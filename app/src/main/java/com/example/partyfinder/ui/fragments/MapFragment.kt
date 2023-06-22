package com.example.partyfinder.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.partyfinder.R
import com.example.partyfinder.firestore.FirestoreClass
import com.example.partyfinder.models.Party
import com.example.partyfinder.ui.activities.DashboardActivity
import com.example.partyfinder.utils.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment() {

    private lateinit var parentActivity: DashboardActivity

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var map: GoogleMap

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->

        map = googleMap

        val mapStyleOptions = context?.let { MapStyleOptions.loadRawResourceStyle(it, R.raw.map_style) }
        googleMap.setMapStyle(mapStyleOptions)

        // val sydney = LatLng(-34.0, 151.0)
        // googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 1.0f))

        if (isLocationPermissionGranted()) {

            googleMap.isMyLocationEnabled = true
            zoomToCurrentLocation()
        } else {

            requestLocationPermission()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        parentActivity = activity as DashboardActivity

        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(parentActivity)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        FirestoreClass().getParties(parentActivity, this)
    }

    fun successPartiesList(partyList: ArrayList<Party>) {

        if (partyList.size > 0) {

            for (i in partyList) {

                val latLng = LatLng(i.latitude, i.longitude)
                map.addMarker(MarkerOptions().position(latLng).title(i.name))
            }
        } else {

            Toast.makeText(
                requireActivity(),
                "No parties found.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()

        parentActivity.tvTitle.text = resources.getString(R.string.title_map_fragment)
    }

    private fun isLocationPermissionGranted(): Boolean {

        return ContextCompat.checkSelfPermission(
            parentActivity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {

        ActivityCompat.requestPermissions(
            parentActivity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            Constants.LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    @SuppressLint("MissingPermission")
    fun zoomToCurrentLocation() {

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f))
            }
        }
    }
}