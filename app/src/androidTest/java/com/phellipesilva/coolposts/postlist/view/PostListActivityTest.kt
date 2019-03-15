package com.phellipesilva.coolposts.postlist.view

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import io.appflate.restmock.RESTMockServer
import io.appflate.restmock.utils.RequestMatchers.pathContains
import org.hamcrest.CoreMatchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class PostListActivityTest {

    @Rule
    @JvmField
    var activityRule: ActivityTestRule<PostListActivity> = ActivityTestRule(PostListActivity::class.java, false, false)

    @Test
    fun shouldFetchPostsWhenOpenActivityForTheFirstTime() {
        RESTMockServer.whenGET(pathContains("posts")).thenReturnFile(200, "json/posts_response.json")
        RESTMockServer.whenGET(pathContains("users")).thenReturnFile(200, "json/users_response.json")

        activityRule.launchActivity(Intent())

        onView(withText("qui est esse")).check(matches(isDisplayed()))
        onView(
            allOf(
                withText("eum et est occaecati"),
                hasSibling(withText("Leanne Graham"))
            )
        ).check(matches(isDisplayed()))
    }
}