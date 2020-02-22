package com.phellipesilva.coolposts.postlist.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.phellipesilva.coolposts.postlist.database.PostDao
import com.phellipesilva.coolposts.postlist.database.entity.PostEntity
import com.phellipesilva.coolposts.postlist.domain.Post
import com.phellipesilva.coolposts.postlist.service.PostService
import com.phellipesilva.coolposts.postlist.service.entity.PostRemoteEntity
import com.phellipesilva.coolposts.postlist.service.entity.UserRemoteEntity
import com.phellipesilva.coolposts.utils.RxUtils
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.assertEquals
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
    fun `Set Up`() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)

        postListRepository = PostListRepository(postService, postDao)
        RxUtils.overridesRXSchedulers(Schedulers.trampoline())
    }

    @After
    fun `Tear Down`() {
        RxUtils.resetSchedulers()
    }

    @Test
    fun `Should return empty post list when nothing is stored on database`() {
        every { postDao.getPosts() } returns MutableLiveData<List<PostEntity>>()

        postListRepository.getPosts().observeForever {
            assertEquals(0, it.size)
        }
    }

    @Test
    fun `Should return one post domain entity when one database entity is stored on database`() {
        val singlePostEntity = listOf(
            PostEntity(
                id = 1,
                title = "Title",
                body = "Body",
                userId = 99,
                userName = "User Name"
            )
        )
        every { postDao.getPosts() } returns MutableLiveData<List<PostEntity>>(singlePostEntity)

        postListRepository.getPosts().observeForever {
            assertEquals(1, it.size)
            assertEquals(
                Post(
                    id = 1,
                    title = "Title",
                    body = "Body",
                    userId = 99,
                    userName = "User Name"
                ), it.first()
            )
        }
    }

    @Test
    fun `Should return multiple post domain entities when multiple database entities are stored on database`() {
        val multiplePostEntity = listOf(
            PostEntity(
                id = 1,
                title = "Title",
                body = "Body",
                userId = 99,
                userName = "User Name"
            ),
            PostEntity(
                id = 2,
                title = "Title2",
                body = "Body2",
                userId = 98,
                userName = "User Name2"
            )
        )
        every { postDao.getPosts() } returns MutableLiveData<List<PostEntity>>(multiplePostEntity)

        postListRepository.getPosts().observeForever {
            assertEquals(2, it.size)
            assertEquals(
                listOf(
                    Post(
                        id = 1,
                        title = "Title",
                        body = "Body",
                        userId = 99,
                        userName = "User Name"
                    ),
                    Post(
                        id = 2,
                        title = "Title2",
                        body = "Body2",
                        userId = 98,
                        userName = "User Name2"
                    )
                ), it
            )
        }
    }

    @Test
    fun `Should save posts on database when posts and users requests are successful`() {
        val postsSingle = Single.just(listOf<PostRemoteEntity>())
        val usersSingle = Single.just(listOf<UserRemoteEntity>())
        every { postService.fetchPosts() } returns postsSingle
        every { postService.fetchUsers() } returns usersSingle

        postListRepository.updatePosts().test()

        verify { postDao.savePosts(any()) }
    }

    @Test
    fun `Should complete operation when posts and users requests are successful`() {
        val postsSingle = Single.just(listOf<PostRemoteEntity>())
        val usersSingle = Single.just(listOf<UserRemoteEntity>())
        every { postService.fetchPosts() } returns postsSingle
        every { postService.fetchUsers() } returns usersSingle
        every { postDao.savePosts(any()) } returns Completable.complete()

        postListRepository.updatePosts().test().assertComplete()
    }

    @Test
    fun `Should throw error when post request is not successful`() {
        val expectedError = Throwable("Error")
        val postsSingle = Single.error<List<PostRemoteEntity>>(expectedError)
        every { postService.fetchPosts() } returns postsSingle

        val expectedUsers = listOf<UserRemoteEntity>()
        val usersSingle = Single.just(expectedUsers)
        every { postService.fetchUsers() } returns usersSingle

        postListRepository.updatePosts().test()
            .assertNotComplete()
            .assertError { it == expectedError }

        verify(exactly = 0) { postDao.savePosts(any()) }
    }

    @Test
    fun `Should throw error when user request is not successful`() {
        val expectedPosts = listOf<PostRemoteEntity>()
        val postsSingle = Single.just(expectedPosts)
        every { postService.fetchPosts() } returns postsSingle

        val expectedError = Throwable("Error")
        val usersSingle = Single.error<List<UserRemoteEntity>>(expectedError)
        every { postService.fetchUsers() } returns usersSingle

        postListRepository.updatePosts().test()
            .assertNotComplete()
            .assertError { it == expectedError }

        verify(exactly = 0) { postDao.savePosts(any()) }
    }



    @Test
    fun `Should map post and user remote entities to single post database entity`() {
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

        postListRepository.updatePosts().test()

        verify {
            postDao.savePosts(
                listOf(
                    PostEntity(
                        id = 1,
                        title = "Title",
                        body = "Body",
                        userId = 99,
                        userName = "User Name"
                    )
                )
            )
        }
    }

    @Test
    fun `Should map post and user remote entities to multiple post database entities`() {
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
        every { postService.fetchUsers() } returns Single.just(
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

        postListRepository.updatePosts().test()

        verify {
            postDao.savePosts(
                listOf(
                    PostEntity(
                        id = 1,
                        title = "Title",
                        body = "Body",
                        userId = 99,
                        userName = "User Name"
                    ),
                    PostEntity(
                        id = 2,
                        title = "Title2",
                        body = "Body2",
                        userId = 99,
                        userName = "User Name"
                    ),
                    PostEntity(
                        id = 3,
                        title = "Title3",
                        body = "Body3",
                        userId = 98,
                        userName = "User Name 2"
                    )
                )
            )
        }
    }
}