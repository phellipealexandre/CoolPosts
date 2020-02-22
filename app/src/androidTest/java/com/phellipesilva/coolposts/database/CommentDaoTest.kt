package com.phellipesilva.coolposts.database

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.phellipesilva.coolposts.postdetails.database.CommentDao
import com.phellipesilva.coolposts.postdetails.data.Comment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CommentDaoTest {

    private lateinit var commentDao: CommentDao
    private lateinit var database: MainDatabase

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        val context = getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, MainDatabase::class.java).build()
        commentDao = database.getCommentDao()
    }

    @Test
    fun shouldReturnEmptyCommentListWhenThereIsNothingStoredInTheDatabase() {
        val commentLiveData = commentDao.getCommentsFromPost(1)

        commentLiveData.observeForever {
            assertEquals(0, it.size)
            assertNotNull(it)
        }
    }

    @Test
    fun shouldReturnSameCommentListForPostWhenThereWasAPreviouslyStoredCommentListForSpecificPost() {
        val comment1 = Comment(
            id = 1,
            postId = 1,
            name = "Name",
            email = "email",
            body = "body"
        )
        val comment2 = Comment(
            id = 2,
            postId = 1,
            name = "Name2",
            email = "email2",
            body = "body2"
        )
        val commentList = listOf(comment1, comment2)

        commentDao.saveComments(commentList).subscribe()
        val commentLiveData = commentDao.getCommentsFromPost(1)

        commentLiveData.observeForever {
            assertEquals(2, it.size)
            assertEquals(comment1, it[0])
            assertEquals(comment2, it[1])
        }
    }

    @Test
    fun shouldReturnSpecificCommentListForPostWhenThereWasAPreviouslyStoredCommentListForManyPosts() {
        val comment1 = Comment(
            id = 1,
            postId = 1,
            name = "Name",
            email = "email",
            body = "body"
        )
        val comment2 = Comment(
            id = 2,
            postId = 2,
            name = "Name2",
            email = "email2",
            body = "body2"
        )
        val comment3 = Comment(
            id = 3,
            postId = 2,
            name = "Name3",
            email = "email3",
            body = "body3"
        )
        val commentList = listOf(comment1, comment2, comment3)

        commentDao.saveComments(commentList).subscribe()
        val commentLiveData = commentDao.getCommentsFromPost(1)

        commentLiveData.observeForever {
            assertEquals(1, it.size)
            assertEquals(comment1, it[0])
        }
    }

    @Test
    fun shouldReplaceSpecificCommentForPostWhenThereWasAStoredCommentWithSameIdForThisPost() {
        val comment1 = Comment(
            id = 1,
            postId = 1,
            name = "Name",
            email = "email",
            body = "body"
        )
        val comment2 = Comment(
            id = 2,
            postId = 1,
            name = "Name2",
            email = "email2",
            body = "bod2"
        )
        val comment3 = Comment(
            id = 3,
            postId = 2,
            name = "Name3",
            email = "email3",
            body = "body3"
        )
        val commentList = listOf(comment1, comment2, comment3)
        commentDao.saveComments(commentList).subscribe()

        val comment1New = Comment(
            id = 1,
            postId = 1,
            name = "NameNew",
            email = "emailNew",
            body = "bodyNew"
        )
        val commentListNew = listOf(comment1New, comment2)
        commentDao.saveComments(commentListNew).subscribe()

        val commentLiveData = commentDao.getCommentsFromPost(1)

        commentLiveData.observeForever {
            assertEquals(2, it.size)
            assertEquals(comment1New, it[0])
            assertEquals(comment2, it[1])
        }
    }
}