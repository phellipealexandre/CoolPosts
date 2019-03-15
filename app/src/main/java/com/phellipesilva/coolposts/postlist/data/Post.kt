package com.phellipesilva.coolposts.postlist.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Post(
    @PrimaryKey val id: Int,
    val title: String,
    val body: String,
    @Embedded val user: User
)