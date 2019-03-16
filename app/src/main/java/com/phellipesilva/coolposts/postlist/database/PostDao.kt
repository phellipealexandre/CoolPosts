package com.phellipesilva.coolposts.postlist.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.phellipesilva.coolposts.postlist.data.Post
import io.reactivex.Completable

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun savePosts(posts: List<Post>): Completable

    @Query("SELECT * FROM post")
    fun getAllPosts(): LiveData<List<Post>>
}