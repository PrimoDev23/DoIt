package com.example.doit.ui.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootScaffold(
    onMenuClicked: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    DrawerMenuButton(onClick = onMenuClicked)
                }
            )
        },
        floatingActionButton = floatingActionButton,
        content = content
    )
}