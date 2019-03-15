package com.phellipesilva.coolposts.postlist.domain

import androidx.room.Entity

@Entity
data class User(
    val userId: Int,
    val name: String,
    val website: String
)