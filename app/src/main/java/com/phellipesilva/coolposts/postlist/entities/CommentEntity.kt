package com.phellipesilva.coolposts.postlist.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CommentEntity(
    val postId: Int,
    @PrimaryKey val id: Int,
    val name: String,
    val email: String,
    val body: String
)