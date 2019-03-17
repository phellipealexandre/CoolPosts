package com.phellipesilva.coolposts.application

import android.app.Application
import com.phellipesilva.coolposts.BuildConfig
import com.phellipesilva.coolposts.di.ApplicationComponent
import com.phellipesilva.coolposts.di.DaggerApplicationComponent
import com.phellipesilva.coolposts.di.DaggerComponentProvider
import timber.log.Timber

class CoolPostsApplication : Application(), DaggerComponentProvider {

    override val component: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
            .applicationContext(applicationContext)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        setupTimber()
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}