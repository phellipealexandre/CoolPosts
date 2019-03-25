package com.phellipesilva.coolposts.postlist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.phellipesilva.coolposts.exceptions.NoConnectionException
import com.phellipesilva.coolposts.postlist.repository.PostListRepository
import com.phellipesilva.coolposts.postlist.view.PostListViewState
import com.phellipesilva.coolposts.state.ConnectionChecker
import com.phellipesilva.coolposts.state.Event
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
        viewState.value = PostListViewState()

        viewState.addSource(postsLiveData) { posts ->
            viewState.value = viewState.value?.copy(posts = posts)
        }
    }

    fun viewState(): LiveData<PostListViewState> = viewState

    fun updatePosts() {
        if (connectionChecker.isOnline()) {
            updatePostsFromServer()
        } else {
            viewState.value = viewState.value?.copy(errorEvent = Event(NoConnectionException()))
        }
    }

    private fun updatePostsFromServer() {
        postListRepository
            .updatePosts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError(Timber::e)
            .doOnSubscribe {
                viewState.value = viewState.value?.copy(isLoading = true)
            }
            .subscribeBy(
                onComplete = {
                    viewState.value = viewState.value?.copy(isLoading = false)
                },
                onError = {
                    viewState.value = viewState.value?.copy(isLoading = false, errorEvent = Event(it))
                }
            )
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}