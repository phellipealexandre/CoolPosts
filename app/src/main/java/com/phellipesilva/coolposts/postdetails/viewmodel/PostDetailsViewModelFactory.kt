package com.phellipesilva.coolposts.postdetails.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.phellipesilva.coolposts.postdetails.repository.PostDetailsRepository
import dagger.Reusable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@Reusable
class PostDetailsViewModelFactory @Inject constructor(
    private val postDetailsRepository: PostDetailsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PostDetailsViewModel(postDetailsRepository, CompositeDisposable()) as T
    }
}