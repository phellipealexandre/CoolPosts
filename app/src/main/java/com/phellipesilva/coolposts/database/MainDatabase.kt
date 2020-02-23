package com.phellipesilva.coolposts.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.phellipesilva.coolposts.postdetails.data.database.CommentDao
import com.phellipesilva.coolposts.postlist.database.entity.PostEntity
import com.phellipesilva.coolposts.postlist.database.PostDao
import com.phellipesilva.coolposts.postdetails.data.entity.CommentEntity

@Database(entities = [CommentEntity::class, PostEntity::class], version = 1, exportSchema = false)
abstract class MainDatabase : RoomDatabase() {
    abstract fun getPostDao(): PostDao
    abstract fun getCommentDao(): CommentDao
}