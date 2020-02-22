package com.phellipesilva.coolposts.postdetails.view

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jakewharton.espresso.OkHttp3IdlingResource
import com.phellipesilva.coolposts.R
import com.phellipesilva.coolposts.di.injector
import com.phellipesilva.coolposts.postlist.data.Post
import com.phellipesilva.coolposts.utils.SwipeLayoutRefreshingIdlingResource
import io.appflate.restmock.RESTMockServer
import io.appflate.restmock.utils.RequestMatchers
import kotlinx.android.synthetic.main.activity_post_details.*
import okhttp3.OkHttpClient
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class PostDetailsActivityTest {

    @Inject
    lateinit var okHttpClient: OkHttpClient

    private lateinit var okHttp3IdlingResource: OkHttp3IdlingResource

    @Before
    fun setUp() {
        RESTMockServer.reset()
        injector.inject(this)

        okHttp3IdlingResource = OkHttp3IdlingResource.create("OkHttp", okHttpClient)
        IdlingRegistry.getInstance().register(okHttp3IdlingResource)
    }

    @After
    fun tearDown() {
        with(IdlingRegistry.getInstance()) {
            resources.forEach {
                unregister(it)
            }
        }
    }

    @Test
    fun shouldShowPostInformationAccordingToIntentThatWasPassedInExtras() {
        val intent = Intent(getApplicationContext(), PostDetailsActivity::class.java)
        intent.putExtra(
            "com.phellipesilva.coolposts.post",
            Post(
                id = 1,
                title = "Title",
                body = "Body",
                userId = 1,
                userName = "Name"
            )
        )

        launch<PostDetailsActivity>(intent)

        onView(withText("Title")).check(matches(isDisplayed()))
        onView(withText("Body")).check(matches(isDisplayed()))
        onView(withId(R.id.authorAvatarImageView)).check(matches(isDisplayed()))
        onView(withId(R.id.postDetailsThumbnailImageView)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldShowLoadingIndicatorWhenOpenActivityForTheFirstTimeWithLongWaitTime() {
        RESTMockServer.whenGET(RequestMatchers.pathContains("comments?postId=1"))
            .thenReturnFile(200, "json/comments_response.json")
            .delay(TimeUnit.SECONDS, 5)

        val intent = Intent(getApplicationContext(), PostDetailsActivity::class.java)
        intent.putExtra(
            "com.phellipesilva.coolposts.post",
            Post(
                id = 1,
                title = "Title",
                body = "Body",
                userId = 1,
                userName = "Name"
            )
        )

        launch<PostDetailsActivity>(intent).onActivity {
            val idlingResource = SwipeLayoutRefreshingIdlingResource(it.postDetailsSwipeRefreshLayout)
            IdlingRegistry.getInstance().register(idlingResource)
            assertTrue(it.postDetailsSwipeRefreshLayout.isRefreshing)
        }
    }

    @Test
    fun shouldFetchCommentsFromSpecificPostWhenStartActivity() {
        RESTMockServer.whenGET(RequestMatchers.pathContains("comments?postId=1")).thenReturnFile(200, "json/comments_response.json")

        val intent = Intent(getApplicationContext(), PostDetailsActivity::class.java)
        intent.putExtra(
            "com.phellipesilva.coolposts.post",
            Post(
                id = 1,
                title = "Title",
                body = "Body",
                userId = 1,
                userName = "Name"
            )
        )

        launch<PostDetailsActivity>(intent).onActivity {
            val idlingResource = SwipeLayoutRefreshingIdlingResource(it.postDetailsSwipeRefreshLayout)
            IdlingRegistry.getInstance().register(idlingResource)
        }

        onView(withText("Eliseo@gardner.biz")).check(matches(isDisplayed()))
        onView(withText("laudantium enim quasi est quidem magnam voluptate ipsam eos\ntempora quo necessitatibus\ndolor quam autem quasi\nreiciendis et nam sapiente accusantium")).check(matches(isDisplayed()))
    }

    @Test
    fun shouldLoadCommentsFromDatabaseWhenOpenActivityForTheSecondTimeAndServiceIsUnavailable() {
        RESTMockServer.whenGET(RequestMatchers.pathContains("comments?postId=1"))
            .thenReturnFile(200, "json/comments_response.json")

        val intent = Intent(getApplicationContext(), PostDetailsActivity::class.java)
        intent.putExtra(
            "com.phellipesilva.coolposts.post",
            Post(
                id = 1,
                title = "Title",
                body = "Body",
                userId = 1,
                userName = "Name"
            )
        )

        val scenario = launch<PostDetailsActivity>(intent).onActivity {
            val idlingResource =
                SwipeLayoutRefreshingIdlingResource(it.postDetailsSwipeRefreshLayout)
            IdlingRegistry.getInstance().register(idlingResource)
        }

        RESTMockServer.reset()
        scenario.recreate()

        onView(withText("Eliseo@gardner.biz")).check(matches(isDisplayed()))
        onView(withText("laudantium enim quasi est quidem magnam voluptate ipsam eos\ntempora quo necessitatibus\ndolor quam autem quasi\nreiciendis et nam sapiente accusantium")).check(matches(isDisplayed()))
    }

    @Test
    fun shouldUpdateCommentsFromSpecificPostWhenSwipeToRefresh() {
        RESTMockServer.whenGET(RequestMatchers.pathContains("comments?postId=1"))
            .thenReturnFile(200, "json/comments_response.json", "json/comments_updated_response.json")

        val intent = Intent(getApplicationContext(), PostDetailsActivity::class.java)
        intent.putExtra(
            "com.phellipesilva.coolposts.post",
            Post(
                id = 1,
                title = "Title",
                body = "Body",
                userId = 1,
                userName = "Name"
            )
        )

        launch<PostDetailsActivity>(intent).onActivity {
            val idlingResource = SwipeLayoutRefreshingIdlingResource(it.postDetailsSwipeRefreshLayout)
            IdlingRegistry.getInstance().register(idlingResource)
        }

        onView(withText("Eliseo@gardner.biz")).check(matches(isDisplayed()))
        onView(withText("laudantium enim quasi est quidem magnam voluptate ipsam eos\ntempora quo necessitatibus\ndolor quam autem quasi\nreiciendis et nam sapiente accusantium")).check(matches(isDisplayed()))

        onView(withId(R.id.postDetailsAppBarLayout)).perform(swipeUp())
        onView(withId(R.id.postDetailsSwipeRefreshLayout)).perform(swipeDown())
        onView(withText("Updated Comment")).check(matches(isDisplayed()))
    }

    @Test
    fun shouldScrollToLastCommentAndSeeContent() {
        RESTMockServer.whenGET(RequestMatchers.pathContains("comments?postId=1")).thenReturnFile(200, "json/comments_response.json")

        val intent = Intent(getApplicationContext(), PostDetailsActivity::class.java)
        intent.putExtra(
            "com.phellipesilva.coolposts.post",
            Post(
                id = 1,
                title = "Title",
                body = "Body",
                userId = 1,
                userName = "Name"
            )
        )

        launch<PostDetailsActivity>(intent)

        onView(withId(R.id.postDetailsAppBarLayout)).perform(swipeUp())
        onView(withId(R.id.postDetailsRecyclerView)).perform(swipeUp())

        onView(withText("Hayden@althea.biz")).check(matches(isDisplayed()))
        onView(withText("harum non quasi et ratione\ntempore iure ex voluptates in ratione\nharum architecto fugit inventore cupiditate\nvoluptates magni quo et")).check(matches(isDisplayed()))
    }

    @Test
    fun shouldMaintainCollapsingToolbarStateAfterRotationWhenItIsCollapsed() {
        RESTMockServer.whenGET(RequestMatchers.pathContains("comments?postId=1")).thenReturnFile(200, "json/comments_response.json")

        val intent = Intent(getApplicationContext(), PostDetailsActivity::class.java)
        intent.putExtra(
            "com.phellipesilva.coolposts.post",
            Post(
                id = 1,
                title = "Title",
                body = "Body",
                userId = 1,
                userName = "Name"
            )
        )
        val scenario = launch<PostDetailsActivity>(intent)

        onView(withId(R.id.postDetailsAppBarLayout)).perform(swipeUp())

        scenario.onActivity {
            it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        onView(withText("Title")).check(matches(not(isDisplayed())))
        onView(withText("Body")).check(matches(not(isDisplayed())))
        onView(withId(R.id.authorAvatarImageView)).check(matches(not(isDisplayed())))
    }
}