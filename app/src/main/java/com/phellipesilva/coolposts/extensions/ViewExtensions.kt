package com.phellipesilva.coolposts.extensions

import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import timber.log.Timber

fun View.fadeIn() {
    val fadeIn = AlphaAnimation(0.0f, 1.0f)
    fadeIn.duration = 500
    fadeIn.interpolator = DecelerateInterpolator()
    fadeIn.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {}
        override fun onAnimationStart(animation: Animation?) {}
        override fun onAnimationEnd(animation: Animation?) {
            visibility = View.VISIBLE
        }
    })
    startAnimation(fadeIn)
}

fun ImageView.loadRoundedAvatar(
    userId: Int,
    withCrossFade: Boolean = true,
    onLoadingFinished: () -> Unit = {}
) {
    this.load(
        url = "https://api.adorable.io/avatars/$userId",
        withCrossFade = withCrossFade,
        rounded = true,
        onLoadingFinished = onLoadingFinished
    )
}

fun ImageView.loadThumbnail(
    id: Int,
    withCrossFade: Boolean = true,
    onLoadingFinished: () -> Unit = {}
) {
    this.load(
        url = "https://picsum.photos/400/400/?image=$id",
        withCrossFade = withCrossFade,
        rounded = false,
        onLoadingFinished = onLoadingFinished
    )
}

private fun ImageView.load(
    url: String,
    withCrossFade: Boolean,
    rounded: Boolean,
    onLoadingFinished: () -> Unit
) {

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

    if (withCrossFade)
        requestBuilder.transition(DrawableTransitionOptions.withCrossFade())

    if (rounded)
        requestBuilder.apply(RequestOptions.circleCropTransform())
    else
        requestBuilder.apply(RequestOptions.noTransformation())

    requestBuilder.into(this)
}