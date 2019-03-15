package com.phellipesilva.coolposts.di

import android.app.Activity

val Activity.injector get() = (application as DaggerComponentProvider).component