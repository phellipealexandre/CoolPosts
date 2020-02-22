package com.phellipesilva.coolposts.database

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.phellipesilva.coolposts.postlist.database.entity.PostEntity
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
        val context = getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, MainDatabase::class.java).build()
        postDao = database.getPostDao()
    }

    @Test
    fun shouldReturnEmptyPostListWhenThereIsNothingStoredInTheDatabase() {
        val postsLiveData = postDao.getPosts()

        postsLiveData.observeForever {
            assertEquals(0, it.size)
            assertNotNull(it)
        }
    }

    @Test
    fun shouldReturnSamePostListValueWhenThereWasAPreviouslyStoredPostList() {
        val post1 = PostEntity(1, "Title", "body", 1, "Name")
        val post2 = PostEntity(2, "Title2", "body2", 1, "Name")
        val postList = listOf(post1, post2)

        postDao.savePosts(postList).subscribe()
        val postsLiveData = postDao.getPosts()

        postsLiveData.observeForever {
            assertEquals(2, it.size)
            assertEquals(post1, it[0])
            assertEquals(post2, it[1])
        }
    }

    @Test
    fun shouldReplacePostsWithSameIdWhenThereIsConflictInStoredPostList() {
        val post1 = PostEntity(1, "Title", "body", 1, "Name")
        val post2 = PostEntity(2, "Title2", "body2", 1, "Name")
        val postList = listOf(post1, post2)

        postDao.savePosts(postList).subscribe()

        val post1New = PostEntity(1, "TitleNew", "bodyNew", 1, "Name")
        val newPostList = listOf(post1New, post2)

        postDao.savePosts(newPostList).subscribe()
        val postsLiveData = postDao.getPosts()

        postsLiveData.observeForever {
            assertEquals(2, it.size)
            assertEquals(post1New, it[0])
            assertEquals(post2, it[1])
        }
    }
}