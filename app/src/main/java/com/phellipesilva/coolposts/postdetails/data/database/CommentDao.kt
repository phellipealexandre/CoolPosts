package com.phellipesilva.coolposts.postdetails.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.phellipesilva.coolposts.postdetails.data.entity.CommentEntity
import io.reactivex.Completable

@Dao
interface CommentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveComments(commentEntities: List<CommentEntity>): Completable

    @Query("SELECT * FROM commentEntity WHERE postId = :postId")
    fun getCommentsFromPost(postId: Int): LiveData<List<CommentEntity>>
}