package com.phellipesilva.coolposts.postlist.service

import com.phellipesilva.coolposts.postlist.entities.CommentEntity
import com.phellipesilva.coolposts.postlist.entities.PostEntity
import com.phellipesilva.coolposts.postlist.entities.UserEntity
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface PostService {

    @GET("posts")
    fun getPosts(): Observable<List<PostEntity>>

    @GET("users")
    fun getUsers(): Observable<List<UserEntity>>

    @GET("comments")
    fun getComments(@Query("postId") postId: Int): Observable<List<CommentEntity>>
}