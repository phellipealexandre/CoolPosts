package com.phellipesilva.coolposts.postlist.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.phellipesilva.coolposts.exceptions.NoConnectionException
import com.phellipesilva.coolposts.postdetails.data.Comment
import com.phellipesilva.coolposts.postlist.data.Post
import com.phellipesilva.coolposts.postlist.repository.PostListRepository
import com.phellipesilva.coolposts.postlist.view.PostListViewState
import com.phellipesilva.coolposts.state.ConnectionChecker
import com.phellipesilva.coolposts.state.ViewState
import com.phellipesilva.coolposts.utils.RxUtils
import io.reactivex.Completable
import io.reactivex.Single
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
class PostListViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var postListRepository: PostListRepository

    @Mock
    private lateinit var connectionChecker: ConnectionChecker

    @Mock
    private lateinit var compositeDisposable: CompositeDisposable

    private lateinit var postListViewModel: PostListViewModel

    private val posts = listOf(
        Post(
            id = 1,
            title = "Title",
            body = "Body",
            userId = 2,
            userName = "User name"
        )
    )

    @Before
    fun setUp() {
        val mutableLiveData = MutableLiveData<List<Post>>()
        mutableLiveData.value = posts
        whenever(postListRepository.getPosts()).thenReturn(mutableLiveData)

        postListViewModel = PostListViewModel(postListRepository, connectionChecker, compositeDisposable)
        RxUtils.overridesEnvironmentToCustomScheduler(Schedulers.trampoline())
        whenever(connectionChecker.isOnline()).thenReturn(true)
    }

    @After
    fun tearDown() {
        RxUtils.resetSchedulers()
    }

    @Test
    fun shouldGetPostLiveDataFromRepositoryOnInitialState() {
        postListViewModel.viewState().observeForever {
            assertEquals(
                PostListViewState.buildSuccessState(posts),
                it
            )
        }
    }

    @Test
    fun shouldEmitNoInternetEventWhenThereIsNoInternet() {
        var errorFlag = false
        whenever(connectionChecker.isOnline()).thenReturn(false)
        postListViewModel.viewState().observeForever {
            if (it.throwable?.peekContent() is NoConnectionException) {
                errorFlag = true
            }
        }

        postListViewModel.updatePosts()

        assertTrue(errorFlag)
    }

    @Test
    fun shouldEmitErrorEventWhenPostFetchingFinishesWithUnexpectedError() {
        var errorFlag = false
        whenever(postListRepository.updatePosts()).thenReturn(Completable.error(Exception()))
        postListViewModel.viewState().observeForever {
            if (it.throwable?.peekContent() is Exception) {
                errorFlag = true
            }
        }

        postListViewModel.updatePosts()

        assertTrue(errorFlag)
    }

    @Test
    fun shouldEmitLoadingEventWhenStartPostsFetching() {
        var loadingFlag = false
        whenever(postListRepository.updatePosts()).thenReturn(Completable.error(Throwable()))
        postListViewModel.viewState().observeForever {
            if (it.viewState == ViewState.LOADING) {
                loadingFlag = true
            }
        }

        postListViewModel.updatePosts()

        assertTrue(loadingFlag)
    }

    @Test
    fun shouldAddDisposableToCompositeDisposableWhenFetchingPosts() {
        whenever(postListRepository.updatePosts()).thenReturn(Completable.complete())

        postListViewModel.updatePosts()

        verify(compositeDisposable).add(any())
    }
}