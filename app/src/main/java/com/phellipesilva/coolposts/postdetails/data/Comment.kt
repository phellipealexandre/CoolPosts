package com.phellipesilva.coolposts.postdetails.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Comment(
    val postId: Int,
    @PrimaryKey val id: Int,
    val name: String,
    val email: String,
    val body: String
)