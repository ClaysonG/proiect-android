package com.example.partyfinder.ui.activities

import android.Manifest
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
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
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
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

@Suppress("DEPRECATION")
class EditUserProfileActivity : BaseActivity(), View.OnClickListener {

    private lateinit var tbUserProfile: androidx.appcompat.widget.Toolbar
    private lateinit var etFirstName: CustomEditText
    private lateinit var etLastName: CustomEditText
    private lateinit var etEmail: CustomEditText
    private lateinit var ivUserPhoto: ImageView
    private lateinit var etPhoneNumber: CustomEditText
    private lateinit var btnSave: CustomButton
    private lateinit var rbMale: CustomRadioButton
    private lateinit var rbFemale: CustomRadioButton

    private lateinit var mUserDetails: User
    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user_profile)

        tbUserProfile = findViewById(R.id.toolbar_edit_user_profile_activity)
        etFirstName = findViewById(R.id.et_first_name)
        etLastName = findViewById(R.id.et_last_name)
        etEmail = findViewById(R.id.et_email)
        ivUserPhoto = findViewById(R.id.iv_user_photo)
        etPhoneNumber = findViewById(R.id.et_phone_number)
        btnSave = findViewById(R.id.btn_submit)
        rbMale = findViewById(R.id.rb_male)
        rbFemale = findViewById(R.id.rb_female)

        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        etFirstName.setText(mUserDetails.firstName)
        etLastName.setText(mUserDetails.lastName)

        etEmail.isEnabled = false
        etEmail.setText(mUserDetails.email)

        if (mUserDetails.profileCompleted == 0) {

            etFirstName.isEnabled = false
            etLastName.isEnabled = false
        } else {

            // setupActionBar()

            GlideLoader(this@EditUserProfileActivity).loadUserPicture(Uri.parse(mUserDetails.image), ivUserPhoto)

            if (mUserDetails.mobile != 0L) {
                etPhoneNumber.setText(mUserDetails.mobile.toString())
            }

            if (mUserDetails.gender == Constants.MALE) {

                rbMale.isChecked = true
            } else {

                rbFemale.isChecked = true
            }
        }

        ivUserPhoto.setOnClickListener(this@EditUserProfileActivity)
        btnSave.setOnClickListener(this@EditUserProfileActivity)
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

                    showImagePickerMenu()
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

        val firstName = etFirstName.text.toString().trim { it <= ' ' }
        if (firstName != mUserDetails.firstName) {

            userHashMap[Constants.FIRST_NAME] = firstName
        }

        val lastName = etLastName.text.toString().trim { it <= ' ' }
        if (lastName != mUserDetails.lastName) {

            userHashMap[Constants.LAST_NAME] = lastName
        }

        val phoneNumber = etPhoneNumber.text.toString().trim { it <= ' ' }

        val gender = if (rbMale.isChecked) {

            Constants.MALE
        } else {

            Constants.FEMALE
        }

        if (mUserProfileImageURL.isNotEmpty()) {

            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }

        if (phoneNumber.isNotEmpty() && phoneNumber != mUserDetails.mobile.toString()) {

            userHashMap[Constants.MOBILE] = phoneNumber.toLong()
        }

        if (gender.isNotEmpty() && gender != mUserDetails.gender) {

            userHashMap[Constants.GENDER] = gender
        }

        userHashMap[Constants.COMPLETE_PROFILE] = 1

        // showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().updateUserProfileData(this, userHashMap)
    }

    fun userProfileUpdateSuccess() {

        hideProgressDialog()

        Toast.makeText(
            this@EditUserProfileActivity,
            resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_SHORT
        ).show()

        startActivity(Intent(this@EditUserProfileActivity, DashboardActivity::class.java))
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
                            this@EditUserProfileActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            if (requestCode == Constants.CAMERA_REQUEST_CODE) {

                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap
                // ivUserPhoto.setImageBitmap(thumbnail)

                try {

                    mSelectedImageFileUri = getBitmapUri(applicationContext, thumbnail)!!

                    GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, ivUserPhoto)
                } catch (e: IOException) {

                    e.printStackTrace()
                    Toast.makeText(
                        this@EditUserProfileActivity,
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

    private fun showImagePickerMenu() {

        val popupMenu = PopupMenu(this, ivUserPhoto)
        popupMenu.menuInflater.inflate(R.menu.image_picker_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->

            when (item.itemId) {

                R.id.menu_gallery -> {

                    openGallery()
                    true
                }

                R.id.menu_camera -> {

                    openCamera()
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    private fun openGallery() {

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

    private fun openCamera() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            // showErrorSnackBar("Camera permission already granted.", false)
            Constants.takePhoto(this)
        } else {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                Constants.CAMERA_PERMISSION_CODE
            )
        }
    }
}