package com.phellipesilva.coolposts.postlist.view

import com.phellipesilva.coolposts.postlist.data.Post
import com.phellipesilva.coolposts.state.Event

data class PostListViewState(
    val isLoading: Boolean,
    val posts: List<Post>? = null,
    val errorEvent: Event<Throwable>? = null
)