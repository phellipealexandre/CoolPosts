package com.phellipesilva.coolposts.postlist.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.phellipesilva.coolposts.postlist.entities.PostEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PostDAOTest {

    private lateinit var postDAO: PostDAO
    private lateinit var database: MainDatabase

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().context
        database = Room.inMemoryDatabaseBuilder(context, MainDatabase::class.java).build()
        postDAO = database.getPostDAO()
    }

    @Test
    fun shouldReturnEmptyPostListWhenThereIsNothingStoredInTheDatabase() {
        val postsLiveData = postDAO.getAllPosts()

        postsLiveData.observeForever {
            assertEquals(0, it.size)
            assertNotNull(it)
        }
    }

    @Test
    fun shouldReturnSamePostListValueWhenThereWasAPreviouslyStoredPostList() {
        val post1 = PostEntity(1, 1, "Title", "body")
        val post2 = PostEntity(1, 2, "Title2", "body2")
        val postList = listOf(post1, post2)

        postDAO.savePosts(postList).subscribe()
        val postsLiveData = postDAO.getAllPosts()

        postsLiveData.observeForever {
            assertEquals(2, it.size)
            assertEquals(post1, it[0])
            assertEquals(post2, it[1])
        }
    }

    @Test
    fun shouldReplacePostsWithSameIdWhenThereIsConflictInStoredPostList() {
        val post1 = PostEntity(1, 1, "Title", "body")
        val post2 = PostEntity(1, 2, "Title2", "body2")
        val postList = listOf(post1, post2)

        postDAO.savePosts(postList).subscribe()

        val post1New = PostEntity(1, 1, "TitleNew", "bodyNew")
        val newPostList = listOf(post1New, post2)

        postDAO.savePosts(newPostList).subscribe()
        val postsLiveData = postDAO.getAllPosts()

        postsLiveData.observeForever {
            assertEquals(2, it.size)
            assertEquals(post1New, it[0])
            assertEquals(post2, it[1])
        }
    }
}