package com.phellipesilva.coolposts.postdetails.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.phellipesilva.coolposts.postdetails.repository.PostDetailsRepository
import com.phellipesilva.coolposts.state.ConnectionManager
import dagger.Reusable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@Reusable
class PostDetailsViewModelFactory @Inject constructor(
    private val postDetailsRepository: PostDetailsRepository,
    private val connectionManager: ConnectionManager
    ) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PostDetailsViewModel(postDetailsRepository, connectionManager, CompositeDisposable()) as T
    }
}