package com.example.doit.ui.composables

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester

@Composable
fun rememberFocusRequester(): FocusRequester {
    return remember {
        FocusRequester()
    }
}

@Composable
fun rememberSnackbarHostState(): SnackbarHostState {
    return remember {
        SnackbarHostState()
    }
}