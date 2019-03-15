package com.phellipesilva.coolposts.postlist.service

import com.phellipesilva.coolposts.postlist.service.entities.CommentRemoteEntity
import com.phellipesilva.coolposts.postlist.service.entities.PostRemoteEntity
import com.phellipesilva.coolposts.postlist.service.entities.UserRemoteEntity
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface PostService {

    @GET("posts")
    fun getPosts(): Observable<List<PostRemoteEntity>>

    @GET("users")
    fun getUsers(): Observable<List<UserRemoteEntity>>

    @GET("comments")
    fun getComments(@Query("postId") postId: Int): Observable<List<CommentRemoteEntity>>
}