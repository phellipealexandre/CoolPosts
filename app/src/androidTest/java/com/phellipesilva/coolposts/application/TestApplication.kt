package com.phellipesilva.coolposts.application

import android.app.Application
import com.phellipesilva.coolposts.di.ApplicationComponent
import com.phellipesilva.coolposts.di.DaggerComponentProvider
import com.phellipesilva.coolposts.di.DaggerTestComponent

class TestApplication : Application(), DaggerComponentProvider {
    override val component: ApplicationComponent = DaggerTestComponent
        .builder()
        .applicationContext(this)
        .build()
}