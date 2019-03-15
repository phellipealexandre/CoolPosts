package com.phellipesilva.coolposts.postlist.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.phellipesilva.coolposts.postlist.entities.CommentEntity
import io.reactivex.Completable

@Dao
interface CommentDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveComments(comments: List<CommentEntity>): Completable

    @Query("SELECT * FROM commentEntity WHERE postId = :postId")
    fun getAllCommentsFromPost(postId: Int): LiveData<List<CommentEntity>>
}