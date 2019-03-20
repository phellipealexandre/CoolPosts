package com.phellipesilva.coolposts.postdetails.view

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.transition.addListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.phellipesilva.coolposts.R
import com.phellipesilva.coolposts.di.injector
import com.phellipesilva.coolposts.extensions.fadeIn
import com.phellipesilva.coolposts.extensions.load
import com.phellipesilva.coolposts.navigation.PostNavigator
import com.phellipesilva.coolposts.postdetails.viewmodel.PostDetailsViewModel
import com.phellipesilva.coolposts.postlist.data.Post
import com.phellipesilva.coolposts.state.ViewState
import kotlinx.android.synthetic.main.activity_post_details.*

class PostDetailsActivity : AppCompatActivity() {

    private val postDetailsViewModel by lazy {
        ViewModelProviders.of(this, injector.getPostDetailsViewModelFactory()).get(PostDetailsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)

        val post = intent.getParcelableExtra<Post>(PostNavigator.postId)
        setupsCollapsingToolbar(post)
        initViewStateObserver()
        initRecyclerView(savedInstanceState, post.id)
        initSwipeLayout(post)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                leaveActivityWithSceneTransition()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        leaveActivityWithSceneTransition()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("FilterVisibility", filter.visibility)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        filter.visibility = savedInstanceState?.getInt("FilterVisibility") ?: View.VISIBLE
        super.onRestoreInstanceState(savedInstanceState)
    }

    private fun initRecyclerView(savedInstanceState: Bundle?, postId: Int) {
        val adapter = CommentsAdapter()
        postDetailsRecyclerView.adapter = adapter

        postDetailsViewModel.getCommentsFromPost(postId).observe(this, Observer { commentList ->
            val isFirstUse = commentList.isNullOrEmpty() && savedInstanceState == null

            if (isFirstUse) {
                postDetailsSwipeRefreshLayout.isRefreshing = true
                postDetailsViewModel.updateCommentsFromPost(postId)
            } else {
                adapter.submitList(commentList)
            }
        })
    }

    private fun initViewStateObserver() {
        postDetailsViewModel.viewState().observe(this, Observer {
            postDetailsSwipeRefreshLayout.isRefreshing = false
            val event = it.peekContent()
            when (event) {
                ViewState.UNEXPECTED_ERROR -> {
                    Snackbar.make(postDetailsCoordinatorLayout, R.string.unexpected_error_msg, Snackbar.LENGTH_LONG).show()
                }
                ViewState.NO_INTERNET -> {
                    Snackbar.make(postDetailsCoordinatorLayout, R.string.no_connection_msg, Snackbar.LENGTH_LONG).show()
                }
                else -> {}
            }
        })
    }

    private fun setupsCollapsingToolbar(post: Post) {
        setSupportActionBar(postDetailsToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        postBodyTextView.text = post.body
        postTitleTextView.text = post.title

        supportPostponeEnterTransition()
        postDetailsThumbnailImageView.load(
            url = "https://picsum.photos/400/400/?image=${post.id}",
            onLoadingFinished = { supportStartPostponedEnterTransition() }
        )

        authorAvatarImageView.load(
            url = "https://api.adorable.io/avatars/${post.user.userId}",
            rounded = true,
            onLoadingFinished = { supportStartPostponedEnterTransition() }
        )

        window.sharedElementEnterTransition.addListener(
            onEnd = {
                postBodyTextView.fadeIn()
                postTitleTextView.fadeIn()
                filter.fadeIn()
            }
        )
    }

    private fun leaveActivityWithSceneTransition() {
        postBodyTextView.visibility = View.INVISIBLE
        postTitleTextView.visibility = View.INVISIBLE
        filter.visibility = View.INVISIBLE
        supportFinishAfterTransition()
    }

    private fun initSwipeLayout(post: Post) {
        postDetailsSwipeRefreshLayout.setOnRefreshListener {
            postDetailsViewModel.updateCommentsFromPost(post.id)
        }
    }
}
