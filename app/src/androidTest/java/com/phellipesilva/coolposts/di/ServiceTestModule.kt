package com.phellipesilva.coolposts.di

import com.phellipesilva.coolposts.postdetails.service.CommentService
import com.phellipesilva.coolposts.postlist.service.PostService
import dagger.Module
import dagger.Provides
import io.appflate.restmock.RESTMockServer
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
object ServiceTestModule {

    @Provides
    @JvmStatic
    fun providesOkHttpCientService(): OkHttpClient = OkHttpClient.Builder()
        .sslSocketFactory(RESTMockServer.getSSLSocketFactory(), RESTMockServer.getTrustManager())
        .build()

    @Provides
    @JvmStatic
    fun providesPostService(okHttpClient: OkHttpClient): PostService = Retrofit.Builder()
        .baseUrl(RESTMockServer.getUrl())
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(PostService::class.java)

    @Provides
    @JvmStatic
    fun providesCommentService(okHttpClient: OkHttpClient): CommentService = Retrofit.Builder()
        .baseUrl(RESTMockServer.getUrl())
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(CommentService::class.java)
}