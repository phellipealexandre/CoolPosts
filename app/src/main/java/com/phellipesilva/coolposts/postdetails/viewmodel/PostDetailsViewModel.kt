package com.phellipesilva.coolposts.postdetails.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phellipesilva.coolposts.postdetails.repository.PostDetailsRepository
import com.phellipesilva.coolposts.state.ConnectionManager
import com.phellipesilva.coolposts.state.ViewState
import com.phellipesilva.coolposts.state.Event
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class PostDetailsViewModel(
    private val postDetailsRepository: PostDetailsRepository,
    private val connectionManager: ConnectionManager,
    private val compositeDisposable: CompositeDisposable
) : ViewModel() {

    private val viewState = MutableLiveData<Event<ViewState>>()

    fun getCommentsObservable(postId: Int) = postDetailsRepository.getComments(postId)

    fun viewState(): LiveData<Event<ViewState>> = viewState

    fun fetchComments(postId: Int) {
        if (connectionManager.isOnline()) {
            fetchCommentsFromRepository(postId)
        } else {
            viewState.value = Event(ViewState.NO_INTERNET)
        }
    }

    private fun fetchCommentsFromRepository(postId: Int) {
        postDetailsRepository
            .fetchComments(postId)
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