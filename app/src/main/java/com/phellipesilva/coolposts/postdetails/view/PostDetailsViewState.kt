package com.phellipesilva.coolposts.postdetails.view

import com.phellipesilva.coolposts.postdetails.data.Comment
import com.phellipesilva.coolposts.state.Event
import com.phellipesilva.coolposts.state.ViewState

data class PostDetailsViewState(
    val viewState: ViewState,
    val comments: List<Comment>? = null,
    val throwable: Event<Throwable>? = null
) {
    companion object {

        fun buildLoadingState(): PostDetailsViewState {
            return PostDetailsViewState(ViewState.LOADING)
        }

        fun buildSuccessState(comments: List<Comment>): PostDetailsViewState {
            return PostDetailsViewState(ViewState.SUCCESS, comments = comments)
        }

        fun buildErrorState(throwable: Throwable): PostDetailsViewState {
            return PostDetailsViewState(ViewState.ERROR, throwable = Event(throwable))
        }
    }
}
