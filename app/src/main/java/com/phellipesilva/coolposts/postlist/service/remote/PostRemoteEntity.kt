package com.phellipesilva.coolposts.postlist.service.remote

data class PostRemoteEntity(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)