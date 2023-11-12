package com.phellipesilva.coolposts.postlist.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.phellipesilva.coolposts.postlist.database.entity.PostEntity
import com.phellipesilva.coolposts.postlist.database.PostDao
import com.phellipesilva.coolposts.postlist.domain.Post
import com.phellipesilva.coolposts.postlist.service.PostService
import com.phellipesilva.coolposts.postlist.service.entity.PostRemoteEntity
import com.phellipesilva.coolposts.postlist.service.entity.UserRemoteEntity
import io.reactivex.Completable
import io.reactivex.functions.BiFunction
import javax.inject.Inject

private typealias RemotePostUserPair = Pair<List<PostRemoteEntity>, List<UserRemoteEntity>>
private typealias RemotePostUserPairListFunction = BiFunction<List<PostRemoteEntity>, List<UserRemoteEntity>, RemotePostUserPair>

class PostListRepository @Inject constructor(
    private val postService: PostService,
    private val postDao: PostDao
) {

    fun getPosts(): LiveData<List<Post>> = postDao.getPosts().map {
        mapPostsDatabaseEntitiesToDomainEntities(it)
    }

    fun updatePosts(): Completable {
        return postService.fetchPosts()
            .zipWith(
                postService.fetchUsers(),
                RemotePostUserPairListFunction { posts, users -> Pair(posts, users) }
            )
            .map(::mapPostAndUserRemoteEntitiesToDatabaseEntities)
            .flatMapCompletable(postDao::savePosts)
    }

    private fun mapPostsDatabaseEntitiesToDomainEntities(databasePosts: List<PostEntity>): List<Post> {
        return databasePosts.map { databaseEntity ->
            Post(
                id = databaseEntity.id,
                body = databaseEntity.body,
                title = databaseEntity.title,
                userId = databaseEntity.userId,
                userName = databaseEntity.userName
            )
        }
    }

    private fun mapPostAndUserRemoteEntitiesToDatabaseEntities(pair: RemotePostUserPair): List<PostEntity> {
        return pair.first.map { postEntity ->
            val user = pair.second.first { userEntity -> postEntity.userId == userEntity.id }

            PostEntity(
                id = postEntity.id,
                title = postEntity.title,
                body = postEntity.body,
                userId = user.id,
                userName = user.name
            )
        }
    }
}