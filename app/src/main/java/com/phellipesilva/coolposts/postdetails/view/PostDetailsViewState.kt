package com.phellipesilva.coolposts.postdetails.view

import com.phellipesilva.coolposts.postdetails.data.Comment
import com.phellipesilva.coolposts.state.Event

data class PostDetailsViewState(
    val isLoading: Boolean,
    val comments: List<Comment>? = null,
    val errorEvent: Event<Throwable>? = null
)
