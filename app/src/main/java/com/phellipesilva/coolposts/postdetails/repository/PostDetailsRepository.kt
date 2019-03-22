package com.phellipesilva.coolposts.postdetails.repository

import androidx.lifecycle.LiveData
import com.phellipesilva.coolposts.postdetails.data.Comment
import com.phellipesilva.coolposts.postdetails.database.CommentDao
import com.phellipesilva.coolposts.postdetails.service.CommentService
import dagger.Reusable
import io.reactivex.Completable
import javax.inject.Inject

@Reusable
class PostDetailsRepository @Inject constructor(
    private val commentService: CommentService,
    private val commentDao: CommentDao
) {

    fun getCommentsFromPost(postId: Int): LiveData<List<Comment>> {
        return commentDao.getCommentsFromPost(postId)
    }

    fun updateCommentsFromPost(postId: Int): Completable {
        return commentService.fetchCommentsFromPost(postId)
            .flatMapCompletable(commentDao::saveComments)
    }
}