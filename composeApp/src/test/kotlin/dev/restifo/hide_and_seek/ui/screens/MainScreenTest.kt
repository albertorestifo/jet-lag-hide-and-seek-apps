package dev.restifo.hide_and_seek.ui.screens

import org.junit.Test
import kotlin.test.assertEquals

class MainScreenTest {
    @Test
    fun testGameCodeValidation() {
        // This is a simple test to verify our test setup
        val validCode = "ABC123"
        assertEquals(6, validCode.length)
    }
}
