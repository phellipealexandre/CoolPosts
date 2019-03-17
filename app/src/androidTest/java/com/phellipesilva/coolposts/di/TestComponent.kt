package com.phellipesilva.coolposts.di

import com.phellipesilva.coolposts.postdetails.view.PostDetailsActivityTest
import dagger.Component

@Component(modules = [ServiceTestModule::class, DatabaseTestModule::class])
interface TestComponent : ApplicationComponent {

    @Component.Builder
    interface Builder : ApplicationComponent.Builder

    fun inject(test: PostDetailsActivityTest)
}