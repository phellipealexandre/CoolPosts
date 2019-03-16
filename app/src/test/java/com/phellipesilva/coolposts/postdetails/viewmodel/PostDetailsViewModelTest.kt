package com.phellipesilva.coolposts.postdetails.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.phellipesilva.coolposts.postdetails.entity.CommentEntity
import com.phellipesilva.coolposts.postdetails.repository.PostDetailsRepository
import com.phellipesilva.coolposts.postlist.utils.RxUtils
import com.phellipesilva.coolposts.state.ConnectionManager
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
    private lateinit var connectionManager: ConnectionManager

    @Mock
    private lateinit var compositeDisposable: CompositeDisposable

    private lateinit var postDetailsViewModel: PostDetailsViewModel

    @Before
    fun setUp() {
        postDetailsViewModel = PostDetailsViewModel(postDetailsRepository, connectionManager, compositeDisposable)
        RxUtils.overridesEnvironmentToCustomScheduler(Schedulers.trampoline())
        whenever(connectionManager.isOnline()).thenReturn(true)
    }

    @After
    fun tearDown() {
        RxUtils.resetSchedulers()
    }

    @Test
    fun shouldEmitSuccessEventWhenPostFetchingFinishesSuccessfully() {
        whenever(postDetailsRepository.fetchComments(1)).thenReturn(Completable.complete())

        postDetailsViewModel.fetchComments(1)

        postDetailsViewModel.viewState().observeForever {
            assertEquals(ViewState.SUCCESS, it.peekContent())
        }
    }

    @Test
    fun shouldEmitNoInternetEventWhenThereIsNoInternet() {
        whenever(connectionManager.isOnline()).thenReturn(false)

        postDetailsViewModel.fetchComments(1)

        postDetailsViewModel.viewState().observeForever {
            assertEquals(ViewState.NO_INTERNET, it.peekContent())
        }
    }

    @Test
    fun shouldEmitErrorEventWhenPostFetchingFinishesWithError() {
        whenever(postDetailsRepository.fetchComments(1)).thenReturn(Completable.error(Throwable()))

        postDetailsViewModel.fetchComments(1)

        postDetailsViewModel.viewState().observeForever {
            assertEquals(ViewState.UNEXPECTED_ERROR, it.peekContent())
        }
    }

    @Test
    fun shouldAddDisposableToCompositeDisposableWhenFetchingComments() {
        whenever(postDetailsRepository.fetchComments(1)).thenReturn(Completable.complete())

        postDetailsViewModel.fetchComments(1)

        verify(compositeDisposable).add(any())
    }

    @Test
    fun shouldGetCommentLiveDataFromRepository() {
        val postsLiveData = MutableLiveData<List<CommentEntity>>()
        whenever(postDetailsRepository.getComments(1)).thenReturn(postsLiveData)

        postDetailsViewModel.getCommentsObservable(1).observeForever {
            assertEquals(postsLiveData, it)
        }
    }

}