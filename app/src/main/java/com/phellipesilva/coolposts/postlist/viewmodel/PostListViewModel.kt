package com.phellipesilva.coolposts.postlist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phellipesilva.coolposts.postlist.repository.PostListRepository
import com.phellipesilva.coolposts.state.ConnectionChecker
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
    private val connectionChecker: ConnectionChecker,
    private val compositeDisposable: CompositeDisposable
) : ViewModel() {

    private val postsLiveData = postListRepository.getPosts()
    private val viewState = MutableLiveData<Event<ViewState>>()

    fun getPosts() = this.postsLiveData

    fun viewState(): LiveData<Event<ViewState>> = viewState

    fun updatePosts() {
        if (connectionChecker.isOnline()) {
            updatePostsFromServer()
        } else {
            viewState.value = Event(ViewState.NO_INTERNET)
        }
    }

    private fun updatePostsFromServer() {
        postListRepository
            .updatePosts()
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