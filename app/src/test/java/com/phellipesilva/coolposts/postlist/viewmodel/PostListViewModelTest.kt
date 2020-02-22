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
import org.junit.Assert.*

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
    fun `Should get posts LiveData from repository with initial state`() {
        val posts = listOf(
            Post(
                id = 1,
                title = "Title",
                body = "Body",
                userId = 2,
                userName = "User name"
            )
        )

        initViewModel(initialStatePosts = posts, isOnline = true)

        postListViewModel.viewState().observeForever {
            assertEquals(PostListViewState(isLoading = false, posts = posts), it)
        }
    }

    @Test
    fun `Should emit no internet state when there is no internet`() {
        initViewModel(initialStatePosts = listOf(), isOnline = false)

        postListViewModel.updatePosts()

        postListViewModel.viewState().observeForever {
            assertTrue("Error event should be NoConnectionException", it.errorEvent?.getContentIfNotHandled() is NoConnectionException)
        }
    }

    @Test
    fun `Should fetch posts from repository without error when update posts operation is successful`() {
        initViewModel(initialStatePosts = listOf(), isOnline = true)
        every { postListRepository.updatePosts() } returns Completable.complete()

        postListViewModel.updatePosts()

        postListViewModel.viewState().observeForever {
            assertNull("Error event should be null", it.errorEvent?.getContentIfNotHandled())
        }
    }

    @Test
    fun `Should emit error state when update posts operation fails`() {
        initViewModel(initialStatePosts = listOf(), isOnline = true)
        every { postListRepository.updatePosts() } returns Completable.error(Exception())

        postListViewModel.updatePosts()

        postListViewModel.viewState().observeForever {
            assertTrue("Error event should be Exception", it.errorEvent?.getContentIfNotHandled() is Exception)
        }
    }

    @Test
    fun `Should emit loading state when start fetching posts`() {
        initViewModel(initialStatePosts = listOf(), isOnline = true)
        every { postListRepository.updatePosts() } returns Completable.complete()

        var loadingFlag = false
        postListViewModel.viewState().observeForever {
            if (it.isLoading) {
                loadingFlag = true
            }
        }

        postListViewModel.updatePosts()

        assertTrue(loadingFlag)
    }

    @Test
    fun `Should add disposable to CompositeDisposable when fetching posts`() {
        initViewModel(initialStatePosts = listOf(), isOnline = true)
        every { postListRepository.updatePosts() } returns Completable.complete()

        postListViewModel.updatePosts()

        verify { compositeDisposable.add(any()) }
    }

    private fun initViewModel(initialStatePosts: List<Post>, isOnline: Boolean) {
        val initialLiveDataFromRepository = MutableLiveData<List<Post>>(initialStatePosts)
        every { postListRepository.getPosts() } returns initialLiveDataFromRepository
        every { connectionChecker.isOnline() } returns isOnline

        postListViewModel = PostListViewModel(postListRepository, connectionChecker, compositeDisposable)

    }
}