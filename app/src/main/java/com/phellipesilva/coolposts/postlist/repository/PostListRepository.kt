package com.phellipesilva.coolposts.postlist.repository

import androidx.lifecycle.LiveData
import com.phellipesilva.coolposts.postlist.database.PostDao
import com.phellipesilva.coolposts.postlist.data.Post
import com.phellipesilva.coolposts.postlist.data.User
import com.phellipesilva.coolposts.postlist.service.remote.PostRemoteEntity
import com.phellipesilva.coolposts.postlist.service.remote.UserRemoteEntity
import com.phellipesilva.coolposts.postlist.service.PostService
import dagger.Reusable
import io.reactivex.Completable
import io.reactivex.functions.BiFunction
import javax.inject.Inject

typealias PostUserPairList = BiFunction<List<PostRemoteEntity>, List<UserRemoteEntity>, Pair<List<PostRemoteEntity>, List<UserRemoteEntity>>>

@Reusable
class PostListRepository @Inject constructor(
    private val postService: PostService,
    private val postDao: PostDao
) {

    fun updatePosts(): Completable {
        return postService.fetchPosts()
            .zipWith(postService.fetchUsers(),
                PostUserPairList { posts, users ->
                    Pair(
                        posts,
                        users
                    )
                }
            ).flatMapCompletable { (posts, users) ->
                val postList = mapPostEntityListInPostDomainList(posts, users)
                postDao.savePosts(postList)
            }
    }

    fun getPosts(): LiveData<List<Post>> {
        return postDao.getPosts()
    }

    private fun mapPostEntityListInPostDomainList(
        postEntities: List<PostRemoteEntity>,
        userEntities: List<UserRemoteEntity>
    ): List<Post> {
        return postEntities.map { postEntity ->
            Post(
                id = postEntity.id,
                title = postEntity.title,
                body = postEntity.body,
                user = mapUserEntityToUserDomain(userEntities.first { postEntity.userId == it.id })
            )
        }
    }

    private fun mapUserEntityToUserDomain(userEntity: UserRemoteEntity): User {
        return userEntity.run { User(id, name) }
    }
}