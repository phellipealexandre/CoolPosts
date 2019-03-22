package com.phellipesilva.coolposts.postdetails.di

import com.phellipesilva.coolposts.postdetails.viewmodel.PostDetailsViewModelFactory
import dagger.Subcomponent

@Subcomponent(modules = [PostDetailsModule::class])
interface PostDetailsComponent {
    fun getPostDetailsViewModelFactory(): PostDetailsViewModelFactory
}