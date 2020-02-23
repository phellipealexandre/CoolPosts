package com.phellipesilva.coolposts.di

import com.phellipesilva.coolposts.postdetails.data.service.CommentService
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
    fun providesOkHttpClientService(): OkHttpClient = OkHttpClient.Builder()
        .sslSocketFactory(RESTMockServer.getSSLSocketFactory(), RESTMockServer.getTrustManager())
        .build()

    @Provides
    fun providesRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(RESTMockServer.getUrl())
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    @Provides
    fun providesPostService(retrofit: Retrofit): PostService = retrofit.create(PostService::class.java)

    @Provides
    fun providesCommentService(retrofit: Retrofit): CommentService = retrofit.create(CommentService::class.java)
}