package com.phellipesilva.coolposts.postdetails.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.phellipesilva.coolposts.postdetails.database.CommentDao
import com.phellipesilva.coolposts.postdetails.data.Comment
import com.phellipesilva.coolposts.postdetails.service.CommentService
import com.phellipesilva.coolposts.utils.RxUtils
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PostDetailsRepositoryTest {


    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var commentService: CommentService

    @Mock
    private lateinit var commentDao: CommentDao

    private lateinit var postDetailsRepository: PostDetailsRepository

    @Before
    fun setUp() {
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
                postId = 1,
                id = 1,
                name = "Name",
                email = "Email",
                body = "body"
            ),
            Comment(
                postId = 2,
                id = 2,
                name = "Name2",
                email = "Email2",
                body = "body2"
            )
        )
        val commentsSingle = Single.just(comments)
        whenever(commentService.getComments(1)).thenReturn(commentsSingle)

        postDetailsRepository.fetchComments(1)

        verify(commentService).getComments(1)
    }

    @Test
    fun shouldSaveCommentsOnDatabaseWhenFetchingIsSuccessful() {
        val comments = listOf(
            Comment(
                postId = 1,
                id = 1,
                name = "Name",
                email = "Email",
                body = "body"
            ),
            Comment(
                postId = 2,
                id = 2,
                name = "Name2",
                email = "Email2",
                body = "body2"
            )
        )
        val commentsSingle = Single.just(comments)
        whenever(commentService.getComments(1)).thenReturn(commentsSingle)

        val testObserver = TestObserver<Unit>()
        postDetailsRepository.fetchComments(1).subscribe(testObserver)

        verify(commentDao).saveComments(comments)
    }

    @Test
    fun shouldReturnCommentsLiveDataFromDatabaseForGivenPostId() {
        val expectedLiveData = MutableLiveData<List<Comment>>()
        val comments = listOf(
            Comment(
                postId = 1,
                id = 1,
                name = "Name",
                email = "Email",
                body = "body"
            ),
            Comment(
                postId = 2,
                id = 2,
                name = "Name2",
                email = "Email2",
                body = "body2"
            )
        )
        expectedLiveData.value = comments
        whenever(commentDao.getAllCommentsFromPost(1)).thenReturn(expectedLiveData)

        val commentsLiveData = postDetailsRepository.getComments(1)

        assertEquals(expectedLiveData, commentsLiveData)
    }
}