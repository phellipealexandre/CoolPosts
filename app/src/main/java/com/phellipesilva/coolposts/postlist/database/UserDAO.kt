package com.phellipesilva.coolposts.postlist.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.phellipesilva.coolposts.postlist.entities.UserEntity
import io.reactivex.Completable

@Dao
interface UserDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUsers(users: List<UserEntity>): Completable

    @Query("SELECT * FROM userEntity")
    fun getAllUsers(): LiveData<List<UserEntity>>
}