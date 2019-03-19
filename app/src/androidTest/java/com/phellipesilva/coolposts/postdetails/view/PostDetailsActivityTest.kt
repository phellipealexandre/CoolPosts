package com.phellipesilva.coolposts.postdetails.view

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.jakewharton.espresso.OkHttp3IdlingResource
import com.phellipesilva.coolposts.R
import com.phellipesilva.coolposts.di.injector
import com.phellipesilva.coolposts.postlist.data.Post
import com.phellipesilva.coolposts.postlist.data.User
import io.appflate.restmock.RESTMockServer
import io.appflate.restmock.utils.RequestMatchers
import okhttp3.OkHttpClient
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


@RunWith(AndroidJUnit4::class)
class PostDetailsActivityTest {

    @get:Rule
    var activityRule: ActivityTestRule<PostDetailsActivity> = ActivityTestRule(PostDetailsActivity::class.java, false, false)

    @Inject
    lateinit var okHttpClient: OkHttpClient

    lateinit var okHttp3IdlingResource: OkHttp3IdlingResource

    @Before
    fun setUp() {
        RESTMockServer.reset()
        injector.inject(this)
        okHttp3IdlingResource = OkHttp3IdlingResource.create("OkHttp", okHttpClient)
        IdlingRegistry.getInstance().register(okHttp3IdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(okHttp3IdlingResource)
    }

    @Test
    fun shouldShowPostInformationAccordingToIntentThtWasPassedInExtras() {
        val intent = Intent()
        intent.putExtra(
            "post",
            Post(
                id = 1,
                title = "Title",
                body = "Body",
                user = User(1, "Name")
            )
        )
        activityRule.launchActivity(intent)

        onView(withText("Title")).check(matches(isDisplayed()))
        onView(withText("Body")).check(matches(isDisplayed()))
        onView(withId(R.id.authorAvatarImageView)).check(matches(isDisplayed()))
        onView(withId(R.id.postDetailsThumbnailImageView)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldFetchCommentsFromSpecificPostWhenStartActivity() {
        RESTMockServer.whenGET(RequestMatchers.pathContains("comments?postId=1")).thenReturnFile(200, "json/comments_response.json")

        val intent = Intent()
        intent.putExtra(
            "post",
            Post(
                id = 1,
                title = "Title",
                body = "Body",
                user = User(1, "Name")
            )
        )
        activityRule.launchActivity(intent)

        Thread.sleep(1000)

        onView(withText("Eliseo@gardner.biz")).check(matches(isDisplayed()))
        onView(withText("laudantium enim quasi est quidem magnam voluptate ipsam eos\ntempora quo necessitatibus\ndolor quam autem quasi\nreiciendis et nam sapiente accusantium")).check(matches(isDisplayed()))
    }

    @Test
    fun shouldScrollToLastCommentAndSeeContent() {
        RESTMockServer.whenGET(RequestMatchers.pathContains("comments?postId=1")).thenReturnFile(200, "json/comments_response.json")

        val intent = Intent()
        intent.putExtra(
            "post",
            Post(
                id = 1,
                title = "Title",
                body = "Body",
                user = User(1, "Name")
            )
        )
        activityRule.launchActivity(intent)
        onView(withId(R.id.postDetailsAppBarLayout)).perform(swipeUp())
        onView(withId(R.id.postDetailsRecyclerView)).perform(swipeUp())

        onView(withText("Hayden@althea.biz")).check(matches(isDisplayed()))
        onView(withText("harum non quasi et ratione\ntempore iure ex voluptates in ratione\nharum architecto fugit inventore cupiditate\nvoluptates magni quo et")).check(matches(isDisplayed()))
    }

    @Test
    fun shouldMaintainCollapsingToolbarStateAfterRotationWhenItIsCollapsed() {
        RESTMockServer.whenGET(RequestMatchers.pathContains("comments?postId=1")).thenReturnFile(200, "json/comments_response.json")

        val intent = Intent()
        intent.putExtra(
            "post",
            Post(
                id = 1,
                title = "Title",
                body = "Body",
                user = User(1, "Name")
            )
        )
        val activity = activityRule.launchActivity(intent)
        onView(withId(R.id.postDetailsAppBarLayout)).perform(swipeUp())

        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        onView(withText("Title")).check(matches(not(isDisplayed())))
        onView(withText("Body")).check(matches(not(isDisplayed())))
        onView(withId(R.id.authorAvatarImageView)).check(matches(not(isDisplayed())))
    }
}