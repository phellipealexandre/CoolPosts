package com.phellipesilva.coolposts.postdetails.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.phellipesilva.coolposts.exceptions.NoConnectionException
import com.phellipesilva.coolposts.postdetails.data.Comment
import com.phellipesilva.coolposts.postdetails.repository.PostDetailsRepository
import com.phellipesilva.coolposts.postdetails.view.PostDetailsViewState
import com.phellipesilva.coolposts.state.ConnectionChecker
import com.phellipesilva.coolposts.state.ViewState
import com.phellipesilva.coolposts.utils.RxUtils
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PostDetailsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var postDetailsRepository: PostDetailsRepository

    @Mock
    private lateinit var connectionChecker: ConnectionChecker

    @Mock
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
        val mutableLiveData = MutableLiveData<List<Comment>>()
        mutableLiveData.value = comments
        whenever(postDetailsRepository.getCommentsFromPost(postId)).thenReturn(mutableLiveData)

        postDetailsViewModel = PostDetailsViewModel(postDetailsRepository, connectionChecker, compositeDisposable, postId)
        RxUtils.overridesEnvironmentToCustomScheduler(Schedulers.trampoline())
        whenever(connectionChecker.isOnline()).thenReturn(true)
    }

    @After
    fun tearDown() {
        RxUtils.resetSchedulers()
    }

    @Test
    fun shouldGetCommentLiveDataFromRepositoryOnInitialState() {
        postDetailsViewModel.viewState().observeForever {
            assertEquals(
                PostDetailsViewState.buildSuccessState(comments),
                it
            )
        }
    }

    @Test
    fun shouldEmitNoInternetEventWhenThereIsNoInternet() {
        var errorFlag = false
        whenever(connectionChecker.isOnline()).thenReturn(false)
        postDetailsViewModel.viewState().observeForever {
            if (it.throwable?.peekContent() is NoConnectionException) {
                errorFlag = true
            }
        }

        postDetailsViewModel.updateCommentsFromPost(1)

        assertTrue(errorFlag)
    }

    @Test
    fun shouldEmitErrorEventWhenCommentsFetchingFinishesWithUnexpectedError() {
        var errorFlag = false
        whenever(postDetailsRepository.updateCommentsFromPost(1)).thenReturn(Completable.error(Exception()))
        postDetailsViewModel.viewState().observeForever {
            if (it.throwable?.peekContent() is Exception) {
                errorFlag = true
            }
        }

        postDetailsViewModel.updateCommentsFromPost(1)

        assertTrue(errorFlag)
    }

    @Test
    fun shouldEmitLoadingEventWhenStartCommentsFetching() {
        var loadingFlag = false
        whenever(postDetailsRepository.updateCommentsFromPost(1)).thenReturn(Completable.error(Throwable()))
        postDetailsViewModel.viewState().observeForever {
            if (it.viewState == ViewState.LOADING) {
                loadingFlag = true
            }
        }

        postDetailsViewModel.updateCommentsFromPost(1)

        assertTrue(loadingFlag)
    }

    @Test
    fun shouldAddDisposableToCompositeDisposableWhenFetchingComments() {
        whenever(postDetailsRepository.updateCommentsFromPost(1)).thenReturn(Completable.complete())

        postDetailsViewModel.updateCommentsFromPost(1)

        verify(compositeDisposable).add(any())
    }
}