package com.phellipesilva.coolposts.postlist.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.phellipesilva.coolposts.postlist.entities.PostEntity
import io.reactivex.Completable

@Dao
interface PostDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun savePosts(posts: List<PostEntity>): Completable

    @Query("SELECT * FROM postEntity")
    fun getAllPosts(): LiveData<List<PostEntity>>
}