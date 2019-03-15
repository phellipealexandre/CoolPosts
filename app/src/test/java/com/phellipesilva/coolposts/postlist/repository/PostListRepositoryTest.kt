package com.phellipesilva.coolposts.postlist.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.phellipesilva.coolposts.postlist.database.PostDAO
import com.phellipesilva.coolposts.postlist.database.UserDAO
import com.phellipesilva.coolposts.postlist.domain.Post
import com.phellipesilva.coolposts.postlist.domain.User
import com.phellipesilva.coolposts.postlist.entities.PostEntity
import com.phellipesilva.coolposts.postlist.entities.UserEntity
import com.phellipesilva.coolposts.postlist.service.PostService
import com.phellipesilva.coolposts.postlist.utils.RxUtils
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
    private lateinit var postDAO: PostDAO

    @Mock
    private lateinit var userDAO: UserDAO

    private lateinit var postListRepository: PostListRepository

    @Before
    fun setUp() {
        postListRepository = PostListRepository(postService, postDAO, userDAO)
        RxUtils.overridesEnvironmentToCustomScheduler(Schedulers.trampoline())
    }

    @After
    fun tearDown() {
        RxUtils.resetSchedulers()
    }

    @Test
    fun shouldSavePostsOnDatabaseWhenPostAndUserRequestsAreSuccessful() {
        val expectedPosts = listOf<PostEntity>()
        val postsSingle = Single.just(expectedPosts)
        whenever(postService.getPosts()).thenReturn(postsSingle)

        val expectedUsers = listOf<UserEntity>()
        val usersSingle = Single.just(expectedUsers)
        whenever(postService.getUsers()).thenReturn(usersSingle)

        val testObserver = TestObserver<Unit>()
        postListRepository.fetchPosts().subscribe(testObserver)

        verify(postDAO).savePosts(expectedPosts)
    }

    @Test
    fun shouldSaveUsersOnDatabaseWhenPostAndUserRequestsAreSuccessful() {
        val expectedPosts = listOf<PostEntity>()
        val postsSingle = Single.just(expectedPosts)
        whenever(postService.getPosts()).thenReturn(postsSingle)

        val expectedUsers = listOf<UserEntity>()
        val usersSingle = Single.just(expectedUsers)
        whenever(postService.getUsers()).thenReturn(usersSingle)

        val testObserver = TestObserver<Unit>()
        postListRepository.fetchPosts().subscribe(testObserver)

        verify(userDAO).saveUsers(expectedUsers)
    }

    @Test
    fun shouldThrowErrorOnCompletionWhenPostRequestsHasError() {
        val expectedError = Throwable("Error")
        val postsSingle = Single.error<List<PostEntity>>(expectedError)
        whenever(postService.getPosts()).thenReturn(postsSingle)

        val expectedUsers = listOf<UserEntity>()
        val usersSingle = Single.just(expectedUsers)
        whenever(postService.getUsers()).thenReturn(usersSingle)

        val testObserver = TestObserver<Unit>()
        postListRepository.fetchPosts().subscribe(testObserver)

        testObserver.assertError { it == expectedError }
    }

    @Test
    fun shouldThrowErrorOnCompletionWhenUserRequestsHasError() {
        val expectedPosts = listOf<PostEntity>()
        val postsSingle = Single.just(expectedPosts)
        whenever(postService.getPosts()).thenReturn(postsSingle)

        val expectedError = Throwable("Error")
        val usersSingle = Single.error<List<UserEntity>>(expectedError)
        whenever(postService.getUsers()).thenReturn(usersSingle)

        val testObserver = TestObserver<Unit>()
        postListRepository.fetchPosts().subscribe(testObserver)

        testObserver.assertError { it == expectedError }
    }

    @Test
    fun shouldReturnEmptyPostListWhenNothingIsStoredOnDatabase() {
        whenever(postDAO.getAllPosts()).thenReturn(MutableLiveData<List<PostEntity>>())

        postListRepository.getPosts().observeForever {
            assertEquals(0, it.size)
        }
    }

    @Test
    fun shouldMapLiveDataEntitiesWithOneElementToLiveDataWithSinglePost() {
        val expectedPost = Post(
            id = 1,
            title = "Title",
            body = "Body",
            user = User(
                id = 99,
                name = "User Name",
                website = "Website"
            )
        )

        val postsLiveData = MutableLiveData<List<PostEntity>>()
        postsLiveData.value = listOf(
            PostEntity(
                id = 1,
                userId = 99,
                title = "Title",
                body = "Body"
            )
        )
        whenever(postDAO.getAllPosts()).thenReturn(postsLiveData)

        val usersLiveData = MutableLiveData<List<UserEntity>>()
        usersLiveData.value = listOf(
            UserEntity(
                id = 99,
                name = "User Name",
                website = "Website"
            )
        )
        whenever(userDAO.getAllUsers()).thenReturn(usersLiveData)

        postListRepository.getPosts().observeForever {
            assertEquals(listOf(expectedPost), it)
        }
    }

    @Test
    fun shouldMapLiveDataEntitiesWithManyElementsToLiveDataWithManyPost() {
        val expectedPost1 = Post(
            id = 1,
            title = "Title",
            body = "Body",
            user = User(
                id = 99,
                name = "User Name",
                website = "Website"
            )
        )
        val expectedPost2 = Post(
            id = 2,
            title = "Title2",
            body = "Body2",
            user = User(
                id = 99,
                name = "User Name",
                website = "Website"
            )
        )
        val expectedPost3 = Post(
            id = 3,
            title = "Title3",
            body = "Body3",
            user = User(
                id = 98,
                name = "User Name 2",
                website = "Website 2"
            )
        )

        val postsLiveData = MutableLiveData<List<PostEntity>>()
        postsLiveData.value = listOf(
            PostEntity(
                id = 1,
                userId = 99,
                title = "Title",
                body = "Body"
            ),
            PostEntity(
                id = 2,
                userId = 99,
                title = "Title2",
                body = "Body2"
            ),
            PostEntity(
                id = 3,
                userId = 98,
                title = "Title3",
                body = "Body3"
            )
        )
        whenever(postDAO.getAllPosts()).thenReturn(postsLiveData)

        val usersLiveData = MutableLiveData<List<UserEntity>>()
        usersLiveData.value = listOf(
            UserEntity(
                id = 99,
                name = "User Name",
                website = "Website"
            ),
            UserEntity(
                id = 98,
                name = "User Name 2",
                website = "Website 2"
            )
        )
        whenever(userDAO.getAllUsers()).thenReturn(usersLiveData)

        postListRepository.getPosts().observeForever {
            assertEquals(listOf(expectedPost1, expectedPost2, expectedPost3), it)
        }
    }
}