package com.example.partyfinder.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.partyfinder.R
import com.example.partyfinder.utils.Constants

class MainActivity : ComponentActivity() {

    private lateinit var tvMain: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvMain = findViewById(R.id.tv_main)

        val sharedPreferences = getSharedPreferences(Constants.PARTYFINDER_PREFERENCES, Context.MODE_PRIVATE)
        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")!!

        tvMain.text = "Hello $username."
    }
}