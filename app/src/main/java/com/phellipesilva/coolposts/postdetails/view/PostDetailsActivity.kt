package com.phellipesilva.coolposts.postdetails.view

import android.content.Context
import android.content.Intent
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
import com.phellipesilva.coolposts.exceptions.NoConnectionException
import com.phellipesilva.coolposts.extensions.fadeIn
import com.phellipesilva.coolposts.extensions.loadRoundedAvatar
import com.phellipesilva.coolposts.extensions.loadThumbnail
import com.phellipesilva.coolposts.postdetails.data.Comment
import com.phellipesilva.coolposts.postdetails.di.PostDetailsModule
import com.phellipesilva.coolposts.postdetails.viewmodel.PostDetailsViewModel
import com.phellipesilva.coolposts.postlist.data.Post
import com.phellipesilva.coolposts.state.ViewState
import kotlinx.android.synthetic.main.activity_post_details.*

class PostDetailsActivity : AppCompatActivity() {

    private val filterVisibilityId = "FilterVisibility"

    private val postDetailsViewModel by lazy {
        val post = intent.getParcelableExtra<Post>(PostDetailsActivity.postId)
        val postDetailsViewModelFactory = injector.with(PostDetailsModule(post.id)).getPostDetailsViewModelFactory()
        ViewModelProviders.of(this, postDetailsViewModelFactory).get(PostDetailsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)

        val post = intent.getParcelableExtra<Post>(PostDetailsActivity.postId)

        setupsCollapsingToolbar(post)
        initRecyclerView()
        initViewStateObserver()
        initSwipeLayout(post)

        if (savedInstanceState == null)
            postDetailsViewModel.updateCommentsFromPost(post.id)
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
        outState.putInt(filterVisibilityId, filter.visibility)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        filter.visibility = savedInstanceState?.getInt(filterVisibilityId) ?: View.VISIBLE
        super.onRestoreInstanceState(savedInstanceState)
    }

    private fun leaveActivityWithSceneTransition() {
        postBodyTextView.visibility = View.INVISIBLE
        postTitleTextView.visibility = View.INVISIBLE
        filter.visibility = View.INVISIBLE
        supportFinishAfterTransition()
    }

    private fun setupsCollapsingToolbar(post: Post) {
        setSupportActionBar(postDetailsToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        postBodyTextView.text = post.body
        postTitleTextView.text = post.title

        supportPostponeEnterTransition()
        postDetailsThumbnailImageView.loadThumbnail(
            id = post.id,
            withCrossFade = false,
            onLoadingFinished = { supportStartPostponedEnterTransition() }
        )

        authorAvatarImageView.loadRoundedAvatar(
            userId = post.userId,
            withCrossFade = false,
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

    private fun initRecyclerView() {
        val adapter = CommentsAdapter()
        postDetailsRecyclerView.adapter = adapter
    }

    private fun initViewStateObserver() {
        postDetailsViewModel.viewState().observe(this, Observer {
            it?.let(::processEvent)
        })
    }

    private fun processEvent(event: PostDetailsViewState) {
        when (event.viewState) {
            ViewState.SUCCESS -> {
                processSuccess(event.comments)
            }
            ViewState.ERROR -> {
                processError(event.throwable?.getContentIfNotHandled())
            }
            ViewState.LOADING -> {
                postDetailsSwipeRefreshLayout.isRefreshing = true
            }
        }
    }

    private fun processSuccess(comments: List<Comment>?) {
        postDetailsSwipeRefreshLayout.isRefreshing = false

        val commentsAdapter = postDetailsRecyclerView.adapter as CommentsAdapter
        comments?.let(commentsAdapter::submitList)
    }

    private fun processError(throwable: Throwable?) {
        postDetailsSwipeRefreshLayout.isRefreshing = false
        throwable?.let {
            postDetailsCoordinatorLayout.post {
                if (throwable is NoConnectionException) {
                    Snackbar.make(postDetailsCoordinatorLayout, R.string.no_connection_msg, Snackbar.LENGTH_LONG).show()
                } else {
                    Snackbar.make(postDetailsCoordinatorLayout, R.string.unexpected_error_msg, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun initSwipeLayout(post: Post) {
        postDetailsSwipeRefreshLayout.setOnRefreshListener {
            postDetailsViewModel.updateCommentsFromPost(post.id)
        }
    }

    companion object {
        private const val postId =  "post"

        fun newIntent(context: Context, post: Post): Intent {
            val intent = Intent(context, PostDetailsActivity::class.java)
            intent.putExtra(postId, post)

            return intent
        }
    }
}
