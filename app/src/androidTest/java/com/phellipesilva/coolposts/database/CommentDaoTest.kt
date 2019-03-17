package com.phellipesilva.coolposts.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
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
        val context = InstrumentationRegistry.getInstrumentation().context
        database = Room.inMemoryDatabaseBuilder(context, MainDatabase::class.java).build()
        commentDao = database.getCommentDao()
    }

    @Test
    fun shouldReturnEmptyCommentListWhenThereIsNothingStoredInTheDatabase() {
        val commentLiveData = commentDao.getAllCommentsFromPost(1)

        commentLiveData.observeForever {
            assertEquals(0, it.size)
            assertNotNull(it)
        }
    }

    @Test
    fun shouldReturnSameCommentListForPostWhenThereWasAPreviouslyStoredCommentListForSpecificPost() {
        val comment1 = Comment(
            1,
            1,
            "Name",
            "email",
            "body"
        )
        val comment2 = Comment(
            1,
            2,
            "Name2",
            "email2",
            "body2"
        )
        val commentList = listOf(comment1, comment2)

        commentDao.saveComments(commentList).subscribe()
        val commentLiveData = commentDao.getAllCommentsFromPost(1)

        commentLiveData.observeForever {
            assertEquals(2, it.size)
            assertEquals(comment1, it[0])
            assertEquals(comment2, it[1])
        }
    }

    @Test
    fun shouldReturnSpecificCommentListForPostWhenThereWasAPreviouslyStoredCommentListForManyPosts() {
        val comment1 = Comment(
            1,
            1,
            "Name",
            "email",
            "body"
        )
        val comment2 = Comment(
            2,
            2,
            "Name2",
            "email2",
            "body2"
        )
        val comment3 = Comment(
            2,
            3,
            "Name3",
            "email3",
            "body3"
        )
        val commentList = listOf(comment1, comment2, comment3)

        commentDao.saveComments(commentList).subscribe()
        val commentLiveData = commentDao.getAllCommentsFromPost(1)

        commentLiveData.observeForever {
            assertEquals(1, it.size)
            assertEquals(comment1, it[0])
        }
    }

    @Test
    fun shouldReplaceSpecificCommentForPostWhenThereWasAStoredCommentWithSameIdForThisPost() {
        val comment1 = Comment(
            1,
            1,
            "Name",
            "email",
            "body"
        )
        val comment2 = Comment(
            1,
            2,
            "Name2",
            "email2",
            "bod2"
        )
        val comment3 = Comment(
            2,
            3,
            "Name3",
            "email3",
            "body3"
        )
        val commentList = listOf(comment1, comment2, comment3)
        commentDao.saveComments(commentList).subscribe()

        val comment1New = Comment(
            1,
            1,
            "NameNew",
            "emailNew",
            "bodyNew"
        )
        val commentListNew = listOf(comment1New, comment2)
        commentDao.saveComments(commentListNew).subscribe()

        val commentLiveData = commentDao.getAllCommentsFromPost(1)

        commentLiveData.observeForever {
            assertEquals(2, it.size)
            assertEquals(comment1New, it[0])
            assertEquals(comment2, it[1])
        }
    }
}