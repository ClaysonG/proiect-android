package com.example.partyfinder.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.example.partyfinder.R
import com.example.partyfinder.firestore.FirestoreClass
import com.example.partyfinder.models.User
import com.example.partyfinder.utils.Constants
import com.example.partyfinder.utils.CustomButton
import com.example.partyfinder.utils.CustomTextView
import com.example.partyfinder.utils.CustomTextViewBold
import com.example.partyfinder.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var tbSettings: androidx.appcompat.widget.Toolbar
    private lateinit var ivUserPhoto: ImageView
    private lateinit var tvName: CustomTextViewBold
    private lateinit var tvGender: CustomTextView
    private lateinit var tvEmail: CustomTextView
    private lateinit var tvPhoneNumber: CustomTextView
    private lateinit var tvEdit: CustomTextView
    private lateinit var btnLogout: CustomButton

    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        tbSettings = findViewById(R.id.toolbar_settings_activity)
        ivUserPhoto = findViewById(R.id.iv_user_photo)
        tvName = findViewById(R.id.tv_name)
        tvGender = findViewById(R.id.tv_gender)
        tvEmail = findViewById(R.id.tv_email)
        tvPhoneNumber = findViewById(R.id.tv_phone_number)
        tvEdit = findViewById(R.id.tv_edit)
        btnLogout = findViewById(R.id.btn_logout)

        setupActionBar()

        tvEdit.setOnClickListener(this@SettingsActivity)
        btnLogout.setOnClickListener(this@SettingsActivity)
    }

    @Suppress("DEPRECATION")
    private fun setupActionBar() {

        setSupportActionBar(tbSettings)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24)
            actionBar.setDisplayShowTitleEnabled(false)
        }

        tbSettings.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getUserDetails() {

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getUserDetails(this@SettingsActivity)
    }

    @SuppressLint("SetTextI18n")
    fun userDetailsSuccess(user: User) {

        mUserDetails = user

        hideProgressDialog()

        GlideLoader(this@SettingsActivity).loadUserPicture(user.image, ivUserPhoto)

        tvName.text = "${user.firstName} ${user.lastName}"
        tvGender.text = user.gender
        tvEmail.text = user.email
        tvPhoneNumber.text = "${user.mobile}"
    }

    override fun onResume() {
        super.onResume()

        getUserDetails()
    }

    override fun onClick(v: View?) {

        if (v != null) {

            when (v.id) {

                R.id.tv_edit -> {

                    val intent = Intent(this@SettingsActivity, UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
                    startActivity(intent)
                }

                R.id.btn_logout -> {

                    FirebaseAuth.getInstance().signOut()

                    val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}