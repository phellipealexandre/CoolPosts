package com.phellipesilva.coolposts.postlist.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.phellipesilva.coolposts.R
import com.phellipesilva.coolposts.di.injector
import com.phellipesilva.coolposts.postlist.viewmodel.PostListViewModel
import com.phellipesilva.coolposts.postlist.viewmodel.PostListActivityState
import kotlinx.android.synthetic.main.activity_post_list.*

class PostListActivity : AppCompatActivity() {

    private lateinit var postListViewModel: PostListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_list)

        initViewModel(savedInstanceState)
        initRecyclerView()
        initViewStateObserver()
        initSwipeLayout()
    }

    private fun initViewModel(savedInstanceState: Bundle?) {
        val postListViewModelFactory = injector.getPostListViewModelFactory()
        postListViewModel = ViewModelProviders.of(this, postListViewModelFactory).get(PostListViewModel::class.java)

        if (savedInstanceState == null) {
            swipeRefreshLayout.isRefreshing = true
            postListViewModel.fetchPosts()
        }
    }

    private fun initRecyclerView() {
        val adapter = PostListAdapter(this)
        recyclerView.adapter = adapter

        postListViewModel.getPostsObservable().observe(this, Observer { postList ->
            postList?.let {
                adapter.updateData(postList)
            }
        })
    }

    private fun initViewStateObserver() {
        postListViewModel.viewState().observe(this, Observer {
            when (it.peekContent()) {
                PostListActivityState.IDLE -> swipeRefreshLayout.isRefreshing = false
                PostListActivityState.UNEXPECTED_ERROR -> Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initSwipeLayout() {
        swipeRefreshLayout.setOnRefreshListener {
            postListViewModel.fetchPosts()
        }
    }
}
