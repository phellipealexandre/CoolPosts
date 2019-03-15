package com.phellipesilva.coolposts.postlist.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val website: String
)