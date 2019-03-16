package com.phellipesilva.coolposts.postlist.view

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.phellipesilva.coolposts.R
import com.phellipesilva.coolposts.di.injector
import com.phellipesilva.coolposts.extensions.MarginItemDecoration
import com.phellipesilva.coolposts.state.ViewState
import com.phellipesilva.coolposts.postlist.viewmodel.PostListViewModel
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
        recyclerView.layoutAnimation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_enter_up)
        recyclerView.addItemDecoration(
            MarginItemDecoration(resources.getDimension(R.dimen.recyclerview_margin).toInt())
        )

        postListViewModel.getPostsObservable().observe(this, Observer { postList ->
            val isFirstUse = postList.isNullOrEmpty() && savedInstanceState == null

            if (isFirstUse) {
                swipeRefreshLayout.isRefreshing = true
                postListViewModel.fetchPosts()
            } else {
                adapter.updateData(postList)
                recyclerView.scheduleLayoutAnimation()
            }
        })
    }

    private fun initViewStateObserver() {
        postListViewModel.viewState().observe(this, Observer {
            val event = it.peekContent()
            when (event) {
                ViewState.SUCCESS -> swipeRefreshLayout.isRefreshing = false
                else -> {
                    Snackbar.make(postListCoordinatorLayout, event.msgStringId, Snackbar.LENGTH_LONG).show()
                    swipeRefreshLayout.isRefreshing = event.isLoading
                }
            }
        })
    }

    private fun initSwipeLayout() {
        swipeRefreshLayout.setOnRefreshListener {
            postListViewModel.fetchPosts()
        }
    }
}
