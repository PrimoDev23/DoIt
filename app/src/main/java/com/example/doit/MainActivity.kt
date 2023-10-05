package com.example.doit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.doit.ui.composables.DrawerMenu
import com.example.doit.ui.composables.NavGraphs
import com.example.doit.ui.composables.TagListScreen
import com.example.doit.ui.composables.TodoListScreen
import com.example.doit.ui.composables.destinations.TagListScreenDestination
import com.example.doit.ui.composables.destinations.TodoListScreenDestination
import com.example.doit.ui.composables.locals.DrawerStateProvider
import com.example.doit.ui.theme.DoItTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DoItTheme {
                val scope = rememberCoroutineScope()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val navController = rememberNavController()

                ModalNavigationDrawer(
                    modifier = Modifier.fillMaxSize(),
                    drawerState = drawerState,
                    drawerContent = {
                        DrawerMenu(
                            navController = navController,
                            onDismiss = {
                                scope.launch {
                                    drawerState.close()
                                }
                            }
                        )
                    }
                ) {
                    DrawerStateProvider(drawerState = drawerState) {
                        DestinationsNavHost(
                            modifier = Modifier.fillMaxSize(),
                            navGraph = NavGraphs.root,
                            navController = navController
                        ) {
                            composable(TodoListScreenDestination) {
                                TodoListScreen(navigator = destinationsNavigator)
                            }

                            composable(TagListScreenDestination) {
                                TagListScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}