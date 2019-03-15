package com.phellipesilva.coolposts

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.phellipesilva.coolposts.application.TestApplication
import io.appflate.restmock.RESTMockOptions
import io.appflate.restmock.RESTMockServerStarter
import io.appflate.restmock.android.AndroidAssetsFileParser
import io.appflate.restmock.android.AndroidLogger

class TestInstrumentationRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        RESTMockServerStarter.startSync(
            AndroidAssetsFileParser(getContext()),
            AndroidLogger(),
            RESTMockOptions.Builder().useHttps(true).build()
        )
        return super.newApplication(cl, TestApplication::class.java.name, context)
    }
}