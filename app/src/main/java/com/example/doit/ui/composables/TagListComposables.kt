package com.example.doit.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.doit.R
import com.example.doit.ui.viewmodels.TagListViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RootNavGraph
@Destination
@Composable
fun TagListScreen(
    navigator: DestinationsNavigator,
    onMenuClicked: () -> Unit,
    viewModel: TagListViewModel = hiltViewModel()
) {
    RootScaffold(
        modifier = Modifier.fillMaxSize(),
        onMenuClicked = onMenuClicked,
        title = stringResource(id = R.string.tag_overview_title)
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {

        }
    }
}