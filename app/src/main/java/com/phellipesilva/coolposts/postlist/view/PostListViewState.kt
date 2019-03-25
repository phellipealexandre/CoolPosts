package com.phellipesilva.coolposts.postlist.view

import com.phellipesilva.coolposts.postlist.data.Post
import com.phellipesilva.coolposts.state.Event

data class PostListViewState(
    val isLoading: Boolean = false,
    val posts: List<Post>? = emptyList(),
    val errorEvent: Event<Throwable>? = null
)