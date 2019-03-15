package com.phellipesilva.coolposts.postlist.viewmodel.state

/**
 * @author: Phellipe Silva
 * Class intentionally copied and adapted from:
 * https://medium.com/androiddevelopers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150
 */
class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    fun peekContent(): T = content
}