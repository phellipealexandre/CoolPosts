package com.phellipesilva.coolposts.postdetails.view

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.phellipesilva.coolposts.R
import com.phellipesilva.coolposts.postlist.domain.Post
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowNetworkCapabilities

@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(RobolectricTestRunner::class)
class PostDetailsActivityIntegrationTest {

    @Test
    fun `Should show snackbar with no internet message when there is no connection to internet`() {
        disableInternetConnection()

        val post = Post(id = 1, title = "title", body = "body", userName = "userName", userId = 1)
        val intent = PostDetailsActivity.newNavigationIntent(getApplicationContext(), post)
        ActivityScenario.launch<PostDetailsActivity>(intent)

        Espresso.onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText(R.string.no_connection_msg)))
    }

    @Test
    fun `Should not show error snackbar when there is internet connection and no error occurs`() {
        enableInternetConnection()

        val post = Post(id = 1, title = "title", body = "body", userName = "userName", userId = 1)
        val intent = PostDetailsActivity.newNavigationIntent(getApplicationContext(), post)
        ActivityScenario.launch<PostDetailsActivity>(intent)

        Espresso.onView(withId(R.id.snackbar_text)).check(doesNotExist())
    }

    private fun disableInternetConnection() {
        val connectivityManager = getApplicationContext<Context>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        Shadows.shadowOf(connectivityManager).setNetworkCapabilities(connectivityManager.activeNetwork, null)
    }

    private fun enableInternetConnection() {
        val connectivityManager = getApplicationContext<Context>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCapabilities = ShadowNetworkCapabilities.newInstance()
        Shadows.shadowOf(networkCapabilities).addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        Shadows.shadowOf(connectivityManager).setNetworkCapabilities(connectivityManager.activeNetwork, networkCapabilities)
    }
}