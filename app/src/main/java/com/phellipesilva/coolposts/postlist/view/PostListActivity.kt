package com.phellipesilva.coolposts.postlist.view

import android.app.ActivityOptions
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.phellipesilva.coolposts.R
import com.phellipesilva.coolposts.di.injector
import com.phellipesilva.coolposts.exceptions.NoConnectionException
import com.phellipesilva.coolposts.extensions.AndroidTransitionPair
import com.phellipesilva.coolposts.postdetails.view.PostDetailsActivity
import com.phellipesilva.coolposts.postlist.domain.Post
import com.phellipesilva.coolposts.postlist.viewmodel.PostListViewModel

class PostListActivity : AppCompatActivity() {

    private val postListRecyclerView: RecyclerView by lazy { findViewById(R.id.postListRecyclerView) }
    private val postListSwipeRefreshLayout: SwipeRefreshLayout by lazy { findViewById(R.id.postListSwipeRefreshLayout) }
    private val postListCoordinatorLayout: CoordinatorLayout by lazy { findViewById(R.id.postListCoordinatorLayout) }
    private val postListAppBarLayout: View by lazy { findViewById(R.id.postListAppBarLayout) }

    private val postListViewModel by lazy {
        ViewModelProvider(this, injector.getPostListViewModelFactory()).get(PostListViewModel::class.java)
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
        val adapter = PostsAdapter()
        adapter.setOnItemClickListener(::navigateToPostDetailsWithTransition)

        postListRecyclerView.adapter = adapter
    }

    private fun initViewStateObserver() {
        postListViewModel.viewState().observe(this, Observer { state ->
            state?.let(::processViewState)
        })
    }

    private fun processViewState(state: PostListViewState) {
        postListSwipeRefreshLayout.isRefreshing = state.isLoading
        renderPosts(state.posts)
        state.errorEvent?.getContentIfNotHandled()?.let(::renderError)
    }

    private fun renderPosts(posts: List<Post>) {
        val postListAdapter = postListRecyclerView.adapter as PostsAdapter
        postListAdapter.submitList(posts)
    }

    private fun renderError(throwable: Throwable?) {
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
        val transitionElementsMutableList = transitionElements.toMutableList()
        transitionElementsMutableList.add(AndroidTransitionPair(postListAppBarLayout, getString(R.string.post_list_appbarlayout_transition_id)))

        findViewById<View>(android.R.id.navigationBarBackground)?.let {
            transitionElementsMutableList.add(AndroidTransitionPair(it, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME))
        }

        findViewById<View>(android.R.id.statusBarBackground)?.let {
            transitionElementsMutableList.add(AndroidTransitionPair(it, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME))
        }

        val intent = PostDetailsActivity.newNavigationIntent(this, post = post)
        val options = ActivityOptions.makeSceneTransitionAnimation(this, *transitionElementsMutableList.toTypedArray())
        startActivity(intent, options.toBundle())
    }
}
