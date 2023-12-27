package com.example.doit.testing

import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before

@OptIn(ExperimentalCoroutinesApi::class)
abstract class CoroutineTestBase {

    protected val dispatcher = StandardTestDispatcher()

    @Before
    open fun before() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    open fun after() {
        Dispatchers.resetMain()
        unmockkAll()
    }

}