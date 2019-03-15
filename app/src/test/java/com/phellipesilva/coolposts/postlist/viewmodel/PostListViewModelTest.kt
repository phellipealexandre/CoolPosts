package com.phellipesilva.coolposts.postlist.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.phellipesilva.coolposts.postlist.domain.Post
import com.phellipesilva.coolposts.postlist.repository.PostListRepository
import com.phellipesilva.coolposts.postlist.utils.RxUtils
import com.phellipesilva.coolposts.postlist.viewmodel.state.ViewState
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

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var postListRepository: PostListRepository

    @Mock
    private lateinit var compositeDisposable: CompositeDisposable

    private lateinit var postListViewModel: PostListViewModel

    @Before
    fun setUp() {
        postListViewModel = PostListViewModel(postListRepository, compositeDisposable)
        RxUtils.overridesEnvironmentToCustomScheduler(Schedulers.trampoline())
    }

    @After
    fun tearDown() {
        RxUtils.resetSchedulers()
    }

    @Test
    fun shouldEmitIdleEventWhenPostFetchingFinishesSuccessfully() {
        whenever(postListRepository.fetchPosts()).thenReturn(Completable.complete())

        postListViewModel.fetchPosts()

        postListViewModel.viewState().observeForever {
            assertEquals(ViewState.IDLE, it.peekContent())
        }
    }

    @Test
    fun shouldEmitErrorEventWhenPostFetchingFinishesWithError() {
        whenever(postListRepository.fetchPosts()).thenReturn(Completable.error(Throwable()))

        postListViewModel.fetchPosts()

        postListViewModel.viewState().observeForever {
            assertEquals(ViewState.UNEXPECTED_ERROR, it.peekContent())
        }
    }

    @Test
    fun shouldAddDisposableToCompositeDisposableWhenFetchingPosts() {
        whenever(postListRepository.fetchPosts()).thenReturn(Completable.complete())

        postListViewModel.fetchPosts()

        verify(compositeDisposable).add(any())
    }

    @Test
    fun shouldGetPostLiveDataFromRepository() {
        val postsLiveData = MutableLiveData<List<Post>>()
        whenever(postListRepository.getPosts()).thenReturn(postsLiveData)

        postListViewModel.getPostsObservable().observeForever {
            assertEquals(postsLiveData, it)
        }
    }
}