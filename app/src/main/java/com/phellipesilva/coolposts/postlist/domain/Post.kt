package com.phellipesilva.coolposts.postlist.domain

data class Post(
    val id: Int,
    val title: String,
    val body: String,
    val user: User
)