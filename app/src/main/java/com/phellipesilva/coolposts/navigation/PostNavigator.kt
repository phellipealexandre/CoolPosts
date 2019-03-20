package com.phellipesilva.coolposts.navigation

import android.app.ActivityOptions
import android.content.Intent
import android.view.View
import android.view.Window
import com.phellipesilva.coolposts.R
import com.phellipesilva.coolposts.postdetails.view.PostDetailsActivity
import com.phellipesilva.coolposts.postlist.data.Post
import com.phellipesilva.coolposts.postlist.view.AndroidTransitionPair
import com.phellipesilva.coolposts.postlist.view.PostListActivity
import kotlinx.android.synthetic.main.activity_post_list.*
import javax.inject.Inject

class PostNavigator @Inject constructor(){

    companion object {
        const val postId = "post"
    }

    fun navigateToPostDetails(activity: PostListActivity, transitionElements: Array<AndroidTransitionPair>, post: Post) {
        val intent = Intent(activity, PostDetailsActivity::class.java)
        intent.putExtra(postId, post)

        val mutableList = transitionElements.toMutableList()
        mutableList.add(AndroidTransitionPair(activity.postListAppBarLayout, activity.getString(R.string.post_list_appbarlayout_transition_id)))

        activity.findViewById<View>(android.R.id.navigationBarBackground)?.let {
            mutableList.add(AndroidTransitionPair(it, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME))
        }

        activity.findViewById<View>(android.R.id.statusBarBackground)?.let {
            mutableList.add(AndroidTransitionPair(it, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME))
        }

        val options = ActivityOptions.makeSceneTransitionAnimation(activity, *mutableList.toTypedArray())
        activity.startActivity(intent, options.toBundle())
    }
}