package com.phellipesilva.coolposts.postlist.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.phellipesilva.coolposts.postlist.database.PostDao
import com.phellipesilva.coolposts.postlist.data.Post
import com.phellipesilva.coolposts.postlist.service.remote.PostRemoteEntity
import com.phellipesilva.coolposts.postlist.service.remote.UserRemoteEntity
import com.phellipesilva.coolposts.postlist.service.PostService
import com.phellipesilva.coolposts.utils.RxUtils
import io.reactivex.Single
import io.reactivex.observers.TestObserver
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
class PostListRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var postService: PostService

    @Mock
    private lateinit var postDao: PostDao

    private lateinit var postListRepository: PostListRepository

    @Before
    fun setUp() {
        postListRepository = PostListRepository(postService, postDao)
        RxUtils.overridesEnvironmentToCustomScheduler(Schedulers.trampoline())
    }

    @After
    fun tearDown() {
        RxUtils.resetSchedulers()
    }

    @Test
    fun shouldTryToSavePostsOnDatabaseWhenPostAndUserRequestsAreSuccessful() {
        val expectedPosts = listOf<PostRemoteEntity>()
        val postsSingle = Single.just(expectedPosts)
        whenever(postService.fetchPosts()).thenReturn(postsSingle)

        val expectedUsers = listOf<UserRemoteEntity>()
        val usersSingle = Single.just(expectedUsers)
        whenever(postService.fetchUsers()).thenReturn(usersSingle)

        val testObserver = TestObserver<List<Post>>()
        postListRepository.updatePosts().subscribe(testObserver)

        verify(postDao).savePosts(listOf())
    }

    @Test
    fun shouldThrowErrorOnCompletionWhenPostRequestsHasError() {
        val expectedError = Throwable("Error")
        val postsSingle = Single.error<List<PostRemoteEntity>>(expectedError)
        whenever(postService.fetchPosts()).thenReturn(postsSingle)

        val expectedUsers = listOf<UserRemoteEntity>()
        val usersSingle = Single.just(expectedUsers)
        whenever(postService.fetchUsers()).thenReturn(usersSingle)

        val testObserver = TestObserver<List<Post>>()
        postListRepository.updatePosts().subscribe(testObserver)

        testObserver.assertNotComplete()
        testObserver.assertError { it == expectedError }
        verify(postDao, never()).savePosts(any())
    }

    @Test
    fun shouldThrowErrorOnCompletionWhenUserRequestsHasError() {
        val expectedPosts = listOf<PostRemoteEntity>()
        val postsSingle = Single.just(expectedPosts)
        whenever(postService.fetchPosts()).thenReturn(postsSingle)

        val expectedError = Throwable("Error")
        val usersSingle = Single.error<List<UserRemoteEntity>>(expectedError)
        whenever(postService.fetchUsers()).thenReturn(usersSingle)

        val testObserver = TestObserver<List<Post>>()
        postListRepository.updatePosts().subscribe(testObserver)

        testObserver.assertNotComplete()
        testObserver.assertError { it == expectedError }
        verify(postDao, never()).savePosts(any())
    }

    @Test
    fun shouldReturnEmptyPostListWhenNothingIsStoredOnDatabase() {
        whenever(postDao.getPosts()).thenReturn(MutableLiveData<List<Post>>())

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

        whenever(postService.fetchPosts()).thenReturn(
            Single.just(
                listOf(
                    PostRemoteEntity(
                        id = 1,
                        userId = 99,
                        title = "Title",
                        body = "Body"
                    )
                )
            )
        )

        whenever(postService.fetchUsers()).thenReturn(
            Single.just(
                listOf(
                    UserRemoteEntity(
                        id = 99,
                        name = "User Name"
                    )
                )
            )
        )

        val testObserver = TestObserver<List<Post>>()
        postListRepository.updatePosts().subscribe(testObserver)

        verify(postDao).savePosts(listOf(expectedPost))
        testObserver.assertComplete()
        testObserver.assertValue(listOf(expectedPost))
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

        whenever(postService.fetchPosts()).thenReturn(
            Single.just(
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
        )

        whenever(postService.fetchUsers()).thenReturn(
            Single.just(
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
        )

        val testObserver = TestObserver<List<Post>>()
        postListRepository.updatePosts().subscribe(testObserver)

        verify(postDao).savePosts(listOf(expectedPost1, expectedPost2, expectedPost3))
        testObserver.assertComplete()
        testObserver.assertValue(listOf(expectedPost1, expectedPost2, expectedPost3))
    }
}