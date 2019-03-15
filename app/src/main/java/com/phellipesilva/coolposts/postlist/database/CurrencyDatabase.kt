package com.phellipesilva.coolposts.postlist.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.phellipesilva.coolposts.postlist.entities.CommentEntity
import com.phellipesilva.coolposts.postlist.entities.PostEntity
import com.phellipesilva.coolposts.postlist.entities.UserEntity

@Database(entities = [CommentEntity::class, PostEntity::class, UserEntity::class], version = 1, exportSchema = false)
abstract class MainDatabase : RoomDatabase() {
    abstract fun getPostDAO(): PostDAO
    abstract fun getUserDAO(): UserDAO
    abstract fun getCommentDAO(): CommentDAO
}