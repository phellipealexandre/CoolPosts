package com.phellipesilva.coolposts.postlist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phellipesilva.coolposts.postlist.repository.PostListRepository
import com.phellipesilva.coolposts.state.ConnectionManager
import com.phellipesilva.coolposts.state.Event
import com.phellipesilva.coolposts.state.ViewState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class PostListViewModel(
    private val postListRepository: PostListRepository,
    private val connectionManager: ConnectionManager,
    private val compositeDisposable: CompositeDisposable
) : ViewModel() {

    private val postsLiveData by lazy { postListRepository.getPosts() }
    private val viewState = MutableLiveData<Event<ViewState>>()

    fun getPostsObservable() = this.postsLiveData

    fun viewState(): LiveData<Event<ViewState>> = viewState

    fun fetchPosts() {
        if (connectionManager.isOnline()) {
            fetchPostsFromRepository()
        } else {
            viewState.value = Event(ViewState.NO_INTERNET)
        }
    }

    private fun fetchPostsFromRepository() {
        postListRepository
            .fetchPosts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = {
                    Timber.e(it)
                    viewState.value = Event(ViewState.UNEXPECTED_ERROR)
                },
                onComplete = { viewState.value = Event(ViewState.SUCCESS) }
            )
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}