package com.phellipesilva.coolposts.postlist.service

import com.phellipesilva.coolposts.postlist.service.entity.PostRemoteEntity
import com.phellipesilva.coolposts.postlist.service.entity.UserRemoteEntity
import io.reactivex.Single
import retrofit2.http.GET

interface PostService {

    @GET("posts")
    fun fetchPosts(): Single<List<PostRemoteEntity>>

    @GET("users")
    fun fetchUsers(): Single<List<UserRemoteEntity>>
}