package com.phellipesilva.coolposts.postdetails.repository

import androidx.lifecycle.LiveData
import com.phellipesilva.coolposts.postdetails.database.CommentDao
import com.phellipesilva.coolposts.postdetails.data.Comment
import com.phellipesilva.coolposts.postdetails.service.CommentService
import dagger.Reusable
import io.reactivex.Completable
import javax.inject.Inject

@Reusable
class PostDetailsRepository @Inject constructor(
    private val commentService: CommentService,
    private val commentDao: CommentDao
) {

    fun fetchComments(postId: Int): Completable {
        return commentService.getComments(postId)
            .flatMapCompletable(commentDao::saveComments)
    }

    fun getComments(postId: Int): LiveData<List<Comment>> {
        return commentDao.getAllCommentsFromPost(postId)
    }
}