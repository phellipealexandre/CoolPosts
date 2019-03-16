package com.phellipesilva.coolposts.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.phellipesilva.coolposts.postdetails.database.CommentDao
import com.phellipesilva.coolposts.postlist.data.Post
import com.phellipesilva.coolposts.postlist.database.PostDao
import com.phellipesilva.coolposts.postdetails.entity.CommentEntity

@Database(entities = [CommentEntity::class, Post::class], version = 1, exportSchema = false)
abstract class MainDatabase : RoomDatabase() {
    abstract fun getPostDao(): PostDao
    abstract fun getCommentDao(): CommentDao
}