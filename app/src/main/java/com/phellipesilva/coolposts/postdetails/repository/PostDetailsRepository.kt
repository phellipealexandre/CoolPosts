package com.phellipesilva.coolposts.postdetails.repository

import androidx.lifecycle.LiveData
import com.phellipesilva.coolposts.postdetails.data.Comment
import com.phellipesilva.coolposts.postdetails.database.CommentDao
import com.phellipesilva.coolposts.postdetails.service.CommentService
import dagger.Reusable
import io.reactivex.Single
import javax.inject.Inject

@Reusable
class PostDetailsRepository @Inject constructor(
    private val commentService: CommentService,
    private val commentDao: CommentDao
) {

    fun updateCommentsFromPost(postId: Int): Single<List<Comment>> {
        return commentService.fetchCommentsFromPost(postId)
            .map {
                commentDao.saveComments(it)
                it
            }
    }

    fun getCommentsFromPost(postId: Int): LiveData<List<Comment>> {
        return commentDao.getCommentsFromPost(postId)
    }
}