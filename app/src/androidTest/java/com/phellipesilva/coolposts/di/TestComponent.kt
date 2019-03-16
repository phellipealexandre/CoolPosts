package com.phellipesilva.coolposts.di

import dagger.Component

@Component(modules = [ServiceTestModule::class, DatabaseTestModule::class])
interface TestComponent : ApplicationComponent {

    @Component.Builder
    interface Builder : ApplicationComponent.Builder
}