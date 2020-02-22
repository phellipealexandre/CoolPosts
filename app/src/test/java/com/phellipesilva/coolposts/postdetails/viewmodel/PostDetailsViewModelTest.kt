package com.phellipesilva.coolposts.postdetails.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.phellipesilva.coolposts.exceptions.NoConnectionException
import com.phellipesilva.coolposts.postdetails.data.Comment
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

    private val postId = 1
    private val comments = listOf(
        Comment(
            id = 1,
            postId = 2,
            name = "Name",
            email = "Email",
            body = "Body"
        )
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)

        val initialLiveDataFromRepository = MutableLiveData<List<Comment>>()
        initialLiveDataFromRepository.value = comments
        every { postDetailsRepository.getCommentsFromPost(postId) } returns initialLiveDataFromRepository

        postDetailsViewModel = PostDetailsViewModel(postDetailsRepository, connectionChecker, compositeDisposable, postId)
        RxUtils.overridesEnvironmentToCustomScheduler(Schedulers.trampoline())
        every { connectionChecker.isOnline() } returns true
    }

    @After
    fun tearDown() {
        RxUtils.resetSchedulers()
    }

    @Test
    fun shouldGetCommentsLiveDataFromRepositoryOnInitialState() {
        postDetailsViewModel.viewState().observeForever {
            assertEquals(
                PostDetailsViewState(isLoading = false, comments = comments),
                it
            )
        }
    }

    @Test
    fun shouldFetchCommentsFromRepositoryWithoutErrorWhenFetchingCommentsIsSuccessful() {
        var errorFlag = false
        every { postDetailsRepository.updateCommentsFromPost(1) } returns Completable.complete()
        postDetailsViewModel.viewState().observeForever {
            if (it.errorEvent?.getContentIfNotHandled() != null) {
                errorFlag = true
            }
        }

        postDetailsViewModel.updateCommentsFromPost(1)

        assertFalse(errorFlag)
    }

    @Test
    fun shouldEmitNoInternetStateWhenThereIsNoInternet() {
        var onInternetFlag = false
        every { connectionChecker.isOnline() } returns false
        postDetailsViewModel.viewState().observeForever {
            if (it.errorEvent?.getContentIfNotHandled() is NoConnectionException) {
                onInternetFlag = true
            }
        }

        postDetailsViewModel.updateCommentsFromPost(1)

        assertTrue(onInternetFlag)
    }

    @Test
    fun shouldEmitErrorStateWhenCommentsFetchingFinishesWithUnexpectedError() {
        var errorFlag = false
        every { postDetailsRepository.updateCommentsFromPost(1) } returns Completable.error(Exception())
        postDetailsViewModel.viewState().observeForever {
            if (it.errorEvent?.getContentIfNotHandled() is Exception) {
                errorFlag = true
            }
        }

        postDetailsViewModel.updateCommentsFromPost(1)

        assertTrue(errorFlag)
    }

    @Test
    fun shouldEmitLoadingStateWhenStartFetchingComments() {
        var loadingFlag = false
        every { postDetailsRepository.updateCommentsFromPost(1) } returns Completable.error(Throwable())
        postDetailsViewModel.viewState().observeForever {
            if (it.isLoading) {
                loadingFlag = true
            }
        }

        postDetailsViewModel.updateCommentsFromPost(1)

        assertTrue(loadingFlag)
    }

    @Test
    fun shouldAddDisposableToCompositeDisposableWhenFetchingComments() {
        every { postDetailsRepository.updateCommentsFromPost(1) } returns Completable.complete()

        postDetailsViewModel.updateCommentsFromPost(1)

        verify { compositeDisposable.add(any()) }
    }
}