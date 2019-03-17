package com.phellipesilva.coolposts.postdetails.view

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.phellipesilva.coolposts.R
import com.phellipesilva.coolposts.di.injector
import com.phellipesilva.coolposts.extensions.fadeIn
import com.phellipesilva.coolposts.extensions.load
import com.phellipesilva.coolposts.postdetails.viewmodel.PostDetailsViewModel
import com.phellipesilva.coolposts.postlist.data.Post
import com.phellipesilva.coolposts.state.ViewState
import kotlinx.android.synthetic.main.activity_post_details.*

class PostDetailsActivity : AppCompatActivity() {

    private lateinit var postDetailsViewModel: PostDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)

        val post = intent.getParcelableExtra<Post>("post")
        setupsCollapsingToolbar(post)
        initViewModel()
        initViewStateObserver()
        initRecyclerView(savedInstanceState, post.id)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        supportFinishAfterTransition()
        super.onBackPressed()
    }

    private fun initRecyclerView(savedInstanceState: Bundle?, postId: Int) {
        val adapter = CommentsAdapter()
        postDetailsRecyclerView.adapter = adapter

        postDetailsViewModel.getCommentsObservable(postId).observe(this, Observer { commentList ->
            val isFirstUse = commentList.isNullOrEmpty() && savedInstanceState == null

            if (isFirstUse) {
                postDetailsViewModel.fetchComments(postId)
            } else {
                adapter.submitList(commentList)
            }
        })
    }

    private fun initViewModel() {
        val postDetailsViewModelFactory = injector.getPostDetailsViewModelFactory()
        postDetailsViewModel = ViewModelProviders.of(this, postDetailsViewModelFactory).get(PostDetailsViewModel::class.java)
    }

    private fun initViewStateObserver() {
        postDetailsViewModel.viewState().observe(this, Observer {
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
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        collapsingToolbar.title = post.title

        supportPostponeEnterTransition()
        toolbarImageView.load(
            url = "https://picsum.photos/400/400/?image=${post.id}",
            onLoadingFinished = { supportStartPostponedEnterTransition() }
        )

        authorAvatarImageView.load(
            url = "https://api.adorable.io/avatars/${post.user.userId}",
            rounded = true,
            onLoadingFinished = { supportStartPostponedEnterTransition() }
        )

        postBodyTextView.text = post.body
        postBodyTextView.fadeIn()
        postTitleTextView.text = post.title
        postTitleTextView.fadeIn()
        filter.fadeIn()
    }
}
