package com.phellipesilva.coolposts.postdetails.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.phellipesilva.coolposts.postdetails.repository.PostDetailsRepository
import com.phellipesilva.coolposts.state.ConnectionChecker
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class PostDetailsViewModelFactory @Inject constructor(
    private val postDetailsRepository: PostDetailsRepository,
    private val connectionChecker: ConnectionChecker,
    private val postId: Int
    ) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PostDetailsViewModel(postDetailsRepository, connectionChecker, CompositeDisposable(), postId) as T
    }
}