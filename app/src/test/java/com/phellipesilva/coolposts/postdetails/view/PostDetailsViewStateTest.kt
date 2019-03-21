package com.phellipesilva.coolposts.postdetails.view

import com.phellipesilva.coolposts.postdetails.data.Comment
import com.phellipesilva.coolposts.state.Event
import com.phellipesilva.coolposts.state.ViewState
import org.junit.Assert.*
import org.junit.Test

class PostDetailsViewStateTest {

    @Test
    fun shouldBuildLoadingPostDetailsViewStateWithNullThrowableAndNullComments() {
        val loadingViewState = PostDetailsViewState.buildLoadingState()

        assertNull(loadingViewState.throwable)
        assertNull(loadingViewState.comments)
        assertEquals(ViewState.LOADING, loadingViewState.viewState)
    }

    @Test
    fun shouldBuildSuccessPostDetailsViewStateWithNullThrowableAndGivenComments() {
        val comments = emptyList<Comment>()
        val successViewState = PostDetailsViewState.buildSuccessState(comments)

        assertNull(successViewState.throwable)
        assertEquals(comments, successViewState.comments)
        assertEquals(ViewState.SUCCESS, successViewState.viewState)
    }

    @Test
    fun shouldBuildErrorPostDetailsViewStateWithGivenThrowableEventAndNullComments() {
        val throwable = Throwable()
        val errorViewState = PostDetailsViewState.buildErrorState(throwable)

        assertEquals(throwable, errorViewState.throwable?.peekContent())
        assertNull(errorViewState.comments)
        assertEquals(ViewState.ERROR, errorViewState.viewState)
    }
}