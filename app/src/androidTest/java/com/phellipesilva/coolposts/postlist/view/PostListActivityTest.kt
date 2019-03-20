package com.phellipesilva.coolposts.postlist.view

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.phellipesilva.coolposts.R
import com.phellipesilva.coolposts.utils.SwipeLayoutRefreshingIdlingResource
import io.appflate.restmock.RESTMockServer
import io.appflate.restmock.utils.RequestMatchers.pathContains
import kotlinx.android.synthetic.main.activity_post_list.*
import org.hamcrest.CoreMatchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PostListActivityTest {

    @get:Rule
    var activityRule: ActivityTestRule<PostListActivity> = ActivityTestRule(PostListActivity::class.java, false, false)

    @Before
    fun setUp() {
        RESTMockServer.reset()
    }

    @Test
    fun shouldFetchPostsWhenOpenActivityForTheFirstTime() {
        RESTMockServer.whenGET(pathContains("posts")).thenReturnFile(200, "json/posts_response.json")
        RESTMockServer.whenGET(pathContains("users")).thenReturnFile(200, "json/users_response.json")

        val activity = activityRule.launchActivity(Intent())
        val idlingResource = SwipeLayoutRefreshingIdlingResource(activity.postListSwipeRefreshLayout)
        IdlingRegistry.getInstance().register(idlingResource)

        onView(
            allOf(
                withText("sunt aut facere repellat provident occaecati excepturi optio reprehenderit"),
                hasSibling(withText("Leanne Graham"))
            )
        ).check(matches(isDisplayed()))

        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun shouldUpdateSecondPostTitleWhenOpenActivityAndRefreshAgain() {
        RESTMockServer.whenGET(pathContains("posts"))
            .thenReturnFile(200, "json/posts_response.json")
            .thenReturnFile(200, "json/posts_updated_response.json")
        RESTMockServer.whenGET(pathContains("users"))
            .thenReturnFile(200, "json/users_response.json")

        val activity = activityRule.launchActivity(Intent())
        val idlingResource = SwipeLayoutRefreshingIdlingResource(activity.postListSwipeRefreshLayout)
        IdlingRegistry.getInstance().register(idlingResource)

        onView(withText("sunt aut facere repellat provident occaecati excepturi optio reprehenderit")).check(matches(isDisplayed()))

        onView(withId(com.phellipesilva.coolposts.R.id.postListSwipeRefreshLayout)).perform(swipeDown())
        onView(withText("sunt aut facere repellat provident occaecati excepturi optio reprehenderit")).check(doesNotExist())
        onView(withText("Updated Title")).check(matches(isDisplayed()))

        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun shouldScrollToBottomPosition() {
        RESTMockServer.whenGET(pathContains("posts")).thenReturnFile(200, "json/posts_response.json")
        RESTMockServer.whenGET(pathContains("users")).thenReturnFile(200, "json/users_response.json")

        val activity = activityRule.launchActivity(Intent())
        val idlingResource = SwipeLayoutRefreshingIdlingResource(activity.postListSwipeRefreshLayout)
        IdlingRegistry.getInstance().register(idlingResource)

        onView(withId(R.id.postListRecyclerView)).perform(RecyclerViewActions.scrollToPosition<PostListAdapter.PostViewHolder>(99))
        onView(withText("at nam consequatur ea labore ea harum")).check(matches(isDisplayed()))

        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun shouldMaintainScrollPositionAfterScreenOrientationChanges() {
        RESTMockServer.whenGET(pathContains("posts")).thenReturnFile(200, "json/posts_response.json")
        RESTMockServer.whenGET(pathContains("users")).thenReturnFile(200, "json/users_response.json")

        val activity = activityRule.launchActivity(Intent())
        val idlingResource = SwipeLayoutRefreshingIdlingResource(activity.postListSwipeRefreshLayout)
        IdlingRegistry.getInstance().register(idlingResource)

        onView(withId(R.id.postListRecyclerView)).perform(RecyclerViewActions.scrollToPosition<PostListAdapter.PostViewHolder>(99))
        onView(withText("at nam consequatur ea labore ea harum")).check(matches(isDisplayed()))
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onView(
            allOf(
                withText("temporibus sit alias delectus eligendi possimus magni"),
                hasSibling(withText("Clementina DuBuque"))
            )
        ).check(matches(isDisplayed()))

        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun shouldSeePostDescriptionWithBodyWhenClickingOnItem() {
        RESTMockServer.whenGET(pathContains("posts")).thenReturnFile(200, "json/posts_response.json")
        RESTMockServer.whenGET(pathContains("users")).thenReturnFile(200, "json/users_response.json")

        val activity = activityRule.launchActivity(Intent())
        val idlingResource = SwipeLayoutRefreshingIdlingResource(activity.postListSwipeRefreshLayout)
        IdlingRegistry.getInstance().register(idlingResource)

        onView(withId(R.id.postListRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition<PostListAdapter.PostViewHolder>(0, click()))

        onView(withText("sunt aut facere repellat provident occaecati excepturi optio reprehenderit")).check(matches(isDisplayed()))
        onView(withText("quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto")).check(matches(isDisplayed()))

        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun shouldMaintainThumbnailFilterAfterRotationInDetailsScreen() {
        RESTMockServer.whenGET(pathContains("posts")).thenReturnFile(200, "json/posts_response.json")
        RESTMockServer.whenGET(pathContains("users")).thenReturnFile(200, "json/users_response.json")

        val activity = activityRule.launchActivity(Intent())

        val idlingResource = SwipeLayoutRefreshingIdlingResource(activity.postListSwipeRefreshLayout)
        IdlingRegistry.getInstance().register(idlingResource)

        onView(withId(R.id.postListRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition<PostListAdapter.PostViewHolder>(0, click()))
        onView(withId(R.id.filter)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onView(withId(R.id.filter)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))

        IdlingRegistry.getInstance().unregister(idlingResource)
    }
}