package com.phellipesilva.coolposts.postlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.phellipesilva.coolposts.postlist.repository.PostListRepository
import com.phellipesilva.coolposts.state.ConnectionManager
import dagger.Reusable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@Reusable
class PostListViewModelFactory @Inject constructor(
    private val postListRepository: PostListRepository,
    private val connectionManager: ConnectionManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PostListViewModel(postListRepository, connectionManager, CompositeDisposable()) as T
    }
}