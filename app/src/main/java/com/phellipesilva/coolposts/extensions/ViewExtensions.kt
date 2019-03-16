package com.phellipesilva.coolposts.extensions

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import timber.log.Timber

fun ImageView.load(url: String, rounded: Boolean = false, onLoadingFinished: () -> Unit = {}) {
    val listener = object : RequestListener<Drawable> {

        override fun onLoadFailed(glideException: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
            Timber.e(glideException)
            onLoadingFinished()
            return false
        }

        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            onLoadingFinished()
            return false
        }
    }


    val requestBuilder = Glide.with(this)
        .load(url)
        .listener(listener)

    if (rounded)
        requestBuilder.apply(RequestOptions.circleCropTransform())
    else
        requestBuilder.apply(RequestOptions.noTransformation())

    requestBuilder.into(this)

}