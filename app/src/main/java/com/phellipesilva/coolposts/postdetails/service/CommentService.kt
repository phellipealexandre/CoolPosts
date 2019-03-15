package com.phellipesilva.coolposts.postdetails.service

import com.phellipesilva.coolposts.postdetails.entity.CommentEntity
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface CommentService {

    @GET("comments")
    fun getComments(@Query("postId") postId: Int): Single<List<CommentEntity>>
}