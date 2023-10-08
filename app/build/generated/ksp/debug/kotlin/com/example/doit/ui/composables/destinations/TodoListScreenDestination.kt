package com.example.doit.ui.composables.destinations

import androidx.annotation.RestrictTo
import androidx.compose.runtime.Composable
import com.example.doit.ui.composables.screens.TodoListScreen
import com.ramcosta.composedestinations.scope.DestinationScope
import com.ramcosta.composedestinations.spec.Direction

public object TodoListScreenDestination : DirectionDestination {
         
    public operator fun invoke(): Direction = this
    
    @get:RestrictTo(RestrictTo.Scope.SUBCLASSES)
    override val baseRoute: String = "todo_list_screen"

    override val route: String = baseRoute
    
    @Composable
    override fun DestinationScope<Unit>.Content() {
		TodoListScreen(
			navigator = destinationsNavigator
		)
    }
    
}