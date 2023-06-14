package com.example.partyfinder.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.example.partyfinder.R
import com.example.partyfinder.utils.CustomTextViewBold

class LoginActivity : Activity() {

    private lateinit var tvRegister : CustomTextViewBold

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tvRegister = findViewById(R.id.tv_register)

        tvRegister.setOnClickListener {

            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}