package com.phellipesilva.coolposts.postdetails.view

import com.phellipesilva.coolposts.postdetails.domain.Comment
import com.phellipesilva.coolposts.state.Event

data class PostDetailsViewState(
    val isLoading: Boolean = false,
    val comments: List<Comment> = emptyList(),
    val errorEvent: Event<Throwable>? = null
)
