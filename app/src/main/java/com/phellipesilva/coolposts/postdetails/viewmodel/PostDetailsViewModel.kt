package com.phellipesilva.coolposts.postdetails.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.phellipesilva.coolposts.exceptions.NoConnectionException
import com.phellipesilva.coolposts.postdetails.repository.PostDetailsRepository
import com.phellipesilva.coolposts.postdetails.view.PostDetailsViewState
import com.phellipesilva.coolposts.state.ConnectionChecker
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
        viewState.addSource(commentsLiveData) { comments ->
            if (!comments.isNullOrEmpty()) {
                viewState.value = PostDetailsViewState.buildSuccessState(comments)
            }
        }
    }

    fun viewState(): LiveData<PostDetailsViewState> = viewState

    fun updateCommentsFromPost(postId: Int) {
        if (connectionChecker.isOnline()) {
            fetchCommentsFromPost(postId)
        } else {
            viewState.value = PostDetailsViewState.buildErrorState(NoConnectionException())
        }
    }

    private fun fetchCommentsFromPost(postId: Int) {
        postDetailsRepository
            .updateCommentsFromPost(postId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                viewState.value = PostDetailsViewState.buildLoadingState()
            }
            .subscribeBy(
                onError = {
                    Timber.e(it)
                    viewState.value = PostDetailsViewState.buildErrorState(it)
                },
                onSuccess = {
                    viewState.value = PostDetailsViewState.buildSuccessState(it)
                }
            )
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}