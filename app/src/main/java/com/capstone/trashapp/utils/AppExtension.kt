package com.capstone.trashapp.utils

import android.icu.text.SimpleDateFormat
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capstone.trashapp.R
import java.util.Date
import java.util.Locale

fun ImageView.glide(imageUrl: String) {
    val circularProgressDrawable = CircularProgressDrawable(context)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.start()
    Glide.with(this.rootView).load(imageUrl).apply(
        RequestOptions()
            .placeholder(
                circularProgressDrawable
            ).error(AppCompatResources.getDrawable(context, R.drawable.ic_place_holder))
    ).into(this)
}

fun Date.getFormattedTimestamp(): String {
    val format = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
    return format.format(this)
}