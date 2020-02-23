package com.phellipesilva.coolposts.postlist.view

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.phellipesilva.coolposts.R
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowNetworkCapabilities

@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(RobolectricTestRunner::class)
class PostListActivityIntegrationTest {

    @Test
    fun `Should show snackbar with no internet message when there is no connection to internet`() {
        disableInternetConnection()

        launch(PostListActivity::class.java)

        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText(R.string.no_connection_msg)))
    }

    @Test
    fun `Should not show error snackbar when there is internet connection and no error occurs`() {
        enableInternetConnection()

        launch(PostListActivity::class.java)

        onView(withId(com.google.android.material.R.id.snackbar_text)).check(doesNotExist())
    }

    private fun disableInternetConnection() {
        val connectivityManager = ApplicationProvider.getApplicationContext<Context>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        Shadows.shadowOf(connectivityManager).setNetworkCapabilities(connectivityManager.activeNetwork, null)
    }

    private fun enableInternetConnection() {
        val connectivityManager = ApplicationProvider.getApplicationContext<Context>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCapabilities = ShadowNetworkCapabilities.newInstance()
        Shadows.shadowOf(networkCapabilities).addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        Shadows.shadowOf(connectivityManager).setNetworkCapabilities(connectivityManager.activeNetwork, networkCapabilities)
    }
}