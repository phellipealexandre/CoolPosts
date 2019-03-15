package com.phellipesilva.coolposts.postlist.repository

import androidx.lifecycle.LiveData
import com.phellipesilva.coolposts.postlist.database.PostDAO
import com.phellipesilva.coolposts.postlist.domain.Post
import com.phellipesilva.coolposts.postlist.domain.User
import com.phellipesilva.coolposts.postlist.entities.PostEntity
import com.phellipesilva.coolposts.postlist.entities.UserEntity
import com.phellipesilva.coolposts.postlist.service.PostService
import dagger.Reusable
import io.reactivex.Completable
import io.reactivex.functions.BiFunction
import javax.inject.Inject

typealias PostUserPairList = BiFunction<List<PostEntity>, List<UserEntity>, Pair<List<PostEntity>, List<UserEntity>>>

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
        postEntities: List<PostEntity>,
        userEntities: List<UserEntity>
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

    private fun mapUserEntityToUserDomain(userEntity: UserEntity): User {
        return userEntity.run { User(id, name, website) }
    }
}