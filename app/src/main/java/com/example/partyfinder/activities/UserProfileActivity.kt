package com.example.partyfinder.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.partyfinder.R
import com.example.partyfinder.models.User
import com.example.partyfinder.utils.Constants
import com.example.partyfinder.utils.CustomEditText
import com.example.partyfinder.utils.GlideLoader
import java.io.IOException

@Suppress("DEPRECATION")
class UserProfileActivity : BaseActivity(), View.OnClickListener {

    private lateinit var tbUserProfile: androidx.appcompat.widget.Toolbar
    private lateinit var etFirstName: CustomEditText
    private lateinit var etLastName: CustomEditText
    private lateinit var etEmail: CustomEditText
    private lateinit var ivUserPhoto: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        tbUserProfile = findViewById(R.id.toolbar_user_profile_activity)
        etFirstName = findViewById(R.id.et_first_name)
        etLastName = findViewById(R.id.et_last_name)
        etEmail = findViewById(R.id.et_email)
        ivUserPhoto = findViewById(R.id.iv_user_photo)

        setupActionBar()

        var userDetails = User()
        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            userDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        etFirstName.isEnabled = false
        etFirstName.setText(userDetails.firstName)

        etLastName.isEnabled = false
        etLastName.setText(userDetails.lastName)

        etEmail.isEnabled = false
        etEmail.setText(userDetails.email)

        ivUserPhoto.setOnClickListener(this@UserProfileActivity)
    }

    private fun setupActionBar() {

        setSupportActionBar(tbUserProfile)

        val actionBar = supportActionBar
        if (actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24)
            actionBar.setDisplayShowTitleEnabled(false)
        }

        tbUserProfile.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(v: View?) {

        if (v != null) {

            when (v.id) {

                R.id.iv_user_photo -> {

                    // Here we will check if the permission is already allowed or id we need to ask for it.
                    // If the permission is already allowed then the boolean value returned will be true else false.
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        // showErrorSnackBar("Storage permission already granted.", false)
                        Constants.showImagePicker(this)
                    } else {

                        // Request permissions to be granted to this application at runtime.
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

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
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {

                if (data != null) {

                    try {

                        val selectedImageFileUri = data.data!!

                        // ivUserPhoto.setImageURI(selectedImageFileUri)
                        GlideLoader(this).loadUserPicture(selectedImageFileUri, ivUserPhoto)
                    } catch (e: IOException) {

                        e.printStackTrace()
                        Toast.makeText(
                            this@UserProfileActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } else if (requestCode == Activity.RESULT_CANCELED) {

                // A log is printed when the user closes or cancels the image selection.
                Log.e("Request Canceled", "Image selection canceled")
        }
    }
}