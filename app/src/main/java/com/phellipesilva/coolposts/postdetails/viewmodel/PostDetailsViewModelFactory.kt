package com.phellipesilva.coolposts.postdetails.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.phellipesilva.coolposts.postdetails.repository.PostDetailsRepository
import com.phellipesilva.coolposts.state.ConnectionChecker
import dagger.Reusable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@Reusable
class PostDetailsViewModelFactory @Inject constructor(
    private val postDetailsRepository: PostDetailsRepository,
    private val connectionChecker: ConnectionChecker
    ) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PostDetailsViewModel(postDetailsRepository, connectionChecker, CompositeDisposable()) as T
    }
}