package com.phellipesilva.coolposts.postlist.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.phellipesilva.coolposts.postlist.database.PostDao
import com.phellipesilva.coolposts.postlist.data.Post
import com.phellipesilva.coolposts.postlist.service.remote.PostRemoteEntity
import com.phellipesilva.coolposts.postlist.service.remote.UserRemoteEntity
import com.phellipesilva.coolposts.postlist.service.PostService
import com.phellipesilva.coolposts.utils.RxUtils
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PostListRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var postService: PostService

    @MockK
    private lateinit var postDao: PostDao

    private lateinit var postListRepository: PostListRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)

        postListRepository = PostListRepository(postService, postDao)
        RxUtils.overridesRXSchedulers(Schedulers.trampoline())
    }

    @After
    fun tearDown() {
        RxUtils.resetSchedulers()
    }

    @Test
    fun shouldTryToSavePostsOnDatabaseWhenPostAndUserRequestsAreSuccessful() {
        val expectedPosts = listOf<PostRemoteEntity>()
        val postsSingle = Single.just(expectedPosts)
        every { postService.fetchPosts() } returns postsSingle

        val expectedUsers = listOf<UserRemoteEntity>()
        val usersSingle = Single.just(expectedUsers)
        every { postService.fetchUsers() } returns usersSingle

        val testObserver = TestObserver<Unit>()
        postListRepository.updatePosts().subscribe(testObserver)

        verify { postDao.savePosts(listOf()) }
    }

    @Test
    fun shouldThrowErrorOnCompletionWhenPostRequestsHasError() {
        val expectedError = Throwable("Error")
        val postsSingle = Single.error<List<PostRemoteEntity>>(expectedError)
        every { postService.fetchPosts() } returns postsSingle

        val expectedUsers = listOf<UserRemoteEntity>()
        val usersSingle = Single.just(expectedUsers)
        every { postService.fetchUsers() } returns usersSingle

        val testObserver = TestObserver<Unit>()
        postListRepository.updatePosts().subscribe(testObserver)

        testObserver.assertNotComplete()
        testObserver.assertError { it == expectedError }
        verify(exactly = 0) { postDao.savePosts(any()) }
    }

    @Test
    fun shouldThrowErrorOnCompletionWhenUserRequestsHasError() {
        val expectedPosts = listOf<PostRemoteEntity>()
        val postsSingle = Single.just(expectedPosts)
        every { postService.fetchPosts() } returns postsSingle

        val expectedError = Throwable("Error")
        val usersSingle = Single.error<List<UserRemoteEntity>>(expectedError)
        every { postService.fetchUsers() } returns usersSingle

        val testObserver = TestObserver<Unit>()
        postListRepository.updatePosts().subscribe(testObserver)

        testObserver.assertNotComplete()
        testObserver.assertError { it == expectedError }
        verify(exactly = 0) { postDao.savePosts(any()) }
    }

    @Test
    fun shouldReturnEmptyPostListWhenNothingIsStoredOnDatabase() {
        every { postDao.getPosts() } returns MutableLiveData<List<Post>>()

        postListRepository.getPosts().observeForever {
            assertEquals(0, it.size)
        }
    }

    @Test
    fun shouldMapPostAndUserEntitiesWithOneElementToLiveDataWithSinglePost() {
        val expectedPost = Post(
            id = 1,
            title = "Title",
            body = "Body",
            userId = 99,
            userName = "User Name"
        )

        every { postService.fetchPosts() } returns Single.just(
            listOf(
                PostRemoteEntity(
                    id = 1,
                    userId = 99,
                    title = "Title",
                    body = "Body"
                )
            )
        )

        every { postService.fetchUsers() } returns Single.just(
            listOf(
                UserRemoteEntity(
                    id = 99,
                    name = "User Name"
                )
            )
        )

        every { postDao.savePosts(listOf(expectedPost)) } returns Completable.complete()

        val testObserver = TestObserver<Unit>()
        postListRepository.updatePosts().subscribe(testObserver)

        verify { postDao.savePosts(listOf(expectedPost)) }
        testObserver.assertComplete()
    }

    @Test
    fun shouldMapLiveDataEntitiesWithManyElementsToLiveDataWithManyPost() {
        val expectedPost1 = Post(
            id = 1,
            title = "Title",
            body = "Body",
            userId = 99,
            userName = "User Name"
        )
        val expectedPost2 = Post(
            id = 2,
            title = "Title2",
            body = "Body2",
            userId = 99,
            userName = "User Name"
        )
        val expectedPost3 = Post(
            id = 3,
            title = "Title3",
            body = "Body3",
            userId = 98,
            userName = "User Name 2"
        )
        every { postService.fetchPosts() } returns Single.just(
            listOf(
                PostRemoteEntity(
                    id = 1,
                    userId = 99,
                    title = "Title",
                    body = "Body"
                ),
                PostRemoteEntity(
                    id = 2,
                    userId = 99,
                    title = "Title2",
                    body = "Body2"
                ),
                PostRemoteEntity(
                    id = 3,
                    userId = 98,
                    title = "Title3",
                    body = "Body3"
                )
            )
        )

        every { postService.fetchUsers() } returns  Single.just(
            listOf(
                UserRemoteEntity(
                    id = 99,
                    name = "User Name"
                ),
                UserRemoteEntity(
                    id = 98,
                    name = "User Name 2"
                )
            )
        )

        every { postDao.savePosts(listOf(expectedPost1, expectedPost2, expectedPost3)) } returns Completable.complete()

        val testObserver = TestObserver<Unit>()
        postListRepository.updatePosts().subscribe(testObserver)

        verify { postDao.savePosts(listOf(expectedPost1, expectedPost2, expectedPost3)) }
        testObserver.assertComplete()
    }
}