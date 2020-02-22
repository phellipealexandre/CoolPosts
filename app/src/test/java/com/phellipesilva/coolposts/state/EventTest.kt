package com.phellipesilva.coolposts.state

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class EventTest {

    private lateinit var event: Event<String>

    @Before
    fun `Set Up`() {
        event = Event("Test")
    }

    @Test
    fun `Event should not be handled when content is not requested`() {
        assertFalse(event.hasBeenHandled)
    }

    @Test
    fun `Event should be handled when content is requested`() {
        event.getContentIfNotHandled()

        assertTrue(event.hasBeenHandled)
    }

    @Test
    fun `Should return content when requested`() {
        assertEquals("Test", event.getContentIfNotHandled())
    }

    @Test
    fun `Should return null when requesting content that has been already handled`() {
        event.getContentIfNotHandled()

        assertNull(event.getContentIfNotHandled())
    }
}