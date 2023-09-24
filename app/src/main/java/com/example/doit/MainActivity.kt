package com.example.doit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.doit.ui.composables.NavGraphs
import com.example.doit.ui.theme.DoItTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DoItTheme {
                DestinationsNavHost(
                    modifier = Modifier.fillMaxSize(),
                    navGraph = NavGraphs.root
                )
            }
        }
    }
}