package com.phellipesilva.coolposts.di

import androidx.test.platform.app.InstrumentationRegistry
import com.phellipesilva.coolposts.application.TestApplication
import com.phellipesilva.coolposts.postdetails.view.PostDetailsActivityTest

fun PostDetailsActivityTest.injector(): TestComponent  {
    val testApplication = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestApplication
    return testApplication.component as TestComponent
}