package com.phellipesilva.coolposts.postlist.view

import com.phellipesilva.coolposts.postlist.data.Post
import com.phellipesilva.coolposts.state.ViewState
import org.junit.Assert.*
import org.junit.Test

class PostListViewStateTest {

    @Test
    fun shouldBuildLoadingPostListViewStateWithNullThrowableAndNullPosts() {
        val loadingViewState = PostListViewState.buildLoadingState()

        assertNull(loadingViewState.throwable)
        assertNull(loadingViewState.posts)
        assertEquals(ViewState.LOADING, loadingViewState.viewState)
    }

    @Test
    fun shouldBuildSuccessPostListViewStateWithNullThrowableAndGivenPosts() {
        val posts = emptyList<Post>()
        val successViewState = PostListViewState.buildSuccessState(posts)

        assertNull(successViewState.throwable)
        assertEquals(posts, successViewState.posts)
        assertEquals(ViewState.SUCCESS, successViewState.viewState)
    }

    @Test
    fun shouldBuildErrorPostListViewStateWithGivenThrowableEventAndNullPosts() {
        val throwable = Throwable()
        val errorViewState = PostListViewState.buildErrorState(throwable)

        assertEquals(throwable, errorViewState.throwable?.peekContent())
        assertNull(errorViewState.posts)
        assertEquals(ViewState.ERROR, errorViewState.viewState)
    }
}
