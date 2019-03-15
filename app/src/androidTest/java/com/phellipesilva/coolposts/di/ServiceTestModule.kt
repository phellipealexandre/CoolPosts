package com.phellipesilva.coolposts.di

import com.phellipesilva.coolposts.postlist.service.PostService
import dagger.Module
import dagger.Provides
import io.appflate.restmock.RESTMockServer
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
object ServiceTestModule {

    @Singleton
    @Provides
    @JvmStatic
    fun providesPostService(): PostService = Retrofit.Builder()
        .baseUrl(RESTMockServer.getUrl())
        .client(
            OkHttpClient.Builder()
                .sslSocketFactory(RESTMockServer.getSSLSocketFactory(), RESTMockServer.getTrustManager())
                .build()
        )
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(PostService::class.java)
}