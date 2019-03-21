package com.phellipesilva.coolposts.postlist.view

import com.phellipesilva.coolposts.postlist.data.Post
import com.phellipesilva.coolposts.state.Event
import com.phellipesilva.coolposts.state.ViewState

data class PostListViewState(
    val viewState: ViewState,
    val posts: List<Post>? = null,
    val throwable: Event<Throwable>? = null
) {
    companion object {

        fun buildLoadingState(): PostListViewState {
            return PostListViewState(ViewState.LOADING)
        }

        fun buildSuccessState(posts: List<Post>): PostListViewState {
            return PostListViewState(ViewState.SUCCESS, posts = posts)
        }

        fun buildErrorState(throwable: Throwable): PostListViewState {
            return PostListViewState(ViewState.ERROR, throwable = Event(throwable))
        }
    }
}