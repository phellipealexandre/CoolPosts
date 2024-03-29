package com.phellipesilva.coolposts.postdetails.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.transition.addListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.phellipesilva.coolposts.R
import com.phellipesilva.coolposts.di.injector
import com.phellipesilva.coolposts.exceptions.NoConnectionException
import com.phellipesilva.coolposts.extensions.fadeIn
import com.phellipesilva.coolposts.extensions.loadRoundedAvatar
import com.phellipesilva.coolposts.extensions.loadThumbnail
import com.phellipesilva.coolposts.postdetails.di.PostDetailsModule
import com.phellipesilva.coolposts.postdetails.domain.Comment
import com.phellipesilva.coolposts.postdetails.viewmodel.PostDetailsViewModel
import com.phellipesilva.coolposts.postlist.domain.Post

class PostDetailsActivity : AppCompatActivity() {

    private val filterVisibilityId = "FilterVisibility"

    private val post by lazy { intent.getParcelableExtra<Post>(postIdIntentKey)!! }

    private val filter: View by lazy { findViewById(R.id.filter) }
    private val postBodyTextView: TextView by lazy { findViewById(R.id.postBodyTextView) }
    private val postTitleTextView: TextView by lazy { findViewById(R.id.postTitleTextView) }
    private val postDetailsToolbar: Toolbar by lazy { findViewById(R.id.postDetailsToolbar) }
    private val postDetailsThumbnailImageView: ImageView by lazy { findViewById(R.id.postDetailsThumbnailImageView) }
    private val authorAvatarImageView: ImageView by lazy { findViewById(R.id.authorAvatarImageView) }
    private val postDetailsRecyclerView: RecyclerView by lazy { findViewById(R.id.postDetailsRecyclerView) }
    private val postDetailsSwipeRefreshLayout: SwipeRefreshLayout by lazy { findViewById(R.id.postDetailsSwipeRefreshLayout) }
    private val postDetailsCoordinatorLayout: CoordinatorLayout by lazy { findViewById(R.id.postDetailsCoordinatorLayout) }

    private val postDetailsViewModel by lazy {
        val postDetailsViewModelFactory = injector.with(PostDetailsModule(post.id)).getPostDetailsViewModelFactory()
        ViewModelProvider(this, postDetailsViewModelFactory).get(PostDetailsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)

        setupCollapsingToolbar(post)
        initRecyclerView()
        initViewStateObserver()
        initSwipeLayout(post)

        if (savedInstanceState == null)
            postDetailsViewModel.updateCommentsFromPost(post.id)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                leaveActivityWithSceneTransition()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        leaveActivityWithSceneTransition()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(filterVisibilityId, filter.visibility)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        filter.visibility = savedInstanceState.getInt(filterVisibilityId)
        super.onRestoreInstanceState(savedInstanceState)
    }

    private fun leaveActivityWithSceneTransition() {
        postBodyTextView.visibility = View.INVISIBLE
        postTitleTextView.visibility = View.INVISIBLE
        filter.visibility = View.INVISIBLE
        supportFinishAfterTransition()
    }

    private fun setupCollapsingToolbar(post: Post) {
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
        postDetailsViewModel.viewState().observe(this, Observer { state ->
            state?.let(::processViewState)
        })
    }

    private fun processViewState(state: PostDetailsViewState) {
        postDetailsSwipeRefreshLayout.isRefreshing = state.isLoading
        renderComments(state.comments)
        state.errorEvent?.getContentIfNotHandled()?.let(::renderError)
    }

    private fun renderComments(comments: List<Comment>) {
        val commentsAdapter = postDetailsRecyclerView.adapter as CommentsAdapter
        commentsAdapter.submitList(comments)
    }

    private fun renderError(throwable: Throwable?) {
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
        private const val postIdIntentKey =  "com.phellipesilva.coolposts.post"

        fun newNavigationIntent(context: Context, post: Post): Intent {
            val intent = Intent(context, PostDetailsActivity::class.java)
            intent.putExtra(postIdIntentKey, post)

            return intent
        }
    }
}
