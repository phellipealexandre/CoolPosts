package com.phellipesilva.coolposts.postlist.data

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class User(
    val userId: Int,
    val name: String
) : Parcelable