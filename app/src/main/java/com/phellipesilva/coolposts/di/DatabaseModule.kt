package com.phellipesilva.coolposts.di

import android.content.Context
import androidx.room.Room
import com.phellipesilva.coolposts.postdetails.database.CommentDao
import com.phellipesilva.coolposts.database.MainDatabase
import com.phellipesilva.coolposts.postlist.database.PostDao
import dagger.Module
import dagger.Provides
import dagger.Reusable
import javax.inject.Singleton

@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun providesDatabase(context: Context): MainDatabase =
        Room.databaseBuilder(context, MainDatabase::class.java, "MainDatabase").build()

    @Provides
    @Reusable
    fun providesPostDao(mainDatabase: MainDatabase): PostDao = mainDatabase.getPostDao()

    @Provides
    @Reusable
    fun providesCommentDao(mainDatabase: MainDatabase): CommentDao = mainDatabase.getCommentDao()
}