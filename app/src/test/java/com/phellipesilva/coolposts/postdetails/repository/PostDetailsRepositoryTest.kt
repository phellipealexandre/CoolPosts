package com.phellipesilva.coolposts.postdetails.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.phellipesilva.coolposts.postdetails.data.entity.CommentEntity
import com.phellipesilva.coolposts.postdetails.data.database.CommentDao
import com.phellipesilva.coolposts.postdetails.data.service.CommentService
import com.phellipesilva.coolposts.postdetails.domain.Comment
import com.phellipesilva.coolposts.utils.RxUtils
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PostDetailsRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var commentService: CommentService

    @MockK
    private lateinit var commentDao: CommentDao

    private lateinit var postDetailsRepository: PostDetailsRepository

    @Before
    fun `Set Up`() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        postDetailsRepository = PostDetailsRepository(commentService, commentDao)
        RxUtils.overridesRXSchedulers(Schedulers.trampoline())
    }

    @After
    fun `Tear down`() {
        RxUtils.resetSchedulers()
    }

    @Test
    fun `Should fetch comments with given post id`() {
        val comments = listOf(
            CommentEntity(
                id = 1,
                postId = 1,
                name = "Name",
                email = "Email",
                body = "body"
            ),
            CommentEntity(
                id = 2,
                postId = 2,
                name = "Name2",
                email = "Email2",
                body = "body2"
            )
        )
        every { commentService.fetchCommentsFromPost(1) } returns Single.just(comments)
        every { commentDao.saveComments(any()) } returns Completable.complete()

        postDetailsRepository.updateCommentsFromPost(1).test()

        verify { commentService.fetchCommentsFromPost(1) }
    }

    @Test
    fun `Should save fetched comments on database when request from service is successful`() {
        val comments = listOf(
            CommentEntity(
                id = 1,
                postId = 1,
                name = "Name",
                email = "Email",
                body = "body"
            ),
            CommentEntity(
                id = 2,
                postId = 2,
                name = "Name2",
                email = "Email2",
                body = "body2"
            )
        )
        every { commentService.fetchCommentsFromPost(1) } returns Single.just(comments)
        every { commentDao.saveComments(any()) } returns Completable.complete()

        postDetailsRepository.updateCommentsFromPost(1).test()
            .assertComplete()

        verify { commentDao.saveComments(comments) }
    }

    @Test
    fun `should return mapped comments from database when requesting from post id`() {
        val comments = listOf(
            CommentEntity(
                id = 1,
                postId = 1,
                name = "Name",
                email = "Email",
                body = "body"
            ),
            CommentEntity(
                id = 2,
                postId = 2,
                name = "Name2",
                email = "Email2",
                body = "body2"
            )
        )
        every { commentDao.getCommentsFromPost(1) } returns MutableLiveData<List<CommentEntity>>(comments)

        val commentsLiveData = postDetailsRepository.getCommentsFromPost(1)

        commentsLiveData.observeForever {
            assertEquals(
                listOf(
                    Comment(
                        id = 1,
                        userEmail = "Email",
                        body = "body"
                    ),
                    Comment(
                        id = 2,
                        userEmail = "Email2",
                        body = "body2"
                    )
                ), it
            )
        }
    }
}