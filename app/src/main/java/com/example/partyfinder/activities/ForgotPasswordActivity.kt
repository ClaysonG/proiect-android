package com.example.partyfinder.activities

import android.os.Bundle
import android.widget.Toast
import com.example.partyfinder.R
import com.example.partyfinder.utils.CustomButton
import com.example.partyfinder.utils.CustomEditText
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : BaseActivity() {

    private lateinit var tbForgotPassword: androidx.appcompat.widget.Toolbar
    private lateinit var etEmailForgotPassword: CustomEditText
    private lateinit var btnSubmitForgotPassword : CustomButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        tbForgotPassword = findViewById(R.id.toolbar_forgot_password_activity)
        etEmailForgotPassword = findViewById(R.id.et_email_forgot_password)
        btnSubmitForgotPassword = findViewById(R.id.btn_submit_forgot_password)

        setupActionBar()

        handleSubmit()
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

    private fun handleSubmit() {

        btnSubmitForgotPassword.setOnClickListener {

            val email: String = etEmailForgotPassword.text.toString().trim { it <= ' ' }

            if (email.isEmpty()) {

                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
            } else {

                showProgressDialog(resources.getString(R.string.please_wait))
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->

                        hideProgressDialog()

                        if (task.isSuccessful) {

                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                resources.getString(R.string.email_sent_success),
                                Toast.LENGTH_LONG
                            ).show()
                            // showErrorSnackBar(resources.getString(R.string.email_sent_success), false)
                            finish()
                        } else {

                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    }
            }
        }
    }
}