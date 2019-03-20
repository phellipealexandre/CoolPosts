package com.phellipesilva.coolposts.di

import com.phellipesilva.coolposts.postdetails.service.CommentService
import com.phellipesilva.coolposts.postlist.service.PostService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
object ServiceModule {

    private const val baseUrl = "https://jsonplaceholder.typicode.com"

    @Provides
    @Singleton
    @JvmStatic
    fun providesOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

    @Provides
    @Singleton
    @JvmStatic
    fun providesPostService(okHttpClient: OkHttpClient): PostService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(PostService::class.java)

    @Provides
    @Singleton
    @JvmStatic
    fun providesCommentService(okHttpClient: OkHttpClient): CommentService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(CommentService::class.java)
}