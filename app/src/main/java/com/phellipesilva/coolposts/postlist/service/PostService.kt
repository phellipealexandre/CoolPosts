package com.phellipesilva.coolposts.postlist.service

import com.phellipesilva.coolposts.postlist.entities.CommentEntity
import com.phellipesilva.coolposts.postlist.entities.PostEntity
import com.phellipesilva.coolposts.postlist.entities.UserEntity
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface PostService {

    @GET("posts")
    fun getPosts(): Single<List<PostEntity>>

    @GET("users")
    fun getUsers(): Single<List<UserEntity>>

    @GET("comments")
    fun getComments(@Query("postId") postId: Int): Single<List<CommentEntity>>
}