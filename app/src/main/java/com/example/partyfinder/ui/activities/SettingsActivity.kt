package com.example.partyfinder.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.partyfinder.R
import com.example.partyfinder.utils.CustomButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class SettingsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var tbSettings: androidx.appcompat.widget.Toolbar
    private lateinit var btnLogout: CustomButton

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        tbSettings = findViewById(R.id.toolbar_settings_activity)
        btnLogout = findViewById(R.id.btn_logout)

        setupActionBar()

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

    override fun onClick(v: View?) {

        if (v != null) {

            when (v.id) {

                R.id.btn_logout -> {

                    val currentUser = FirebaseAuth.getInstance().currentUser

                    val providers = currentUser?.providerData?.map { it.providerId }

                    if (providers?.contains(GoogleAuthProvider.PROVIDER_ID) == true) {

                        googleSignInClient = GoogleSignIn.getClient(this@SettingsActivity, GoogleSignInOptions.DEFAULT_SIGN_IN)
                        googleSignInClient.signOut().addOnCompleteListener { task ->

                                if (task.isSuccessful) {

                                    logoutUser()
                                } else {

                                    showErrorSnackBar("Error while logging out", true)
                                }
                        }
                    } else {

                        logoutUser()
                    }
                }
            }
        }
    }

    private fun logoutUser() {

        FirebaseAuth.getInstance().signOut()

        val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}