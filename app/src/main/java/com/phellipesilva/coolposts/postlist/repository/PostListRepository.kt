package com.phellipesilva.coolposts.postlist.repository

import androidx.lifecycle.LiveData
import com.phellipesilva.coolposts.postlist.data.Post
import com.phellipesilva.coolposts.postlist.database.PostDao
import com.phellipesilva.coolposts.postlist.service.PostService
import com.phellipesilva.coolposts.postlist.service.remote.PostRemoteEntity
import com.phellipesilva.coolposts.postlist.service.remote.UserRemoteEntity
import dagger.Reusable
import io.reactivex.Completable
import io.reactivex.functions.BiFunction
import javax.inject.Inject

private typealias PostUserPairListFunction = BiFunction<List<PostRemoteEntity>, List<UserRemoteEntity>, Pair<List<PostRemoteEntity>, List<UserRemoteEntity>>>
private typealias PostUserPair = Pair<List<PostRemoteEntity>, List<UserRemoteEntity>>

@Reusable
class PostListRepository @Inject constructor(
    private val postService: PostService,
    private val postDao: PostDao
) {

    fun getPosts(): LiveData<List<Post>> {
        return postDao.getPosts()
    }

    fun updatePosts(): Completable {
        return postService.fetchPosts()
            .zipWith(
                postService.fetchUsers(),
                PostUserPairListFunction { posts, users ->
                    Pair(
                        posts,
                        users
                    )
                }
            )
            .map(::mapPostAndUserEntitiesInPostDomainList)
            .flatMapCompletable(postDao::savePosts)
    }

    private fun mapPostAndUserEntitiesInPostDomainList(pair: PostUserPair): List<Post> {
        return pair.first.map { postEntity ->
            val user = pair.second.first { userEntity -> postEntity.userId == userEntity.id }

            Post(
                id = postEntity.id,
                title = postEntity.title,
                body = postEntity.body,
                userId = user.id,
                userName = user.name
            )
        }
    }
}