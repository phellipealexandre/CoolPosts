package com.phellipesilva.coolposts.postdetails.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.phellipesilva.coolposts.postdetails.data.Comment
import io.reactivex.Completable

@Dao
interface CommentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveComments(comments: List<Comment>): Completable

    @Query("SELECT * FROM comment WHERE postId = :postId")
    fun getAllCommentsFromPost(postId: Int): LiveData<List<Comment>>
}