package com.example.partyfinder.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.partyfinder.R
import com.example.partyfinder.firestore.FirestoreClass
import com.example.partyfinder.models.Party
import com.example.partyfinder.ui.activities.DashboardActivity
import com.example.partyfinder.ui.activities.EditUserProfileActivity
import com.example.partyfinder.ui.activities.LocationPickerActivity
import com.example.partyfinder.utils.Constants
import com.example.partyfinder.utils.CustomButton
import com.example.partyfinder.utils.CustomEditText

@Suppress("DEPRECATION")
class NewPartyFragment : Fragment(), View.OnClickListener {

    private lateinit var parentActivity: DashboardActivity

    lateinit var ivPartyPhoto: ImageView
    private lateinit var etPartyName: CustomEditText
    private lateinit var btnDatePicker: CustomButton

    var mSelectedImageFileUri: Uri? = null
    var mPartyName: String? = null
    var mSelectedDate: String? = null

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

        val root = inflater.inflate(R.layout.fragment_new_party, container, false)

        ivPartyPhoto = root.findViewById(R.id.iv_party_photo)
        etPartyName = root.findViewById(R.id.et_party_name)
        btnDatePicker = root.findViewById(R.id.btn_pick_date)

        ivPartyPhoto.setOnClickListener(this)
        btnDatePicker.setOnClickListener(this)

        parentActivity = activity as DashboardActivity

        return root
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.new_party_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        when (id) {

            R.id.action_next -> {

                if (validatePartyDetails()) {

                    mPartyName = etPartyName.text.toString().trim { it <= ' ' }

                    val partyDetails = HashMap<String, Any>()
                    partyDetails[Constants.PARTY_IMAGE] = mSelectedImageFileUri.toString()
                    partyDetails[Constants.PARTY_NAME] = mPartyName!!
                    partyDetails[Constants.PARTY_DATE] = mSelectedDate!!

                    val intent = Intent(parentActivity, LocationPickerActivity::class.java)
                    intent.putExtra(Constants.PARTY_DETAILS, partyDetails)
                    startActivity(intent)
                }

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        parentActivity.tvTitle.text = resources.getString(R.string.title_new_party)
    }

    private fun showDatePickerDialog(v: View) {

        val newFragment = DatePickerFragment()
        newFragment.show(parentActivity.supportFragmentManager, "datePicker")
    }

    override fun onClick(v: View?) {

        if (v != null) {

            when (v.id) {

                R.id.iv_party_photo -> {

                    showImagePickerMenu()
                }

                R.id.btn_pick_date -> {

                    showDatePickerDialog(v)
                }
            }
        }
    }

    private fun showImagePickerMenu() {

        val popupMenu = PopupMenu(parentActivity, ivPartyPhoto)
        popupMenu.menuInflater.inflate(R.menu.image_picker_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->

            when (item.itemId) {

                R.id.menu_gallery -> {

                    openGallery()
                    true
                }

                R.id.menu_camera -> {

                    openCamera()
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    private fun openGallery() {

        // Here we will check if the permission is already allowed or id we need to ask for it.
        // If the permission is already allowed then the boolean value returned will be true else false.
        if (ContextCompat.checkSelfPermission(parentActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            // showErrorSnackBar("Storage permission already granted.", false)
            Constants.showImagePicker(parentActivity)
        } else {

            // Request permissions to be granted to this application at runtime.
            ActivityCompat.requestPermissions(
                parentActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.READ_STORAGE_PERMISSION_CODE
            )
        }
    }

    private fun openCamera() {

        if (ContextCompat.checkSelfPermission(parentActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            // showErrorSnackBar("Camera permission already granted.", false)
            Constants.takePhoto(parentActivity)
        } else {

            ActivityCompat.requestPermissions(
                parentActivity,
                arrayOf(Manifest.permission.CAMERA),
                Constants.CAMERA_PERMISSION_CODE
            )
        }
    }

    private fun validatePartyDetails(): Boolean {

        return when {

            mSelectedImageFileUri == null -> {
                parentActivity.showErrorSnackBar(resources.getString(R.string.err_msg_select_party_image), true)
                false
            }

            TextUtils.isEmpty(etPartyName.text.toString().trim { it <= ' ' }) -> {
                parentActivity.showErrorSnackBar(resources.getString(R.string.err_msg_enter_party_name), true)
                false
            }

            mSelectedDate == null -> {
                parentActivity.showErrorSnackBar(resources.getString(R.string.err_msg_select_party_date), true)
                false
            }

            else -> {
                // showErrorSnackBar(resources.getString(R.string.register_success), false)
                true
            }
        }
    }
}