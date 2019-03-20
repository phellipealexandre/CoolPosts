package com.phellipesilva.coolposts.postlist.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Post(
    @PrimaryKey val id: Int,
    val title: String,
    val body: String,
    val userId: Int,
    val userName: String
) : Parcelable