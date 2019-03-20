package com.phellipesilva.coolposts.postdetails.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.phellipesilva.coolposts.postdetails.data.Comment
import com.phellipesilva.coolposts.postdetails.repository.PostDetailsRepository
import com.phellipesilva.coolposts.utils.RxUtils
import com.phellipesilva.coolposts.state.ConnectionChecker
import com.phellipesilva.coolposts.state.ViewState
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.assertEquals
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

    @Before
    fun setUp() {
        postDetailsViewModel = PostDetailsViewModel(postDetailsRepository, connectionChecker, compositeDisposable)
        RxUtils.overridesEnvironmentToCustomScheduler(Schedulers.trampoline())
        whenever(connectionChecker.isOnline()).thenReturn(true)
    }

    @After
    fun tearDown() {
        RxUtils.resetSchedulers()
    }

    @Test
    fun shouldEmitSuccessEventWhenPostFetchingFinishesSuccessfully() {
        whenever(postDetailsRepository.updateCommentsFromPost(1)).thenReturn(Completable.complete())

        postDetailsViewModel.updateCommentsFromPost(1)

        postDetailsViewModel.viewState().observeForever {
            assertEquals(ViewState.SUCCESS, it.peekContent())
        }
    }

    @Test
    fun shouldEmitNoInternetEventWhenThereIsNoInternet() {
        whenever(connectionChecker.isOnline()).thenReturn(false)

        postDetailsViewModel.updateCommentsFromPost(1)

        postDetailsViewModel.viewState().observeForever {
            assertEquals(ViewState.NO_INTERNET, it.peekContent())
        }
    }

    @Test
    fun shouldEmitErrorEventWhenPostFetchingFinishesWithError() {
        whenever(postDetailsRepository.updateCommentsFromPost(1)).thenReturn(Completable.error(Throwable()))

        postDetailsViewModel.updateCommentsFromPost(1)

        postDetailsViewModel.viewState().observeForever {
            assertEquals(ViewState.UNEXPECTED_ERROR, it.peekContent())
        }
    }

    @Test
    fun shouldAddDisposableToCompositeDisposableWhenFetchingComments() {
        whenever(postDetailsRepository.updateCommentsFromPost(1)).thenReturn(Completable.complete())

        postDetailsViewModel.updateCommentsFromPost(1)

        verify(compositeDisposable).add(any())
    }

    @Test
    fun shouldGetCommentLiveDataFromRepository() {
        val postsLiveData = MutableLiveData<List<Comment>>()
        whenever(postDetailsRepository.getCommentsFromPost(1)).thenReturn(postsLiveData)

        postDetailsViewModel.getCommentsFromPost(1).observeForever {
            assertEquals(postsLiveData, it)
        }
    }

}