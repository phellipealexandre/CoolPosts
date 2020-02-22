package com.phellipesilva.coolposts.postdetails.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.phellipesilva.coolposts.postdetails.data.Comment
import com.phellipesilva.coolposts.postdetails.database.CommentDao
import com.phellipesilva.coolposts.postdetails.service.CommentService
import com.phellipesilva.coolposts.utils.RxUtils
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import junit.framework.Assert.assertEquals
import org.junit.After
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
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        postDetailsRepository = PostDetailsRepository(commentService, commentDao)
        RxUtils.overridesEnvironmentToCustomScheduler(Schedulers.trampoline())
    }

    @After
    fun tearDown() {
        RxUtils.resetSchedulers()
    }

    @Test
    fun shouldFetchCommentsWithGivenPostId() {
        val comments = listOf(
            Comment(
                id = 1,
                postId = 1,
                name = "Name",
                email = "Email",
                body = "body"
            ),
            Comment(
                id = 2,
                postId = 2,
                name = "Name2",
                email = "Email2",
                body = "body2"
            )
        )
        val commentsSingle = Single.just(comments)
        every { commentService.fetchCommentsFromPost(1) } returns commentsSingle
        every { commentDao.saveComments(any()) } returns Completable.complete()

        val testObserver = TestObserver<Unit>()
        postDetailsRepository.updateCommentsFromPost(1).subscribe(testObserver)

        verify { commentService.fetchCommentsFromPost(1) }
        testObserver.assertComplete()
    }

    @Test
    fun shouldSaveCommentsOnDatabaseWhenFetchingIsSuccessful() {
        val comments = listOf(
            Comment(
                id = 1,
                postId = 1,
                name = "Name",
                email = "Email",
                body = "body"
            ),
            Comment(
                id = 2,
                postId = 2,
                name = "Name2",
                email = "Email2",
                body = "body2"
            )
        )
        val commentsSingle = Single.just(comments)
        every { commentService.fetchCommentsFromPost(1) } returns commentsSingle
        every { commentDao.saveComments(any()) } returns Completable.complete()

        val testObserver = TestObserver<Unit>()
        postDetailsRepository.updateCommentsFromPost(1).subscribe(testObserver)

        verify { commentDao.saveComments(comments) }
        testObserver.assertComplete()
    }

    @Test
    fun shouldReturnCommentsLiveDataFromDatabaseForGivenPostId() {
        val expectedLiveData = MutableLiveData<List<Comment>>()
        val comments = listOf(
            Comment(
                id = 1,
                postId = 1,
                name = "Name",
                email = "Email",
                body = "body"
            ),
            Comment(
                id = 2,
                postId = 2,
                name = "Name2",
                email = "Email2",
                body = "body2"
            )
        )
        expectedLiveData.value = comments
        every { commentDao.getCommentsFromPost(1) } returns expectedLiveData

        val commentsLiveData = postDetailsRepository.getCommentsFromPost(1)

        assertEquals(expectedLiveData, commentsLiveData)
    }
}