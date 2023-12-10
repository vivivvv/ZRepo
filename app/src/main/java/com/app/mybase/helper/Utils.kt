package com.app.mybase.helper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.widget.ImageView
import com.app.mybase.R
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*


object Utils {

    @SuppressLint("UseCompatLoadingForDrawables")
    fun setImage(
        context: Context,
        movieImage: String?,
        imageView: ImageView
    ) {
        if (movieImage != null) {
            Glide.with(context)
                .load(movieImage)
                .error(R.color.white)
                .into(imageView)
        } else {
            // make sure Glide doesn't load anything into this view until told otherwise
            Glide.with(context).clear(imageView)
            imageView.setImageDrawable(context.getDrawable(R.color.white))
        }
    }

    @SuppressWarnings("deprecation")
    fun fromHtml(html: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // FROM_HTML_MODE_LEGACY is the behaviour that was used for versions below android N
            // we are using this flag to give a consistent behaviour
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html)
        }
    }

    fun convertTimestampToDate(timestamp: Long): String {
        return try {
            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val date = Date(timestamp * 1000) // Convert seconds to milliseconds
            return dateFormat.format(date)
        } catch (e: Exception) {
            "Invalid date/time format"
        }
    }

    fun convertToTime(inputDateTime: String): String {
        if (inputDateTime.isBlank()) {
            return "Invalid date/time"
        }
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("h a", Locale.getDefault())
            val date = inputFormat.parse(inputDateTime)
            outputFormat.format(date)
        } catch (e: Exception) {
            "Invalid date/time format"
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun setLocalImage(
        context: Context,
        movieImage: Drawable?,
        imageView: ImageView
    ) {
        if (movieImage != null) {
            Glide.with(context)
                .load(movieImage)
                .error(R.color.white)
                .into(imageView)
        } else {
            Glide.with(context).clear(imageView)
            imageView.setImageDrawable(context.getDrawable(R.color.white))
        }
    }

}