package com.phellipesilva.coolposts.di

import com.phellipesilva.coolposts.postlist.service.PostService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
object ServiceModule {

    @Provides
    @Singleton
    @JvmStatic
    fun providesPostService(): PostService = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com")
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(PostService::class.java)
}