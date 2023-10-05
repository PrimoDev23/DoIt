package com.example.doit.ui.composables.locals

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

val LocalDrawerState = compositionLocalOf<DrawerState> {
    error("No DrawerState provided")
}

@Composable
fun DrawerStateProvider(
    drawerState: DrawerState,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalDrawerState provides drawerState,
        content = content
    )
}