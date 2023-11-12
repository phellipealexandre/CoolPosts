package com.phellipesilva.coolposts.postdetails.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.phellipesilva.coolposts.postdetails.data.database.CommentDao
import com.phellipesilva.coolposts.postdetails.data.entity.CommentEntity
import com.phellipesilva.coolposts.postdetails.domain.Comment
import com.phellipesilva.coolposts.postdetails.data.service.CommentService
import io.reactivex.Completable
import javax.inject.Inject

class PostDetailsRepository @Inject constructor(
    private val commentService: CommentService,
    private val commentDao: CommentDao
) {

    fun getCommentsFromPost(postId: Int): LiveData<List<Comment>> {
        return commentDao.getCommentsFromPost(postId).map {
            mapCommentDatabaseEntitiesToDomainEntities(it)
        }
    }

    fun updateCommentsFromPost(postId: Int): Completable {
        return commentService.fetchCommentsFromPost(postId).flatMapCompletable(commentDao::saveComments)
    }

    private fun mapCommentDatabaseEntitiesToDomainEntities(databaseComments: List<CommentEntity>): List<Comment> {
        return databaseComments.map { databaseEntity ->
            Comment(
                id = databaseEntity.id,
                body = databaseEntity.body,
                userEmail = databaseEntity.email
            )
        }
    }
}