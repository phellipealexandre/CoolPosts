package com.phellipesilva.coolposts.di

import com.phellipesilva.coolposts.postdetails.view.PostDetailsActivityUITest
import dagger.Component

@Component(modules = [ServiceTestModule::class, DatabaseTestModule::class])
interface TestComponent : ApplicationComponent {

    @Component.Factory
    interface Factory : ApplicationComponent.Factory

    fun inject(test: PostDetailsActivityUITest)
}