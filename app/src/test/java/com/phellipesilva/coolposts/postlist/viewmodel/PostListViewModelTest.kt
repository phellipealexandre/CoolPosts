package com.phellipesilva.coolposts.postlist.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.phellipesilva.coolposts.exceptions.NoConnectionException
import com.phellipesilva.coolposts.postlist.domain.Post
import com.phellipesilva.coolposts.postlist.repository.PostListRepository
import com.phellipesilva.coolposts.postlist.view.PostListViewState
import com.phellipesilva.coolposts.state.ConnectionChecker
import com.phellipesilva.coolposts.utils.RxUtils
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class PostListViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var postListRepository: PostListRepository

    @MockK
    private lateinit var connectionChecker: ConnectionChecker

    @MockK
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
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)

        val initialLiveDataFromRepository = MutableLiveData<List<Post>>()
        initialLiveDataFromRepository.value = posts
        every { postListRepository.getPosts() } returns initialLiveDataFromRepository

        postListViewModel = PostListViewModel(postListRepository, connectionChecker, compositeDisposable)
        RxUtils.overridesRXSchedulers(Schedulers.trampoline())
        every { connectionChecker.isOnline() } returns true
    }

    @After
    fun tearDown() {
        RxUtils.resetSchedulers()
    }

    @Test
    fun shouldGetPostLiveDataFromRepositoryOnInitialState() {
        postListViewModel.viewState().observeForever {
            assertEquals(
                PostListViewState(isLoading = false, posts = posts),
                it
            )
        }
    }

    @Test
    fun shouldFetchPostsFromRepositoryWithoutErrorWhenFetchingPostsIsSuccessful() {
        var errorFlag = false
        every { postListRepository.updatePosts() } returns Completable.complete()
        postListViewModel.viewState().observeForever {
            if (it.errorEvent?.getContentIfNotHandled() != null) {
                errorFlag = true
            }
        }

        postListViewModel.updatePosts()

        Assert.assertFalse(errorFlag)
    }

    @Test
    fun shouldEmitNoInternetStateWhenThereIsNoInternet() {
        var errorFlag = false
        every { connectionChecker.isOnline() } returns false
        postListViewModel.viewState().observeForever {
            if (it.errorEvent?.getContentIfNotHandled() is NoConnectionException) {
                errorFlag = true
            }
        }

        postListViewModel.updatePosts()

        assertTrue(errorFlag)
    }

    @Test
    fun shouldEmitErrorStateWhenPostFetchingFinishesWithUnexpectedError() {
        var errorFlag = false
        every { postListRepository.updatePosts() } returns Completable.error(Exception())
        postListViewModel.viewState().observeForever {
            if (it.errorEvent?.getContentIfNotHandled() is Exception) {
                errorFlag = true
            }
        }

        postListViewModel.updatePosts()

        assertTrue(errorFlag)
    }

    @Test
    fun shouldEmitLoadingStateWhenStartPostsFetching() {
        var loadingFlag = false
        every { postListRepository.updatePosts() } returns Completable.error(Throwable())
        postListViewModel.viewState().observeForever {
            if (it.isLoading) {
                loadingFlag = true
            }
        }

        postListViewModel.updatePosts()

        assertTrue(loadingFlag)
    }

    @Test
    fun shouldAddDisposableToCompositeDisposableWhenFetchingPosts() {
        every { postListRepository.updatePosts() } returns Completable.complete()

        postListViewModel.updatePosts()

        verify { compositeDisposable.add(any()) }
    }
}