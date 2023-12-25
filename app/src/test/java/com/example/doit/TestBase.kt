package com.example.doit

import io.mockk.unmockkAll
import org.junit.After

abstract class TestBase {

    @After
    open fun after() {
        unmockkAll()
    }

}