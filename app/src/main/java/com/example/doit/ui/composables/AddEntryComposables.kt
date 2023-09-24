package com.example.doit.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.doit.ui.viewmodels.AddEntryViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RootNavGraph
@Destination
@Composable
fun AddEntryScreen(
    navigator: DestinationsNavigator,
    viewModel: AddEntryViewModel = hiltViewModel()
) {
    Scaffold(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {

        }
    }
}