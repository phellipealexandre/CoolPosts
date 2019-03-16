package com.phellipesilva.coolposts.postdetails.repository

import androidx.lifecycle.LiveData
import com.phellipesilva.coolposts.postdetails.database.CommentDAO
import com.phellipesilva.coolposts.postdetails.entity.CommentEntity
import com.phellipesilva.coolposts.postdetails.service.CommentService
import dagger.Reusable
import io.reactivex.Completable
import javax.inject.Inject

@Reusable
class PostDetailsRepository @Inject constructor(
    private val commentService: CommentService,
    private val commentDAO: CommentDAO
) {

    fun fetchComments(postId: Int): Completable {
        return commentService.getComments(postId)
            .flatMapCompletable(commentDAO::saveComments)
    }

    fun getComments(postId: Int): LiveData<List<CommentEntity>> {
        return commentDAO.getAllCommentsFromPost(postId)
    }
}