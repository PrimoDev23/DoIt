package com.example.doit.testing

import io.mockk.unmockkAll
import org.junit.After

abstract class TestBase {

    @After
    open fun after() {
        unmockkAll()
    }

}