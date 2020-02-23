package com.phellipesilva.coolposts.utils

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.test.espresso.IdlingResource

class SwipeLayoutRefreshingIdlingResource(private val swipeRefreshLayout: SwipeRefreshLayout) : IdlingResource {

    private var resourceCallback: IdlingResource.ResourceCallback? = null

    override fun getName(): String = this::class.java.name

    override fun isIdleNow(): Boolean {
        if (!swipeRefreshLayout.isRefreshing) {
            this.resourceCallback?.onTransitionToIdle()
            return true
        }

        return false
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.resourceCallback = callback
    }
}