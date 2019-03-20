package com.phellipesilva.coolposts.postlist.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.phellipesilva.coolposts.postlist.data.Post
import com.phellipesilva.coolposts.postlist.repository.PostListRepository
import com.phellipesilva.coolposts.utils.RxUtils
import com.phellipesilva.coolposts.state.ConnectionChecker
import com.phellipesilva.coolposts.state.ViewState
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PostListViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var postListRepository: PostListRepository

    @Mock
    private lateinit var connectionChecker: ConnectionChecker

    @Mock
    private lateinit var compositeDisposable: CompositeDisposable

    @Mock
    private lateinit var postsLiveData: MutableLiveData<List<Post>>

    private lateinit var postListViewModel: PostListViewModel

    @Before
    fun setUp() {
        whenever(postListRepository.getPosts()).thenReturn(postsLiveData)

        postListViewModel = PostListViewModel(postListRepository, connectionChecker, compositeDisposable)
        RxUtils.overridesEnvironmentToCustomScheduler(Schedulers.trampoline())
        whenever(connectionChecker.isOnline()).thenReturn(true)
    }

    @After
    fun tearDown() {
        RxUtils.resetSchedulers()
    }

    @Test
    fun shouldEmitSuccessEventWhenPostFetchingFinishesSuccessfully() {
        whenever(postListRepository.updatePosts()).thenReturn(Completable.complete())

        postListViewModel.updatePosts()

        postListViewModel.viewState().observeForever {
            assertEquals(ViewState.SUCCESS, it.peekContent())
        }
    }

    @Test
    fun shouldEmitNoInternetEventWhenThereIsNoInternet() {
        whenever(connectionChecker.isOnline()).thenReturn(false)

        postListViewModel.updatePosts()

        postListViewModel.viewState().observeForever {
            assertEquals(ViewState.NO_INTERNET, it.peekContent())
        }
    }

    @Test
    fun shouldEmitErrorEventWhenPostFetchingFinishesWithError() {
        whenever(postListRepository.updatePosts()).thenReturn(Completable.error(Throwable()))

        postListViewModel.updatePosts()

        postListViewModel.viewState().observeForever {
            assertEquals(ViewState.UNEXPECTED_ERROR, it.peekContent())
        }
    }

    @Test
    fun shouldAddDisposableToCompositeDisposableWhenFetchingPosts() {
        whenever(postListRepository.updatePosts()).thenReturn(Completable.complete())

        postListViewModel.updatePosts()

        verify(compositeDisposable).add(any())
    }

    @Test
    fun shouldGetPostLiveDataFromRepository() {
        postListViewModel.getPosts().observeForever {
            assertEquals(postsLiveData, it)
        }
    }
}