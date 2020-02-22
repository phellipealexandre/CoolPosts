package com.phellipesilva.coolposts.postlist.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.phellipesilva.coolposts.postlist.database.entity.PostEntity
import io.reactivex.Completable

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun savePosts(postEntities: List<PostEntity>): Completable

    @Query("SELECT * FROM postEntity")
    fun getPosts(): LiveData<List<PostEntity>>
}