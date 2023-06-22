package com.example.partyfinder.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.partyfinder.R
import com.example.partyfinder.ui.fragments.DatePickerFragment
import com.example.partyfinder.ui.fragments.MapFragment
import com.example.partyfinder.ui.fragments.NewPartyFragment
import com.example.partyfinder.utils.Constants
import com.example.partyfinder.utils.GlideLoader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class DashboardActivity : BaseActivity(), DatePickerFragment.DateSelectionListener {

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

        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {

            // Permission granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // showErrorSnackBar("Storage permission granted.", false)
                Constants.showImagePicker(this)
            } else {

                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        if (requestCode == Constants.CAMERA_PERMISSION_CODE) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // showErrorSnackBar("Camera permission granted.", false)
                Constants.takePhoto(this)
            } else {

                Toast.makeText(
                    this,
                    resources.getString(R.string.camera_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDateSelected(year: Int, month: Int, day: Int) {

        val newPartyFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_dashboard)
            ?.childFragmentManager?.fragments?.get(0) as NewPartyFragment?

        val date = "$day/${month + 1}/$year"

        newPartyFragment?.let {

            it.mSelectedDate = date
            it.tvPartyDate.text = date
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {

                if (data != null) {

                    try {

                        val newPartyFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_dashboard)
                            ?.childFragmentManager?.fragments?.get(0) as NewPartyFragment?

                        newPartyFragment?.let {

                            it.mSelectedImageFileUri = data.data!!

                            GlideLoader(this).loadPicture(it.mSelectedImageFileUri!!, it.ivPartyPhoto)
                        }

                    } catch (e: IOException) {

                        e.printStackTrace()
                        Toast.makeText(
                            this@DashboardActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            if (requestCode == Constants.CAMERA_REQUEST_CODE) {

                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap

                try {

                    val newPartyFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_dashboard)
                        ?.childFragmentManager?.fragments?.get(0) as NewPartyFragment?

                    newPartyFragment?.let {

                            it.mSelectedImageFileUri = getBitmapUri(applicationContext, thumbnail)!!

                            GlideLoader(this).loadPicture(it.mSelectedImageFileUri!!, it.ivPartyPhoto)
                    }

                } catch (e: IOException) {

                    e.printStackTrace()
                    Toast.makeText(
                        this@DashboardActivity,
                        resources.getString(R.string.image_selection_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else if (requestCode == Activity.RESULT_CANCELED) {

            // A log is printed when the user closes or cancels the image selection.
            Log.e("Request Canceled", "Image selection canceled")
        }
    }

    private fun getBitmapUri(context: Context, bitmap: Bitmap): Uri? {

        var imageUri: Uri? = null

        try {

            val file = File(context.externalCacheDir, "temp_image.png")
            val outputStream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("png")
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "temp_image")
                put(MediaStore.Images.Media.MIME_TYPE, mimeType)
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/")
            }

            val contentResolver: ContentResolver = context.contentResolver
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {

                val newOutputStream = contentResolver.openOutputStream(uri)
                if (newOutputStream != null) {
                    newOutputStream.write(file.readBytes())
                    newOutputStream.close()
                    imageUri = uri
                }
            }

            // Notify the media scanner about the new image file
            MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), null) { _, _ -> }

        } catch (e: IOException) {

            e.printStackTrace()
        }
        return imageUri
    }
}