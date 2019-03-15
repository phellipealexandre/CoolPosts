package com.phellipesilva.coolposts.postlist.service.entities

data class PostRemoteEntity(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)