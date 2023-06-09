package com.example.partyfinder.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.partyfinder.models.Party
import com.example.partyfinder.ui.activities.LoginActivity
import com.example.partyfinder.ui.activities.RegisterActivity
import com.example.partyfinder.ui.activities.EditUserProfileActivity
import com.example.partyfinder.models.User
import com.example.partyfinder.ui.activities.DashboardActivity
import com.example.partyfinder.ui.activities.LocationPickerActivity
import com.example.partyfinder.ui.fragments.HomeFragment
import com.example.partyfinder.ui.fragments.MapFragment
import com.example.partyfinder.ui.fragments.ProfileFragment
import com.example.partyfinder.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: Activity, userInfo: User) {

        mFireStore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                when (activity) {

                    is RegisterActivity -> {

                        activity.userRegistrationSuccess()
                    }

                    is LoginActivity -> {

                        activity.userLoggedInSuccess(userInfo)
                    }
                }
            }
            .addOnFailureListener { e ->

                when (activity) {

                    is RegisterActivity -> {

                        activity.hideProgressDialog()
                    }

                    is LoginActivity -> {

                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user.",
                    e
                )
            }
    }

    fun uploadParty(activity: Activity, partyInfo: Party) {

        mFireStore.collection(Constants.PARTIES)
            .add(partyInfo)
            .addOnSuccessListener {

                when (activity) {

                    is LocationPickerActivity -> {

                        activity.partyCreationSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->

                when (activity) {

                    is LocationPickerActivity -> {

                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating the party.",
                    e
                )
            }
    }

    fun getParties(activity: Activity, fragment: Fragment? = null) {

        mFireStore.collection(Constants.PARTIES)
            .get()
            .addOnSuccessListener { document ->

                Log.e("Parties List", document.documents.toString())

                val partiesList: ArrayList<Party> = ArrayList()

                for (i in document.documents) {

                    val party = i.toObject(Party::class.java)!!
                    party.id = i.id

                    partiesList.add(party)
                }

                when (fragment) {

                    is HomeFragment -> {

                        fragment.successPartiesList(partiesList)
                    }

                    is MapFragment -> {

                        fragment.successPartiesList(partiesList)
                    }
                }
            }
            .addOnFailureListener { e ->

                when (activity) {

                    is DashboardActivity -> {

                        activity.hideProgressDialog()
                    }
                }

                Log.e("Get Parties List", "Error while getting parties list.", e)
            }
    }

    fun getCurrentUserID(): String {

        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""
        if (currentUser != null) {

            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun userExists(userId: String, callback: (Boolean) -> Unit) {

        mFireStore.collection(Constants.USERS)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->

                val exists = document.exists()
                callback(exists)
            }
            .addOnFailureListener { e ->

                Log.e("FirestoreClass", "Error while checking if user exists.", e)
                callback(false)
            }
    }

    fun getUserDetails(activity: Activity, fragment: Fragment? = null) {

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                Log.i(activity.javaClass.simpleName, document.toString())

                val user = document.toObject(User::class.java)!!

                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constants.PARTYFINDER_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()

                when (activity) {

                    is LoginActivity -> {

                        activity.userLoggedInSuccess(user)
                    }

                    is DashboardActivity -> {

                        (fragment as ProfileFragment).userDetailsSuccess(user)
                    }
                }
            }
            .addOnFailureListener { e ->

                when (activity) {

                    is LoginActivity -> {

                        activity.hideProgressDialog()
                    }

                    is DashboardActivity -> {

                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {

                when (activity) {

                    is EditUserProfileActivity -> {

                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->

                when (activity) {

                    is EditUserProfileActivity -> {

                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details.",
                    e
                )
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?) {

        var baseName = ""

        when (activity) {

            is EditUserProfileActivity -> {

                baseName = Constants.USER_PROFILE_IMAGE
            }

            is LocationPickerActivity -> {

                baseName = Constants.PARTY_PROFILE_IMAGE
            }
        }

        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            baseName + System.currentTimeMillis() + "." + Constants.getFileExtension(
                activity,
                imageFileURI
            )
        )

        sRef.putFile(imageFileURI!!).addOnSuccessListener { taskSnapshot ->

            Log.e(
                "Firebase Image URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )

            // Get the downloadable url from the task snapshot
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->

                Log.e("Downloadable Image URL", uri.toString())

                when (activity) {

                    is EditUserProfileActivity -> {

                        activity.imageUploadSuccess(uri.toString())
                    }

                    is LocationPickerActivity -> {

                        activity.imageUploadSuccess(uri.toString())
                    }
                }
            }
        }
            .addOnFailureListener { exception ->

                when (activity) {

                    is EditUserProfileActivity -> {

                        activity.hideProgressDialog()
                    }

                    is LocationPickerActivity -> {

                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
        }
    }
}