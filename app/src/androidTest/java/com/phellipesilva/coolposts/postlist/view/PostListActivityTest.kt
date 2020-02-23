package com.phellipesilva.coolposts.postlist.view

import android.content.pm.ActivityInfo
import androidx.test.core.app.ActivityScenario.launch
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
import com.phellipesilva.coolposts.R
import com.phellipesilva.coolposts.utils.SwipeLayoutRefreshingIdlingResource
import io.appflate.restmock.RESTMockServer
import io.appflate.restmock.utils.RequestMatchers.pathContains
import junit.framework.Assert.assertTrue
import kotlinx.android.synthetic.main.activity_post_list.*
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class PostListActivityTest {

    @Before
    fun setUp() {
        RESTMockServer.reset()
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
    fun shouldFetchPostsWhenOpenActivityForTheFirstTime() {
        RESTMockServer
            .whenGET(pathContains("posts"))
            .thenReturnFile(200, "json/posts_response.json")
        RESTMockServer
            .whenGET(pathContains("users"))
            .thenReturnFile(200, "json/users_response.json")

        launch(PostListActivity::class.java).onActivity {
            val idlingResource = SwipeLayoutRefreshingIdlingResource(it.postListSwipeRefreshLayout)
            IdlingRegistry.getInstance().register(idlingResource)
        }

        onView(
            allOf(
                withText("sunt aut facere repellat provident occaecati excepturi optio reprehenderit"),
                hasSibling(withText("Leanne Graham"))
            )
        ).check(matches(isDisplayed()))
    }

    @Test
    fun shouldShowLoadingIndicatorWhenOpenActivityForTheFirstTimeWithLongWaitTime() {
        RESTMockServer.whenGET(pathContains("posts"))
            .thenReturnFile(200, "json/posts_response.json")
            .delayBody(TimeUnit.SECONDS, 5)
        RESTMockServer.whenGET(pathContains("users"))
            .thenReturnFile(200, "json/users_response.json")

        launch(PostListActivity::class.java).onActivity {
            val idlingResource = SwipeLayoutRefreshingIdlingResource(it.postListSwipeRefreshLayout)
            IdlingRegistry.getInstance().register(idlingResource)
            assertTrue(it.postListSwipeRefreshLayout.isRefreshing)
        }
    }

    @Test
    fun shouldLoadPostsFromDatabaseWhenOpenActivityForTheSecondTimeAndServiceIsUnavailable() {
        RESTMockServer
            .whenGET(pathContains("posts"))
            .thenReturnFile(200, "json/posts_response.json")
        RESTMockServer
            .whenGET(pathContains("users"))
            .thenReturnFile(200, "json/users_response.json")

        val scenario = launch(PostListActivity::class.java).onActivity {
            val idlingResource = SwipeLayoutRefreshingIdlingResource(it.postListSwipeRefreshLayout)
            IdlingRegistry.getInstance().register(idlingResource)
        }

        RESTMockServer.reset()
        scenario.recreate()

        onView(
            allOf(
                withText("sunt aut facere repellat provident occaecati excepturi optio reprehenderit"),
                hasSibling(withText("Leanne Graham"))
            )
        ).check(matches(isDisplayed()))
    }

    @Test
    fun shouldUpdateSecondPostTitleWhenOpenActivityAndRefreshAgain() {
        RESTMockServer
            .whenGET(pathContains("posts"))
            .thenReturnFile(200, "json/posts_response.json")
            .thenReturnFile(200, "json/posts_updated_response.json")
        RESTMockServer
            .whenGET(pathContains("users"))
            .thenReturnFile(200, "json/users_response.json")

        launch(PostListActivity::class.java).onActivity {
            val idlingResource = SwipeLayoutRefreshingIdlingResource(it.postListSwipeRefreshLayout)
            IdlingRegistry.getInstance().register(idlingResource)
        }

        onView(withText("sunt aut facere repellat provident occaecati excepturi optio reprehenderit")).check(matches(isDisplayed()))

        onView(withId(R.id.postListSwipeRefreshLayout)).perform(swipeDown())

        onView(withText("sunt aut facere repellat provident occaecati excepturi optio reprehenderit")).check(doesNotExist())
        onView(withText("Updated Title")).check(matches(isDisplayed()))
    }

    @Test
    fun shouldScrollToBottomPosition() {
        RESTMockServer
            .whenGET(pathContains("posts"))
            .thenReturnFile(200, "json/posts_response.json")
        RESTMockServer
            .whenGET(pathContains("users"))
            .thenReturnFile(200, "json/users_response.json")

        launch(PostListActivity::class.java).onActivity {
            val idlingResource = SwipeLayoutRefreshingIdlingResource(it.postListSwipeRefreshLayout)
            IdlingRegistry.getInstance().register(idlingResource)
        }

        onView(withId(R.id.postListRecyclerView)).perform(RecyclerViewActions.scrollToPosition<PostsAdapter.PostViewHolder>(99))
        onView(withText("at nam consequatur ea labore ea harum")).check(matches(isDisplayed()))
    }

    @Test
    fun shouldMaintainScrollPositionAfterScreenOrientationChanges() {
        RESTMockServer
            .whenGET(pathContains("posts"))
            .thenReturnFile(200, "json/posts_response.json")
        RESTMockServer
            .whenGET(pathContains("users"))
            .thenReturnFile(200, "json/users_response.json")

        val scenario = launch(PostListActivity::class.java).onActivity {
            val idlingResource = SwipeLayoutRefreshingIdlingResource(it.postListSwipeRefreshLayout)
            IdlingRegistry.getInstance().register(idlingResource)
        }

        onView(withId(R.id.postListRecyclerView)).perform(RecyclerViewActions.scrollToPosition<PostsAdapter.PostViewHolder>(99))
        onView(withText("at nam consequatur ea labore ea harum")).check(matches(isDisplayed()))

        scenario.onActivity {
            it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        onView(
            allOf(
                withText("temporibus sit alias delectus eligendi possimus magni"),
                hasSibling(withText("Clementina DuBuque"))
            )
        ).check(matches(isDisplayed()))
    }

    @Test
    fun shouldSeePostDescriptionWithBodyWhenClickingOnItem() {
        RESTMockServer
            .whenGET(pathContains("posts"))
            .thenReturnFile(200, "json/posts_response.json")
        RESTMockServer
            .whenGET(pathContains("users"))
            .thenReturnFile(200, "json/users_response.json")

        launch(PostListActivity::class.java).onActivity {
            val idlingResource = SwipeLayoutRefreshingIdlingResource(it.postListSwipeRefreshLayout)
            IdlingRegistry.getInstance().register(idlingResource)
        }

        onView(withId(R.id.postListRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition<PostsAdapter.PostViewHolder>(0, click()))

        onView(withText("sunt aut facere repellat provident occaecati excepturi optio reprehenderit")).check(matches(isDisplayed()))
        onView(withText("quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto")).check(matches(isDisplayed()))
    }

    @Test
    fun shouldMaintainThumbnailFilterAfterRotationInDetailsScreen() {
        RESTMockServer
            .whenGET(pathContains("posts"))
            .thenReturnFile(200, "json/posts_response.json")
        RESTMockServer
            .whenGET(pathContains("users"))
            .thenReturnFile(200, "json/users_response.json")

        val scenario = launch(PostListActivity::class.java).onActivity {
            val idlingResource = SwipeLayoutRefreshingIdlingResource(it.postListSwipeRefreshLayout)
            IdlingRegistry.getInstance().register(idlingResource)
        }

        onView(withId(R.id.postListRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition<PostsAdapter.PostViewHolder>(0, click()))
        onView(withId(R.id.filter)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        scenario.onActivity {
            it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        onView(withId(R.id.filter)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }
}