package com.phellipesilva.coolposts.postlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.phellipesilva.coolposts.postlist.repository.PostListRepository
import dagger.Reusable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@Reusable
class PostListViewModelFactory @Inject constructor(
    private val postListRepository: PostListRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PostListViewModel(postListRepository, CompositeDisposable()) as T
    }
}