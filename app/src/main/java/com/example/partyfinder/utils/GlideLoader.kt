package com.example.partyfinder.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.partyfinder.R
import java.io.IOException

class GlideLoader(val context: Context) {

    fun loadPicture(image: Any, imageView: ImageView) {

        try {

            Glide
                .with(context)
                .load(image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_placeholder)
                .into(imageView)
        } catch (e: IOException) {

            e.printStackTrace()
        }
    }
}