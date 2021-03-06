package com.phellipesilva.coolposts.di

import android.content.Context
import androidx.room.Room
import com.phellipesilva.coolposts.postdetails.data.database.CommentDao
import com.phellipesilva.coolposts.database.MainDatabase
import com.phellipesilva.coolposts.postlist.database.PostDao
import dagger.Module
import dagger.Provides

@Module
object DatabaseTestModule {

    @Provides
    fun providesDatabase(context: Context): MainDatabase = Room
        .inMemoryDatabaseBuilder(context, MainDatabase::class.java)
        .build()

    @Provides
    fun providesPostDao(mainDatabase: MainDatabase): PostDao = mainDatabase.getPostDao()

    @Provides
    fun providesCommentDao(mainDatabase: MainDatabase): CommentDao = mainDatabase.getCommentDao()
}