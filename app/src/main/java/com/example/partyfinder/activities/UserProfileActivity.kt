package com.example.partyfinder.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.partyfinder.R
import com.example.partyfinder.firestore.FirestoreClass
import com.example.partyfinder.models.User
import com.example.partyfinder.utils.Constants
import com.example.partyfinder.utils.CustomButton
import com.example.partyfinder.utils.CustomEditText
import com.example.partyfinder.utils.CustomRadioButton
import com.example.partyfinder.utils.GlideLoader
import java.io.IOException

@Suppress("DEPRECATION")
class UserProfileActivity : BaseActivity(), View.OnClickListener {

    private lateinit var tbUserProfile: androidx.appcompat.widget.Toolbar
    private lateinit var etFirstName: CustomEditText
    private lateinit var etLastName: CustomEditText
    private lateinit var etEmail: CustomEditText
    private lateinit var ivUserPhoto: ImageView
    private lateinit var etPhoneNumber: CustomEditText
    private lateinit var btnSave: CustomButton
    private lateinit var rbMale: CustomRadioButton

    private lateinit var mUserDetails: User
    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        tbUserProfile = findViewById(R.id.toolbar_user_profile_activity)
        etFirstName = findViewById(R.id.et_first_name)
        etLastName = findViewById(R.id.et_last_name)
        etEmail = findViewById(R.id.et_email)
        ivUserPhoto = findViewById(R.id.iv_user_photo)
        etPhoneNumber = findViewById(R.id.et_phone_number)
        btnSave = findViewById(R.id.btn_submit)
        rbMale = findViewById(R.id.rb_male)

        setupActionBar()

        // TODO: update page title accordingly -> "Profile" / "Edit Profile"
        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        etFirstName.isEnabled = false
        etFirstName.setText(mUserDetails.firstName)

        etLastName.isEnabled = false
        etLastName.setText(mUserDetails.lastName)

        etEmail.isEnabled = false
        etEmail.setText(mUserDetails.email)

        ivUserPhoto.setOnClickListener(this@UserProfileActivity)
        btnSave.setOnClickListener(this@UserProfileActivity)
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

                R.id.btn_submit -> {

                    if (validateUserProfileDetails()) {

                        showProgressDialog(resources.getString(R.string.please_wait))

                        if (mSelectedImageFileUri != null) {

                            FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri)
                        } else {

                            updateUserProfileDetails()
                        }
                    }
                }
            }
        }
    }

    private fun updateUserProfileDetails() {

        val userHashMap = HashMap<String, Any>()

        val phoneNumber = etPhoneNumber.text.toString().trim { it <= ' ' }

        val gender = if (rbMale.isChecked) {

            Constants.MALE
        } else {

            Constants.FEMALE
        }

        if (mUserProfileImageURL.isNotEmpty()) {

            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }

        if (phoneNumber.isNotEmpty()) {

            userHashMap[Constants.MOBILE] = phoneNumber.toLong()
        }

        userHashMap[Constants.GENDER] = gender

        userHashMap[Constants.COMPLETE_PROFILE] = 1

        // showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().updateUserProfileData(this, userHashMap)
    }

    fun userProfileUpdateSuccess() {

        hideProgressDialog()

        Toast.makeText(
            this@UserProfileActivity,
            resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_SHORT
        ).show()

        startActivity(Intent(this@UserProfileActivity, MainActivity::class.java))
        finish()
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

                        mSelectedImageFileUri = data.data!!

                        // ivUserPhoto.setImageURI(selectedImageFileUri)
                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, ivUserPhoto)
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

    private fun validateUserProfileDetails() : Boolean {

        return when {

            TextUtils.isEmpty(etPhoneNumber.text.toString().trim { it <= ' ' }) -> {

                showErrorSnackBar(resources.getString(R.string.err_msg_enter_phone_number), true)
                false
            } else -> {

                true
            }
        }
    }

    fun imageUploadSuccess(imageURL: String) {

        // hideProgressDialog()

        mUserProfileImageURL = imageURL

        updateUserProfileDetails()
    }
}