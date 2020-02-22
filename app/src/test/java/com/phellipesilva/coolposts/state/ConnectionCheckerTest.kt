package com.phellipesilva.coolposts.state

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowNetworkCapabilities

@RunWith(RobolectricTestRunner::class)
class ConnectionCheckerTest {

    private lateinit var connectionChecker: ConnectionChecker

    @Before
    fun setUp() {
        connectionChecker = ConnectionChecker(getApplicationContext<Context>())
    }

    @Config(sdk = [Build.VERSION_CODES.LOLLIPOP])
    @Test
    fun `Should not be connected when active network is not connected for OS version less than Marshmallow`() {
        val connectivityManager = getApplicationContext<Context>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        @Suppress("DEPRECATION")
        shadowOf(connectivityManager.activeNetworkInfo).setConnectionStatus(NetworkInfo.State.DISCONNECTED)

        assertFalse("Network should not be connected for DISCONNECTED state", connectionChecker.isOnline())
    }

    @Config(sdk = [Build.VERSION_CODES.LOLLIPOP])
    @Test
    fun `Should be connected when active network is connected for OS version less than Marshmallow`() {
        val connectivityManager = getApplicationContext<Context>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        @Suppress("DEPRECATION")
        shadowOf(connectivityManager.activeNetworkInfo).setConnectionStatus(NetworkInfo.State.CONNECTED)

        assertTrue("Network should be connected for CONNECTED state", connectionChecker.isOnline())
    }

    @Config(sdk = [Build.VERSION_CODES.M])
    @Test
    fun `Should be connected when active network capability is WIFI and OS version greater or equals than Marshmallow`() {
        val connectivityManager = getApplicationContext<Context>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCapabilities = ShadowNetworkCapabilities.newInstance()
        shadowOf(networkCapabilities).addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        shadowOf(connectivityManager).setNetworkCapabilities(connectivityManager.activeNetwork, networkCapabilities)

        assertTrue("Network should be connected for WIFI Capability", connectionChecker.isOnline())
    }

    @Config(sdk = [Build.VERSION_CODES.M])
    @Test
    fun `Should be connected when active network capability is CELLULAR and OS version greater or equals than Marshmallow`() {
        val connectivityManager = getApplicationContext<Context>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCapabilities = ShadowNetworkCapabilities.newInstance()
        shadowOf(networkCapabilities).addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        shadowOf(connectivityManager).setNetworkCapabilities(connectivityManager.activeNetwork, networkCapabilities)

        assertTrue("Network should be connected for CELLULAR Capability", connectionChecker.isOnline())
    }

    @Config(sdk = [Build.VERSION_CODES.M])
    @Test
    fun `Should be connected when active network capability is ETHERNET and OS version greater or equals than Marshmallow`() {
        val connectivityManager = getApplicationContext<Context>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCapabilities = ShadowNetworkCapabilities.newInstance()
        shadowOf(networkCapabilities).addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
        shadowOf(connectivityManager).setNetworkCapabilities(connectivityManager.activeNetwork, networkCapabilities)

        assertTrue("Network should be connected for ETHERNET Capability", connectionChecker.isOnline())
    }

    @Config(sdk = [Build.VERSION_CODES.M])
    @Test
    fun `Should not be connected when active network capability is not ETHERNET, CELLULAR or WIFI and OS version greater or equals than Marshmallow`() {
        val connectivityManager = getApplicationContext<Context>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCapabilities = ShadowNetworkCapabilities.newInstance()
        shadowOf(networkCapabilities).addTransportType(NetworkCapabilities.TRANSPORT_BLUETOOTH)
        shadowOf(connectivityManager).setNetworkCapabilities(connectivityManager.activeNetwork, networkCapabilities)

        assertFalse("Network should be connected just for ETHERNET, CELLULAR or WIFI Capabilities", connectionChecker.isOnline())
    }

    @Config(sdk = [Build.VERSION_CODES.M])
    @Test
    fun `Should not be connected when active network has no network capabilities and OS version greater or equals than Marshmallow`() {
        val connectivityManager = getApplicationContext<Context>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        shadowOf(connectivityManager).setNetworkCapabilities(connectivityManager.activeNetwork, null)

        assertNull("Network capabilities should be null", connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork))
        assertFalse("Network should be connected just for ETHERNET, CELLULAR or WIFI Capabilities", connectionChecker.isOnline())
    }
}