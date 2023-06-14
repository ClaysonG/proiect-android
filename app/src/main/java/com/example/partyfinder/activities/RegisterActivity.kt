package com.example.partyfinder.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.example.partyfinder.R
import com.example.partyfinder.utils.CustomTextViewBold

class RegisterActivity : Activity() {

    private lateinit var tvLogin : CustomTextViewBold

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        tvLogin = findViewById(R.id.tv_login)

        tvLogin.setOnClickListener {

            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}