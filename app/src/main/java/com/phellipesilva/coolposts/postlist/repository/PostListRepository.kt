package com.phellipesilva.coolposts.postlist.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.phellipesilva.coolposts.postlist.database.PostDAO
import com.phellipesilva.coolposts.postlist.database.UserDAO
import com.phellipesilva.coolposts.postlist.domain.Post
import com.phellipesilva.coolposts.postlist.domain.User
import com.phellipesilva.coolposts.postlist.entities.PostEntity
import com.phellipesilva.coolposts.postlist.entities.UserEntity
import com.phellipesilva.coolposts.postlist.service.PostService
import io.reactivex.Completable
import io.reactivex.functions.BiFunction

class PostListRepository(
    private val postService: PostService,
    private val postDAO: PostDAO,
    private val userDAO: UserDAO
) {

    fun fetchPosts(): Completable {
        return postService
            .getPosts()
            .zipWith(postService.getUsers(),
                BiFunction<List<PostEntity>, List<UserEntity>, Pair<List<PostEntity>, List<UserEntity>>> { posts, users ->
                    Pair(
                        posts,
                        users
                    )
                }
            ).flatMapCompletable {
                postDAO.savePosts(it.first)
                userDAO.saveUsers(it.second)
            }
    }

    fun getPosts(): LiveData<List<Post>> {
        return Transformations.switchMap(
            postDAO.getAllPosts(),
            ::mapPostEntitiesInPostDomainListLiveData
        )
    }

    private fun mapPostEntitiesInPostDomainListLiveData(postEntities: List<PostEntity>): LiveData<List<Post>> {
        return Transformations.map(userDAO.getAllUsers()) { userEntities ->
            postEntities.map { postEntity ->
                Post(
                    id = postEntity.id,
                    title = postEntity.title,
                    body = postEntity.body,
                    user = mapUserEntityToUserDomain(userEntities.first { postEntity.userId == it.id })
                )
            }
        }
    }

    private fun mapUserEntityToUserDomain(userEntity: UserEntity): User {
        return userEntity.run { User(id, name, website) }
    }
}