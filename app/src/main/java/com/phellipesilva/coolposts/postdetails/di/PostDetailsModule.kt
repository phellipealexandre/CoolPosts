package com.phellipesilva.coolposts.postdetails.di

import dagger.Module
import dagger.Provides

@Module
class PostDetailsModule(@get:Provides val postId: Int)