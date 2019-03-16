package com.phellipesilva.coolposts.di

import android.content.Context
import androidx.room.Room
import com.phellipesilva.coolposts.postdetails.database.CommentDAO
import com.phellipesilva.coolposts.database.MainDatabase
import com.phellipesilva.coolposts.postlist.database.PostDAO
import dagger.Module
import dagger.Provides

@Module
object DatabaseTestModule {

    @Provides
    @JvmStatic
    fun providesDatabase(context: Context): MainDatabase = Room
        .inMemoryDatabaseBuilder(context, MainDatabase::class.java)
        .build()

    @Provides
    @JvmStatic
    fun providesPostDAO(mainDatabase: MainDatabase): PostDAO = mainDatabase.getPostDAO()

    @Provides
    @JvmStatic
    fun providesCommentDAO(mainDatabase: MainDatabase): CommentDAO = mainDatabase.getCommentDAO()
}