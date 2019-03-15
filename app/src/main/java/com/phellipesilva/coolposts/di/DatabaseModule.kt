package com.phellipesilva.coolposts.di

import android.content.Context
import androidx.room.Room
import com.phellipesilva.coolposts.postlist.database.CommentDAO
import com.phellipesilva.coolposts.postlist.database.MainDatabase
import com.phellipesilva.coolposts.postlist.database.PostDAO
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DatabaseModule {

    @Provides
    @Singleton
    @JvmStatic
    fun providesDatabase(context: Context): MainDatabase =
        Room.databaseBuilder(context, MainDatabase::class.java, "MainDatabase").build()

    @Provides
    @Singleton
    @JvmStatic
    fun providesPostDAO(mainDatabase: MainDatabase): PostDAO = mainDatabase.getPostDAO()

    @Provides
    @Singleton
    @JvmStatic
    fun providesCommentDAO(mainDatabase: MainDatabase): CommentDAO = mainDatabase.getCommentDAO()
}