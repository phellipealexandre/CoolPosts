package com.phellipesilva.coolposts.postlist.entity

data class PostRemoteEntity(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)