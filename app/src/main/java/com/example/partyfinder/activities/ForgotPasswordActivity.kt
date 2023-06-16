package com.example.partyfinder.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.partyfinder.R

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var tbForgotPassword: androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        tbForgotPassword = findViewById(R.id.toolbar_forgot_password_activity)

        setupActionBar()
    }

    @Suppress("DEPRECATION")
    private fun setupActionBar() {

        setSupportActionBar(tbForgotPassword)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24)
            actionBar.setDisplayShowTitleEnabled(false)
        }

        tbForgotPassword.setNavigationOnClickListener { onBackPressed() }
    }
}