package com.phellipesilva.coolposts.postlist.repository

import androidx.lifecycle.LiveData
import com.phellipesilva.coolposts.postlist.data.Post
import com.phellipesilva.coolposts.postlist.database.PostDao
import com.phellipesilva.coolposts.postlist.service.PostService
import com.phellipesilva.coolposts.postlist.service.remote.PostRemoteEntity
import com.phellipesilva.coolposts.postlist.service.remote.UserRemoteEntity
import dagger.Reusable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import javax.inject.Inject

typealias PostUserPairList = BiFunction<List<PostRemoteEntity>, List<UserRemoteEntity>, Pair<List<PostRemoteEntity>, List<UserRemoteEntity>>>

@Reusable
class PostListRepository @Inject constructor(
    private val postService: PostService,
    private val postDao: PostDao
) {

    fun updatePosts(): Single<List<Post>> {
        return postService.fetchPosts()
            .zipWith(postService.fetchUsers(),
                PostUserPairList { posts, users ->
                    Pair(
                        posts,
                        users
                    )
                }
            ).map { (posts, users) ->
                val postList = mapPostEntityListInPostDomainList(posts, users)
                postDao.savePosts(postList)
                postList
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
            val user = userEntities.first { postEntity.userId == it.id }

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