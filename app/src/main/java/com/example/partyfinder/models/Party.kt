package com.example.partyfinder.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Party (
    val name: String = "",
    val date: String = "",
    val image: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val createdBy: String = "",
    var id: String? = null
        ) : Parcelable