package com.phellipesilva.coolposts.postdetails.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.phellipesilva.coolposts.exceptions.NoConnectionException
import com.phellipesilva.coolposts.postdetails.domain.Comment
import com.phellipesilva.coolposts.postdetails.repository.PostDetailsRepository
import com.phellipesilva.coolposts.postdetails.view.PostDetailsViewState
import com.phellipesilva.coolposts.state.ConnectionChecker
import com.phellipesilva.coolposts.utils.RxUtils
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PostDetailsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var postDetailsRepository: PostDetailsRepository

    @MockK
    private lateinit var connectionChecker: ConnectionChecker

    @MockK
    private lateinit var compositeDisposable: CompositeDisposable

    private lateinit var postDetailsViewModel: PostDetailsViewModel

    @Before
    fun `Set Up`() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
        RxUtils.overridesRXSchedulers(Schedulers.trampoline())
    }

    @After
    fun `Tear Down`() {
        RxUtils.resetSchedulers()
    }

    @Test
    fun `Should get comments LiveData from repository with initial state`() {
        val comments = listOf(
            Comment(
                id = 1,
                userEmail = "Email",
                body = "Body"
            )
        )

        initViewModel(comments = comments, isOnline = true, postId = 1)

        postDetailsViewModel.viewState().observeForever {
            assertEquals(
                PostDetailsViewState(isLoading = false, comments = comments),
                it
            )
        }
    }

    @Test
    fun `Should emit no internet state when there is no internet`() {
        initViewModel(comments = listOf(), isOnline = false, postId = 1)

        postDetailsViewModel.updateCommentsFromPost(1)

        postDetailsViewModel.viewState().observeForever {
            assertTrue("Error event should be NoConnectionException", it.errorEvent?.getContentIfNotHandled() is NoConnectionException)
        }
    }

    @Test
    fun `Should fetch comments from repository without error when update comments from post operation is successful`() {
        initViewModel(comments = listOf(), isOnline = true, postId = 1)
        every { postDetailsRepository.updateCommentsFromPost(1) } returns Completable.complete()

        postDetailsViewModel.updateCommentsFromPost(1)

        postDetailsViewModel.viewState().observeForever {
            assertNull("Error event should be null", it.errorEvent?.getContentIfNotHandled())
        }
    }

    @Test
    fun `Should emit error state when update comments operation fails`() {
        initViewModel(comments = listOf(), isOnline = true, postId = 1)
        every { postDetailsRepository.updateCommentsFromPost(1) } returns Completable.error(Exception())

        postDetailsViewModel.updateCommentsFromPost(1)

        postDetailsViewModel.viewState().observeForever {
            assertTrue("Error event should be Exception", it.errorEvent?.getContentIfNotHandled() is Exception)
        }
    }

    @Test
    fun `Should emit loading state when start fetching comments`() {
        initViewModel(comments = listOf(), isOnline = true, postId = 1)
        every { postDetailsRepository.updateCommentsFromPost(1) } returns Completable.complete()

        var loadingFlag = false
        postDetailsViewModel.viewState().observeForever {
            if (it.isLoading) {
                loadingFlag = true
            }
        }

        postDetailsViewModel.updateCommentsFromPost(1)

        assertTrue(loadingFlag)
    }

    @Test
    fun `Should add disposable to CompositeDisposable when fetching comments`() {
        initViewModel(comments = listOf(), isOnline = true, postId = 1)
        every { postDetailsRepository.updateCommentsFromPost(1) } returns Completable.complete()

        postDetailsViewModel.updateCommentsFromPost(1)

        verify { compositeDisposable.add(any()) }
    }

    private fun initViewModel(comments: List<Comment>, isOnline: Boolean, postId: Int) {
        every { postDetailsRepository.getCommentsFromPost(postId) } returns MutableLiveData<List<Comment>>(comments)
        every { connectionChecker.isOnline() } returns isOnline

        postDetailsViewModel = PostDetailsViewModel(postDetailsRepository, connectionChecker, compositeDisposable, postId)
    }
}