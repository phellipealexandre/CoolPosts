package com.phellipesilva.coolposts.postlist.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.phellipesilva.coolposts.postlist.domain.Post
import com.phellipesilva.coolposts.postlist.entities.CommentEntity

@Database(entities = [CommentEntity::class, Post::class], version = 1, exportSchema = false)
abstract class MainDatabase : RoomDatabase() {
    abstract fun getPostDAO(): PostDAO
    abstract fun getCommentDAO(): CommentDAO
}