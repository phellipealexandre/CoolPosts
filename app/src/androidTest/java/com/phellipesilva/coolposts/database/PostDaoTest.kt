package com.phellipesilva.coolposts.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.phellipesilva.coolposts.postlist.data.Post
import com.phellipesilva.coolposts.postlist.data.User
import com.phellipesilva.coolposts.postlist.database.PostDao
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PostDaoTest {

    private lateinit var postDao: PostDao
    private lateinit var database: MainDatabase

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().context
        database = Room.inMemoryDatabaseBuilder(context, MainDatabase::class.java).build()
        postDao = database.getPostDao()
    }

    @Test
    fun shouldReturnEmptyPostListWhenThereIsNothingStoredInTheDatabase() {
        val postsLiveData = postDao.getAllPosts()

        postsLiveData.observeForever {
            assertEquals(0, it.size)
            assertNotNull(it)
        }
    }

    @Test
    fun shouldReturnSamePostListValueWhenThereWasAPreviouslyStoredPostList() {
        val post1 = Post(1, "Title", "body", User(1, "Name", "Website"))
        val post2 = Post(2, "Title2", "body2", User(1, "Name", "Website"))
        val postList = listOf(post1, post2)

        postDao.savePosts(postList).subscribe()
        val postsLiveData = postDao.getAllPosts()

        postsLiveData.observeForever {
            assertEquals(2, it.size)
            assertEquals(post1, it[0])
            assertEquals(post2, it[1])
        }
    }

    @Test
    fun shouldReplacePostsWithSameIdWhenThereIsConflictInStoredPostList() {
        val post1 = Post(1, "Title", "body", User(1, "Name", "Website"))
        val post2 = Post(2, "Title2", "body2", User(1, "Name", "Website"))
        val postList = listOf(post1, post2)

        postDao.savePosts(postList).subscribe()

        val post1New = Post(1, "TitleNew", "bodyNew", User(1, "Name", "Website"))
        val newPostList = listOf(post1New, post2)

        postDao.savePosts(newPostList).subscribe()
        val postsLiveData = postDao.getAllPosts()

        postsLiveData.observeForever {
            assertEquals(2, it.size)
            assertEquals(post1New, it[0])
            assertEquals(post2, it[1])
        }
    }
}