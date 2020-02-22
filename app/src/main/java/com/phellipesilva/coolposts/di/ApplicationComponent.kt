package com.phellipesilva.coolposts.di

import android.content.Context
import com.phellipesilva.coolposts.postdetails.di.PostDetailsComponent
import com.phellipesilva.coolposts.postdetails.di.PostDetailsModule
import com.phellipesilva.coolposts.postlist.viewmodel.PostListViewModelFactory
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class, NetworkModule::class])
interface ApplicationComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): ApplicationComponent
    }

    fun getPostListViewModelFactory(): PostListViewModelFactory

    fun with(postDetailsModule: PostDetailsModule): PostDetailsComponent
}