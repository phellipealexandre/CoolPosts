package com.phellipesilva.coolposts.postlist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.phellipesilva.coolposts.exceptions.NoConnectionException
import com.phellipesilva.coolposts.postlist.repository.PostListRepository
import com.phellipesilva.coolposts.postlist.view.PostListViewState
import com.phellipesilva.coolposts.state.ConnectionChecker
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class PostListViewModel(
    private val postListRepository: PostListRepository,
    private val connectionChecker: ConnectionChecker,
    private val compositeDisposable: CompositeDisposable
) : ViewModel() {

    private val viewState = MediatorLiveData<PostListViewState>()
    private val postsLiveData = postListRepository.getPosts()

    init {
        viewState.addSource(postsLiveData) { posts ->
            if (!posts.isNullOrEmpty()) {
                viewState.value = PostListViewState.buildSuccessState(posts)
            }
        }
    }

    fun viewState(): LiveData<PostListViewState> = viewState

    fun updatePosts() {
        if (connectionChecker.isOnline()) {
            updatePostsFromServer()
        } else {
            viewState.value = PostListViewState.buildErrorState(NoConnectionException())
        }
    }

    private fun updatePostsFromServer() {
        postListRepository
            .updatePosts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError(Timber::e)
            .doOnSubscribe {
                viewState.value = PostListViewState.buildLoadingState()
            }
            .subscribeBy(
                onError = { viewState.value = PostListViewState.buildErrorState(it) }
            )
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}