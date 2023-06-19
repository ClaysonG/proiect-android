package com.example.partyfinder.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.partyfinder.R
import com.example.partyfinder.firestore.FirestoreClass
import com.example.partyfinder.models.User
import com.example.partyfinder.ui.activities.DashboardActivity
import com.example.partyfinder.ui.activities.SettingsActivity
import com.example.partyfinder.ui.activities.UserProfileActivity
import com.example.partyfinder.utils.Constants
import com.example.partyfinder.utils.CustomTextView
import com.example.partyfinder.utils.CustomTextViewBold
import com.example.partyfinder.utils.GlideLoader

@Suppress("DEPRECATION")
class ProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var ivUserPhoto: ImageView
    private lateinit var tvName: CustomTextViewBold
    private lateinit var tvGender: CustomTextView
    private lateinit var tvEmail: CustomTextView
    private lateinit var tvPhoneNumber: CustomTextView
    private lateinit var tvEdit: CustomTextView

    private lateinit var mUserDetails: User

    private lateinit var parentActivity: DashboardActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val root = inflater.inflate(R.layout.fragment_profile, container, false)

        ivUserPhoto = root.findViewById(R.id.iv_user_photo)
        tvName = root.findViewById(R.id.tv_name)
        tvGender = root.findViewById(R.id.tv_gender)
        tvEmail = root.findViewById(R.id.tv_email)
        tvPhoneNumber = root.findViewById(R.id.tv_phone_number)
        tvEdit = root.findViewById(R.id.tv_edit)

        tvEdit.setOnClickListener(this@ProfileFragment)

        parentActivity = activity as DashboardActivity

        return root
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        when (id) {

            R.id.action_settings -> {

                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun getUserDetails() {

        parentActivity.showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getUserDetails(parentActivity, this@ProfileFragment)
    }

    @SuppressLint("SetTextI18n")
    fun userDetailsSuccess(user: User) {

        mUserDetails = user

        parentActivity.hideProgressDialog()

        this@ProfileFragment.context?.let { GlideLoader(it).loadUserPicture(user.image, ivUserPhoto) }

        tvName.text = "${user.firstName} ${user.lastName}"
        tvGender.text = user.gender
        tvEmail.text = user.email
        tvPhoneNumber.text = "${user.mobile}"
    }

    override fun onResume() {
        super.onResume()

        getUserDetails()

        parentActivity.tvTitle.text = resources.getString(R.string.title_profile)
    }

    override fun onClick(v: View?) {

        if (v != null) {

            when (v.id) {

                R.id.tv_edit -> {

                    val intent = Intent(parentActivity, UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
                    startActivity(intent)
                }
            }
        }
    }
}