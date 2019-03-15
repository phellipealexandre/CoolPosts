package com.phellipesilva.coolposts.postlist.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.phellipesilva.coolposts.postlist.entities.UserEntity
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDAOTest {

    private lateinit var userDAO: UserDAO
    private lateinit var database: MainDatabase

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().context
        database = Room.inMemoryDatabaseBuilder(context, MainDatabase::class.java).build()
        userDAO = database.getUserDAO()
    }

    @Test
    fun shouldReturnEmptyUserListWhenThereIsNothingStoredInTheDatabase() {
        val userLiveData = userDAO.getAllUsers()

        userLiveData.observeForever {
            Assert.assertEquals(0, it.size)
        }
    }

    @Test
    fun shouldReturnSameUserListValueWhenThereWasAPreviouslyStoredUserList() {
        val user1 = UserEntity(1, "Name", "Website")
        val user2 = UserEntity(2, "Name2", "Website2")
        val userList = listOf(user1, user2)

        userDAO.saveUsers(userList).subscribe()
        val userLiveData = userDAO.getAllUsers()

        userLiveData.observeForever {
            Assert.assertEquals(2, it.size)
            Assert.assertEquals(user1, it[0])
            Assert.assertEquals(user2, it[1])
        }
    }

    @Test
    fun shouldReplacePostsWithSameIdWhenThereIsConflictInStoredPostList() {
        val user1 = UserEntity(1, "Name", "Website")
        val user2 = UserEntity(2, "Name2", "Website2")
        val userList = listOf(user1, user2)

        userDAO.saveUsers(userList).subscribe()

        val user1New = UserEntity(1, "NameNew", "WebsiteNew")
        val newUserList = listOf(user1New, user2)

        userDAO.saveUsers(newUserList).subscribe()
        val userLiveData = userDAO.getAllUsers()

        userLiveData.observeForever {
            Assert.assertEquals(2, it.size)
            Assert.assertEquals(user1New, it[0])
            Assert.assertEquals(user2, it[1])
        }
    }
}