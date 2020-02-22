package com.phellipesilva.coolposts.postlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.phellipesilva.coolposts.postlist.repository.PostListRepository
import com.phellipesilva.coolposts.state.ConnectionChecker
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class PostListViewModelFactory @Inject constructor(
    private val postListRepository: PostListRepository,
    private val connectionChecker: ConnectionChecker
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PostListViewModel(postListRepository, connectionChecker, CompositeDisposable()) as T
    }
}