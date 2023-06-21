package com.example.partyfinder.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {

    const val USERS: String = "users"
    const val PARTYFINDER_PREFERENCES: String = "PartyFinderPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"
    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1

    const val MALE: String = "male"
    const val FEMALE: String = "female"
    const val FIRST_NAME: String = "firstName"
    const val LAST_NAME: String = "lastName"
    const val MOBILE: String = "mobile"
    const val GENDER: String = "gender"
    const val IMAGE: String = "image"
    const val COMPLETE_PROFILE: String = "profileCompleted"

    const val USER_PROFILE_IMAGE: String = "User_Profile_Image"

    const val GOOGLE_SIGN_IN_CODE = 100

    const val CAMERA_PERMISSION_CODE = 3
    const val CAMERA_REQUEST_CODE = 4

    const val LOCATION_PERMISSION_REQUEST_CODE = 5

    const val PARTIES: String = "parties"

    const val PARTY_DETAILS: String = "party_details"

    const val PARTY_IMAGE: String = "image"
    const val PARTY_NAME: String = "name"
    const val PARTY_DATE: String = "date"

    const val PARTY_PROFILE_IMAGE: String = "Party_Profile_Image"

    fun showImagePicker(activity: Activity) {

        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {

        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

    fun takePhoto(activity: Activity) {

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        activity.startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
    }
}