package com.phellipesilva.coolposts.di

import android.content.Context
import com.phellipesilva.coolposts.navigation.PostNavigator
import com.phellipesilva.coolposts.postdetails.di.PostDetailsComponent
import com.phellipesilva.coolposts.postdetails.di.PostDetailsModule
import com.phellipesilva.coolposts.postlist.viewmodel.PostListViewModelFactory
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class, ServiceModule::class])
interface ApplicationComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun applicationContext(applicationContext: Context): Builder
        fun build(): ApplicationComponent
    }

    fun getPostListViewModelFactory(): PostListViewModelFactory
    fun getPostNavigator(): PostNavigator

    fun with(postDetailsModule: PostDetailsModule): PostDetailsComponent
}