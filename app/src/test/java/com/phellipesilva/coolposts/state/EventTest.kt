package com.phellipesilva.coolposts.state

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class EventTest {

    private lateinit var event: Event<String>

    @Before
    fun setUp() {
        event = Event("Test")
    }

    @Test
    fun shouldNotHandleEventWhenContentIsNotRequested() {
        assertFalse(event.hasBeenHandled)
    }

    @Test
    fun shouldHandleEventWhenContentIsRequested() {
        event.getContentIfNotHandled()

        assertTrue(event.hasBeenHandled)
    }

    @Test
    fun shouldReturnContentWhenRequested() {
        assertEquals("Test", event.getContentIfNotHandled())
    }

    @Test
    fun shouldReturnNullIfContentHasAlreadyBeenHandled() {
        event.getContentIfNotHandled()

        assertNull(event.getContentIfNotHandled())
    }
}