package com.phellipesilva.coolposts.postlist.service.entities

data class CommentRemoteEntity(
    val postId: Int,
    val id: Int,
    val name: String,
    val email: String,
    val body: String
)