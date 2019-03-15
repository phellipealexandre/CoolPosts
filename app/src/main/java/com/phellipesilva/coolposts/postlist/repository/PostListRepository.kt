package com.phellipesilva.coolposts.postlist.repository

import androidx.lifecycle.LiveData
import com.phellipesilva.coolposts.postlist.database.PostDAO
import com.phellipesilva.coolposts.postlist.data.Post
import com.phellipesilva.coolposts.postlist.data.User
import com.phellipesilva.coolposts.postlist.entity.PostRemoteEntity
import com.phellipesilva.coolposts.postlist.entity.UserRemoteEntity
import com.phellipesilva.coolposts.postlist.service.PostService
import dagger.Reusable
import io.reactivex.Completable
import io.reactivex.functions.BiFunction
import javax.inject.Inject

typealias PostUserPairList = BiFunction<List<PostRemoteEntity>, List<UserRemoteEntity>, Pair<List<PostRemoteEntity>, List<UserRemoteEntity>>>

@Reusable
class PostListRepository @Inject constructor(
    private val postService: PostService,
    private val postDAO: PostDAO
) {

    fun fetchPosts(): Completable {
        return postService.getPosts()
            .zipWith(postService.getUsers(),
                PostUserPairList { posts, users ->
                    Pair(
                        posts,
                        users
                    )
                }
            ).flatMapCompletable {
                val postList = mapPostEntityListInPostDomainList(it.first, it.second)
                postDAO.savePosts(postList)
            }
    }

    fun getPosts(): LiveData<List<Post>> {
        return postDAO.getAllPosts()
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
        return userEntity.run { User(id, name, website) }
    }
}