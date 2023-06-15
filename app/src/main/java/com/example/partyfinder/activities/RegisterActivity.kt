package com.example.partyfinder.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.CheckBox
import androidx.appcompat.widget.Toolbar
import com.example.partyfinder.R
import com.example.partyfinder.utils.CustomButton
import com.example.partyfinder.utils.CustomEditText
import com.example.partyfinder.utils.CustomTextViewBold

class RegisterActivity : BaseActivity() {

    private lateinit var tvLogin : CustomTextViewBold
    private lateinit var tbRegister: Toolbar
    private lateinit var etFirstName: CustomEditText
    private lateinit var etLastName: CustomEditText
    private lateinit var etEmail: CustomEditText
    private lateinit var etPassword: CustomEditText
    private lateinit var etConfirmPassword: CustomEditText
    private lateinit var cbTermsAndConditions: CheckBox
    private lateinit var btnRegister: CustomButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        tvLogin = findViewById(R.id.tv_login)
        tbRegister = findViewById(R.id.toolbar_register_activity)
        etFirstName = findViewById(R.id.et_first_name)
        etLastName = findViewById(R.id.et_last_name)
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        etConfirmPassword = findViewById(R.id.et_confirm_password)
        cbTermsAndConditions = findViewById(R.id.cb_terms_and_conditions)
        btnRegister = findViewById(R.id.btn_register)

        setupActionBar()

        tvLogin.setOnClickListener {

            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnRegister.setOnClickListener {
            validateRegisterDetails()
        }
    }

    @Suppress("DEPRECATION")
    private fun setupActionBar() {

        setSupportActionBar(tbRegister)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24)
        }

        tbRegister.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(etFirstName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }

            TextUtils.isEmpty(etLastName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }

            TextUtils.isEmpty(etEmail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(etPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            TextUtils.isEmpty(etConfirmPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_confirm_password), true)
                false
            }

            etPassword.text.toString().trim { it <= ' ' } != etConfirmPassword.text.toString().trim { it <= ' ' } -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password_and_confirm_password_mismatch), true)
                false
            }

            !cbTermsAndConditions.isChecked -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_agree_terms_and_conditions), true)
                false
            }

            else -> {
                showErrorSnackBar(resources.getString(R.string.register_success), false)
                true
            }
        }
    }
}