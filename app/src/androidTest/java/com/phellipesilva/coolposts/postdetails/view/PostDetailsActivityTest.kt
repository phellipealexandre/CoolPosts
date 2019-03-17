package com.phellipesilva.coolposts.postdetails.view

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.phellipesilva.coolposts.R
import com.phellipesilva.coolposts.postlist.data.Post
import com.phellipesilva.coolposts.postlist.data.User
import io.appflate.restmock.RESTMockServer
import io.appflate.restmock.utils.RequestMatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PostDetailsActivityTest {

    @get:Rule
    var activityRule: ActivityTestRule<PostDetailsActivity> = ActivityTestRule(PostDetailsActivity::class.java, false, false)

    @Before
    fun setUp() {
        RESTMockServer.reset()
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
                user = User(1, "Name", "Website")
            )
        )
        activityRule.launchActivity(intent)

        onView(withText("Title")).check(matches(isDisplayed()))
        onView(withText("Body")).check(matches(isDisplayed()))
        onView(withId(R.id.authorAvatarImageView)).check(matches(isDisplayed()))
        onView(withId(R.id.toolbarImageView)).check(matches(isDisplayed()))
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
                user = User(1, "Name", "Website")
            )
        )
        activityRule.launchActivity(intent)

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
                user = User(1, "Name", "Website")
            )
        )
        activityRule.launchActivity(intent)
        onView(withId(R.id.postDetailsAppBarLayout)).perform(swipeUp())
        onView(withId(R.id.postDetailsRecyclerView)).perform(swipeUp())

        onView(withText("Hayden@althea.biz")).check(matches(isDisplayed()))
        onView(withText("harum non quasi et ratione\ntempore iure ex voluptates in ratione\nharum architecto fugit inventore cupiditate\nvoluptates magni quo et")).check(matches(isDisplayed()))
    }
}