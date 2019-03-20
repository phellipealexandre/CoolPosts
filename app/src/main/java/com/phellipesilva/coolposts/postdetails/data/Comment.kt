package com.phellipesilva.coolposts.postdetails.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Comment(
    @PrimaryKey val id: Int,
    val postId: Int,
    val name: String,
    val email: String,
    val body: String
)