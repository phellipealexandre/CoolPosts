package com.phellipesilva.coolposts.di

import androidx.test.core.app.ApplicationProvider
import com.phellipesilva.coolposts.application.TestApplication
import com.phellipesilva.coolposts.postdetails.view.PostDetailsActivityUITest

val PostDetailsActivityUITest.injector get(): TestComponent  {
    val testApplication = ApplicationProvider.getApplicationContext<TestApplication>()
    return testApplication.component as TestComponent
}