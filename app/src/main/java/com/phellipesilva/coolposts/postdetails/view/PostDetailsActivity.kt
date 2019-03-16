package com.phellipesilva.coolposts.postdetails.view

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.phellipesilva.coolposts.R
import com.phellipesilva.coolposts.di.injector
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
        window.enterTransition = null

        val post = intent.getParcelableExtra<Post>("post")
        setupsCollapsingToolbar(post)
        initViewModel(savedInstanceState, post.id)
        initViewStateObserver()
        initRecyclerView(post.id)
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

    private fun initRecyclerView(postId: Int) {
        val adapter = CommentsAdapter(this)
        postDetailsRecyclerView.adapter = adapter

        postDetailsViewModel.getCommentsObservable(postId).observe(this, Observer { commentList ->
            commentList?.let { adapter.updateData(commentList) }
        })
    }

    private fun initViewModel(savedInstanceState: Bundle?, postId: Int) {
        val postDetailsViewModelFactory = injector.getPostDetailsViewModelFactory()
        postDetailsViewModel = ViewModelProviders.of(this, postDetailsViewModelFactory)
            .get(PostDetailsViewModel::class.java)

        if (savedInstanceState == null)
            postDetailsViewModel.fetchComments(postId)
    }

    private fun initViewStateObserver() {
        postDetailsViewModel.viewState().observe(this, Observer {
            when (it.peekContent()) {
                ViewState.IDLE -> { }
                ViewState.UNEXPECTED_ERROR -> Toast.makeText(
                    this,
                    "Error",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun setupsCollapsingToolbar(post: Post) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""

        supportPostponeEnterTransition()
        toolbarImageView.load(
            url = "https://picsum.photos/300/300/?image=${post.id}",
            onLoadingFinished = { supportStartPostponedEnterTransition() }
        )

        authorAvatarImageView.load(
            url = "https://api.adorable.io/avatars/${post.user.userId}",
            rounded = true,
            onLoadingFinished = { supportStartPostponedEnterTransition() }
        )

        postBodyTextView.text = post.body
        postTitleTextView.text = post.title
    }
}