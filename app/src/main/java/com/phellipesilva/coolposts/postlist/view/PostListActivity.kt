package com.phellipesilva.coolposts.postlist.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.phellipesilva.coolposts.R
import com.phellipesilva.coolposts.di.injector
import com.phellipesilva.coolposts.postlist.viewmodel.PostListViewModel
import com.phellipesilva.coolposts.state.ViewState
import kotlinx.android.synthetic.main.activity_post_list.*

class PostListActivity : AppCompatActivity() {

    private lateinit var postListViewModel: PostListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_list)

        initViewModel()
        initRecyclerView(savedInstanceState)
        initViewStateObserver()
        initSwipeLayout()
    }

    private fun initViewModel() {
        val postListViewModelFactory = injector.getPostListViewModelFactory()
        postListViewModel = ViewModelProviders.of(this, postListViewModelFactory).get(PostListViewModel::class.java)
    }

    private fun initRecyclerView(savedInstanceState: Bundle?) {
        val adapter = PostListAdapter(this)
        recyclerView.adapter = adapter

        postListViewModel.getPostsObservable().observe(this, Observer { postList ->
            val isFirstUse = postList.isNullOrEmpty() && savedInstanceState == null

            if (isFirstUse) {
                swipeRefreshLayout.isRefreshing = true
                postListViewModel.fetchPosts()
            } else {
                adapter.submitList(postList)
            }
        })
    }

    private fun initViewStateObserver() {
        postListViewModel.viewState().observe(this, Observer {
            val event = it.peekContent()
            swipeRefreshLayout.isRefreshing = false

            when (event) {
                ViewState.UNEXPECTED_ERROR -> {
                    Snackbar.make(postListCoordinatorLayout, R.string.unexpected_error_msg, Snackbar.LENGTH_LONG).show()
                }
                ViewState.NO_INTERNET -> {
                    Snackbar.make(postListCoordinatorLayout, R.string.no_connection_msg, Snackbar.LENGTH_LONG).show()
                }
                else -> {}
            }
        })
    }

    private fun initSwipeLayout() {
        swipeRefreshLayout.setOnRefreshListener {
            postListViewModel.fetchPosts()
        }
    }
}
