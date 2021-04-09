package com.manohar.cookbook.utils

import android.content.Context
import android.media.Image
import android.net.Uri
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.squareup.picasso.Picasso

fun getProgressDrawable(context: Context):CircularProgressDrawable
{

    val circularProgressDrawable = CircularProgressDrawable(context)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.start()

    return circularProgressDrawable
}

fun ImageView.loadimage(uri:String?, progressDrawable: CircularProgressDrawable)
{

    Picasso.get().load(uri).placeholder(progressDrawable).into(this)

}