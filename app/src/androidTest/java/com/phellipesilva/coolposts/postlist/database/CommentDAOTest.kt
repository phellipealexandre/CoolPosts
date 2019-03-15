package com.phellipesilva.coolposts.postlist.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.phellipesilva.coolposts.postlist.entities.CommentEntity
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CommentDAOTest {

    private lateinit var commentDAO: CommentDAO
    private lateinit var database: MainDatabase

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().context
        database = Room.inMemoryDatabaseBuilder(context, MainDatabase::class.java).build()
        commentDAO = database.getCommentDAO()
    }

    @Test
    fun shouldReturnEmptyCommentListWhenThereIsNothingStoredInTheDatabase() {
        val commentLiveData = commentDAO.getAllCommentsFromPost(1)

        commentLiveData.observeForever {
            Assert.assertEquals(0, it.size)
        }
    }

    @Test
    fun shouldReturnSameCommentListForPostWhenThereWasAPreviouslyStoredCommentListForSpecificPost() {
        val comment1 = CommentEntity(1, 1, "Name", "email", "body")
        val comment2 = CommentEntity(1, 2, "Name2", "email2", "body2")
        val commentList = listOf(comment1, comment2)

        commentDAO.saveComments(commentList).subscribe()
        val commentLiveData = commentDAO.getAllCommentsFromPost(1)

        commentLiveData.observeForever {
            Assert.assertEquals(2, it.size)
            Assert.assertEquals(comment1, it[0])
            Assert.assertEquals(comment2, it[1])
        }
    }

    @Test
    fun shouldReturnSpecificCommentListForPostWhenThereWasAPreviouslyStoredCommentListForManyPosts() {
        val comment1 = CommentEntity(1, 1, "Name", "email", "body")
        val comment2 = CommentEntity(2, 2, "Name2", "email2", "body2")
        val comment3 = CommentEntity(2, 3, "Name3", "email3", "body3")
        val commentList = listOf(comment1, comment2, comment3)

        commentDAO.saveComments(commentList).subscribe()
        val commentLiveData = commentDAO.getAllCommentsFromPost(1)

        commentLiveData.observeForever {
            Assert.assertEquals(1, it.size)
            Assert.assertEquals(comment1, it[0])
        }
    }

    @Test
    fun shouldReplaceSpecificCommentForPostWhenThereWasAStoredCommentWithSameIdForThisPost() {
        val comment1 = CommentEntity(1, 1, "Name", "email", "body")
        val comment2 = CommentEntity(1, 2, "Name2", "email2", "bod2")
        val comment3 = CommentEntity(2, 3, "Name3", "email3", "body3")
        val commentList = listOf(comment1, comment2, comment3)
        commentDAO.saveComments(commentList).subscribe()

        val comment1New = CommentEntity(1, 1, "NameNew", "emailNew", "bodyNew")
        val commentListNew = listOf(comment1New, comment2)
        commentDAO.saveComments(commentListNew).subscribe()

        val commentLiveData = commentDAO.getAllCommentsFromPost(1)

        commentLiveData.observeForever {
            Assert.assertEquals(2, it.size)
            Assert.assertEquals(comment1New, it[0])
            Assert.assertEquals(comment2, it[1])
        }
    }
}