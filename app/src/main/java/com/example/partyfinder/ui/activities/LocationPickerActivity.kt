package com.example.partyfinder.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import com.example.partyfinder.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.partyfinder.databinding.ActivityLocationPickerBinding
import com.example.partyfinder.firestore.FirestoreClass
import com.example.partyfinder.models.Party
import com.example.partyfinder.utils.Constants
import com.example.partyfinder.utils.CustomTextView
import com.google.android.gms.maps.model.MapStyleOptions
import java.io.IOException
import java.lang.IndexOutOfBoundsException
import java.util.Locale

@Suppress("DEPRECATION")
class LocationPickerActivity : BaseActivity(), OnMapReadyCallback, GoogleMap.OnCameraIdleListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityLocationPickerBinding

    private lateinit var tbLocationPicker: androidx.appcompat.widget.Toolbar
    private lateinit var tvLocation: CustomTextView
    private lateinit var ivCheck: ImageView

    private var mSelectedImageFileUri: Uri? = null
    private var mPartyName: String? = null
    private var mSelectedDate: String? = null
    private var mPartyAddress: String? = null
    private var mLatitude: Double? = null
    private var mLongitude: Double? = null

    private var mPartyImageURL: String = ""

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
        tvLocation = findViewById(R.id.tv_address)
        ivCheck = findViewById(R.id.iv_check)

        setupActionBar()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        ivCheck.setOnClickListener { uploadParty() }
    }

    private fun uploadParty() {

        if (validatePartyAddress()) {

            showProgressDialog(resources.getString(R.string.please_wait))

            FirestoreClass().uploadImageToCloudStorage(this@LocationPickerActivity, mSelectedImageFileUri)
        }
    }

    fun imageUploadSuccess(imageURL: String) {

        mPartyImageURL = imageURL

        preparePartyUpload()
    }

    private fun preparePartyUpload() {

        val party = Party(
            mPartyName!!,
            mSelectedDate!!,
            mPartyImageURL,
            mPartyAddress!!,
            mLatitude!!,
            mLongitude!!,
            FirestoreClass().getCurrentUserID()
        )

        FirestoreClass().uploadParty(this@LocationPickerActivity, party)
    }

    fun partyCreationSuccess() {

        hideProgressDialog()

        val intent = Intent(this@LocationPickerActivity, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun validatePartyAddress() : Boolean {

        return when {

            mPartyAddress == null || mLatitude == null || mLongitude == null -> {

                showErrorSnackBar(resources.getString(R.string.err_msg_select_party_location), true)
                false
            }

            else -> {

                true
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val mapStyleOptions = MapStyleOptions.loadRawResourceStyle(this@LocationPickerActivity, R.raw.map_style)
        googleMap.setMapStyle(mapStyleOptions)

        mMap.setOnCameraIdleListener(this)

//        Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
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

    @SuppressLint("SetTextI18n")
    private fun setAddress(addresses: Address) {

        if (addresses.getAddressLine(0) != null) {

            tvLocation.text = addresses.getAddressLine(0)
        }

        if (addresses.getAddressLine(1) != null) {

            tvLocation.text = "${tvLocation.text}, ${addresses.getAddressLine(1)}"
        }
    }

    override fun onCameraIdle() {

        val addresses: List<Address>?
        val geocoder = Geocoder(this@LocationPickerActivity, Locale.getDefault())
        try {

            addresses = geocoder.getFromLocation(mMap.cameraPosition.target.latitude, mMap.cameraPosition.target.longitude, 1)

            setAddress(addresses!![0])

            mPartyAddress = addresses[0].getAddressLine(0)
            mLatitude = mMap.cameraPosition.target.latitude
            mLongitude = mMap.cameraPosition.target.longitude
        } catch (e: IndexOutOfBoundsException) {

            e.printStackTrace()
        } catch (e: IOException) {

            e.printStackTrace()
        }
    }
}