package com.phellipesilva.coolposts.di

import com.phellipesilva.coolposts.postdetails.data.service.CommentService
import com.phellipesilva.coolposts.postlist.service.PostService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
object NetworkModule {

    @Provides
    @Singleton
    fun providesRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com")
        .client(OkHttpClient.Builder().build())
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    @Provides
    @Singleton
    fun providesPostService(retrofit: Retrofit): PostService = retrofit.create(PostService::class.java)

    @Provides
    @Singleton
    fun providesCommentService(retrofit: Retrofit): CommentService = retrofit.create(CommentService::class.java)
}