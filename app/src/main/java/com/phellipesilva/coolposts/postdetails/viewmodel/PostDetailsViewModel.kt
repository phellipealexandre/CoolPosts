package com.phellipesilva.coolposts.postdetails.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.phellipesilva.coolposts.exceptions.NoConnectionException
import com.phellipesilva.coolposts.postdetails.repository.PostDetailsRepository
import com.phellipesilva.coolposts.postdetails.view.PostDetailsViewState
import com.phellipesilva.coolposts.state.ConnectionChecker
import com.phellipesilva.coolposts.state.Event
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class PostDetailsViewModel(
    private val postDetailsRepository: PostDetailsRepository,
    private val connectionChecker: ConnectionChecker,
    private val compositeDisposable: CompositeDisposable,
    postId: Int
) : ViewModel() {

    private val viewState = MediatorLiveData<PostDetailsViewState>()
    private val commentsLiveData = postDetailsRepository.getCommentsFromPost(postId)

    init {
        viewState.value = PostDetailsViewState(
            isLoading = false,
            comments = emptyList()
        )

        viewState.addSource(commentsLiveData) { comments ->
            viewState.value = viewState.value?.copy(isLoading = false, comments = comments)
        }
    }

    fun viewState(): LiveData<PostDetailsViewState> = viewState

    fun updateCommentsFromPost(postId: Int) {
        if (connectionChecker.isOnline()) {
            fetchCommentsFromPost(postId)
        } else {
            viewState.value = viewState.value?.copy(isLoading = false, errorEvent = Event(NoConnectionException()))
        }
    }

    private fun fetchCommentsFromPost(postId: Int) {
        postDetailsRepository
            .updateCommentsFromPost(postId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError(Timber::e)
            .doOnSubscribe {
                viewState.value = viewState.value?.copy(isLoading = true)
            }
            .subscribeBy(
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