package com.phellipesilva.coolposts.postlist.data

import androidx.room.Entity

@Entity
data class User(
    val userId: Int,
    val name: String,
    val website: String
)