package com.phellipesilva.coolposts.state

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ConnectionCheckerTest {

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var connectivityManager: ConnectivityManager

    @Mock
    private lateinit var networkInfo: NetworkInfo

    private lateinit var connectionChecker: ConnectionChecker

    @Before
    fun setUp() {
        whenever(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)
        connectionChecker = ConnectionChecker(context)
    }

    @Test
    fun shouldReturnNotOnlineWhenConnectivityManagerHasNoNetworkInfo() {
        assertFalse(connectionChecker.isOnline())
    }

    @Test
    fun shouldReturnNotOnlineWhenNetworkInfoReturnsNotConnected() {
        whenever(connectivityManager.activeNetworkInfo).thenReturn(networkInfo)
        whenever(networkInfo.isConnected).thenReturn(false)

        assertFalse(connectionChecker.isOnline())
    }

    @Test
    fun shouldReturnOnlineWhenNetworkInfoReturnsConnected() {
        whenever(connectivityManager.activeNetworkInfo).thenReturn(networkInfo)
        whenever(networkInfo.isConnected).thenReturn(true)

        assertTrue(connectionChecker.isOnline())
    }
}