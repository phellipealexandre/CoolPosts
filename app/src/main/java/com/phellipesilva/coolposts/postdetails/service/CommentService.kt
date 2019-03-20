package com.phellipesilva.coolposts.postdetails.service

import com.phellipesilva.coolposts.postdetails.data.Comment
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface CommentService {

    @GET("comments")
    fun fetchCommentsFromPost(@Query("postId") postId: Int): Single<List<Comment>>
}