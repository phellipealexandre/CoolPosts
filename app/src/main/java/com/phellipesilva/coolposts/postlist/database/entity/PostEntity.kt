package com.phellipesilva.coolposts.postlist.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PostEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val body: String,
    val userId: Int,
    val userName: String
)