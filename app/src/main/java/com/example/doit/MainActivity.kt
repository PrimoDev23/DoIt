package com.example.doit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.compose.rememberNavController
import com.example.doit.ui.composables.DrawerMenu
import com.example.doit.ui.composables.locals.DrawerStateProvider
import com.example.doit.ui.composables.screens.NavGraphs
import com.example.doit.ui.composables.screens.TagListScreen
import com.example.doit.ui.composables.screens.TodoListScreen
import com.example.doit.ui.composables.screens.destinations.TagListScreenDestination
import com.example.doit.ui.composables.screens.destinations.TodoListScreenDestination
import com.example.doit.ui.theme.DoItTheme
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DoItTheme {
                val scope = rememberCoroutineScope()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val navController = rememberNavController()
                val navHostEngine = rememberAnimatedNavHostEngine(
                    rootDefaultAnimations = RootNavGraphDefaultAnimations(
                        enterTransition = {
                            slideIn(
                                animationSpec = tween(200),
                                initialOffset = {
                                    IntOffset(it.width, 0)
                                }
                            )
                        },
                        exitTransition = {
                            slideOut(
                                animationSpec = tween(200),
                                targetOffset = {
                                    IntOffset(-it.width, 0)
                                }
                            )
                        },
                        popEnterTransition = {
                            slideIn(
                                animationSpec = tween(200),
                                initialOffset = {
                                    IntOffset(-it.width, 0)
                                }
                            )
                        },
                        popExitTransition = {
                            slideOut(
                                animationSpec = tween(200),
                                targetOffset = {
                                    IntOffset(it.width, 0)
                                }
                            )
                        }
                    )
                )

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
                            navController = navController,
                            engine = navHostEngine
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