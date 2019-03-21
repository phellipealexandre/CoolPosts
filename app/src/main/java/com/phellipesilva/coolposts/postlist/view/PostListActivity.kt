package com.phellipesilva.coolposts.postlist.view

import android.app.ActivityOptions
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.phellipesilva.coolposts.R
import com.phellipesilva.coolposts.di.injector
import com.phellipesilva.coolposts.exceptions.NoConnectionException
import com.phellipesilva.coolposts.extensions.AndroidTransitionPair
import com.phellipesilva.coolposts.postdetails.view.PostDetailsActivity
import com.phellipesilva.coolposts.postlist.data.Post
import com.phellipesilva.coolposts.postlist.viewmodel.PostListViewModel
import com.phellipesilva.coolposts.state.ViewState
import kotlinx.android.synthetic.main.activity_post_list.*

class PostListActivity : AppCompatActivity() {

    private val postListViewModel by lazy {
        ViewModelProviders.of(this, injector.getPostListViewModelFactory()).get(PostListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_list)

        initRecyclerView()
        initViewStateObserver()
        initSwipeLayout()

        if (savedInstanceState == null)
            postListViewModel.updatePosts()
    }

    private fun initRecyclerView() {
        val adapter = PostListAdapter()
        adapter.setOnItemClickListener(::navigateToPostDetailsWithTransition)

        postListRecyclerView.adapter = adapter
    }

    private fun initViewStateObserver() {
        postListViewModel.viewState().observe(this, Observer { state ->
            when (state.viewState) {
                ViewState.LOADING -> {
                    postListSwipeRefreshLayout.isRefreshing = true
                }
                ViewState.ERROR -> {
                    processError(state.throwable?.getContentIfNotHandled())
                }
                ViewState.SUCCESS -> {
                    processSuccess(state.posts)
                }
            }
        })
    }

    private fun processSuccess(posts: List<Post>?) {
        postListSwipeRefreshLayout.isRefreshing = false

        val postListAdapter = postListRecyclerView.adapter as PostListAdapter
        posts?.let(postListAdapter::submitList)
    }

    private fun processError(throwable: Throwable?) {
        postListSwipeRefreshLayout.isRefreshing = false

        throwable?.let {
            if (throwable is NoConnectionException) {
                Snackbar.make(postListCoordinatorLayout, R.string.no_connection_msg, Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(postListCoordinatorLayout, R.string.unexpected_error_msg, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun initSwipeLayout() {
        postListSwipeRefreshLayout.setOnRefreshListener {
            postListViewModel.updatePosts()
        }
    }

    private fun navigateToPostDetailsWithTransition(transitionElements: Array<AndroidTransitionPair>, post: Post) {
        val mutableList = transitionElements.toMutableList()
        mutableList.add(AndroidTransitionPair(postListAppBarLayout, getString(R.string.post_list_appbarlayout_transition_id)))

        findViewById<View>(android.R.id.navigationBarBackground)?.let {
            mutableList.add(AndroidTransitionPair(it, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME))
        }

        findViewById<View>(android.R.id.statusBarBackground)?.let {
            mutableList.add(AndroidTransitionPair(it, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME))
        }

        val intent = PostDetailsActivity.newIntent(this, post = post)
        val options = ActivityOptions.makeSceneTransitionAnimation(this, *mutableList.toTypedArray())
        startActivity(intent, options.toBundle())
    }
}
