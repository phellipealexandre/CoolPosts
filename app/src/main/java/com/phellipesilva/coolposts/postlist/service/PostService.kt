package com.phellipesilva.coolposts.postlist.service

import com.phellipesilva.coolposts.postlist.entity.PostRemoteEntity
import com.phellipesilva.coolposts.postlist.entity.UserRemoteEntity
import io.reactivex.Single
import retrofit2.http.GET

interface PostService {

    @GET("posts")
    fun getPosts(): Single<List<PostRemoteEntity>>

    @GET("users")
    fun getUsers(): Single<List<UserRemoteEntity>>
}