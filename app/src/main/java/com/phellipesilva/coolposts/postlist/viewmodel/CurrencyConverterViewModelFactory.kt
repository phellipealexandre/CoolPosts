package com.phellipesilva.coolposts.postlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.phellipesilva.coolposts.postlist.repository.PostListRepository
import io.reactivex.disposables.CompositeDisposable

class PostListViewModelFactory(
    private val postListRepository: PostListRepository,
    private val compositeDisposable: CompositeDisposable
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PostListViewModel(postListRepository, compositeDisposable) as T
    }
}