package com.example.doit.ui.composables.destinations

import android.os.Bundle
import androidx.annotation.RestrictTo
import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument
import androidx.navigation.navArgument
import com.example.doit.ui.composables.screens.AddEntryScreen
import com.example.doit.ui.navigation.arguments.AddEntryNavArgs
import com.ramcosta.composedestinations.navargs.primitives.DestinationsLongNavType
import com.ramcosta.composedestinations.scope.DestinationScope
import com.ramcosta.composedestinations.spec.Direction

public object AddEntryScreenDestination : TypedDestination<AddEntryNavArgs> {
    
    override fun invoke(navArgs: AddEntryNavArgs): Direction = with(navArgs) {
        invoke(id)
    }
     
    public operator fun invoke(
		id: Long,
    ): Direction {
        return Direction(
            route = "$baseRoute" + 
					"/${DestinationsLongNavType.serializeValue(id)}"
        )
    }
    
    @get:RestrictTo(RestrictTo.Scope.SUBCLASSES)
    override val baseRoute: String = "add_entry_screen"

    override val route: String = "$baseRoute/{id}"
    
	override val arguments: List<NamedNavArgument> get() = listOf(
		navArgument("id") {
			type = DestinationsLongNavType
		}
	)

    @Composable
    override fun DestinationScope<AddEntryNavArgs>.Content() {
		AddEntryScreen(
			navArgs = navArgs, 
			navigator = destinationsNavigator
		)
    }
                    
	override fun argsFrom(bundle: Bundle?): AddEntryNavArgs {
	    return AddEntryNavArgs(
			id = DestinationsLongNavType.safeGet(bundle, "id") ?: throw RuntimeException("'id' argument is mandatory, but was not present!"),
	    )
	}
                
	override fun argsFrom(savedStateHandle: SavedStateHandle): AddEntryNavArgs {
	    return AddEntryNavArgs(
			id = DestinationsLongNavType.get(savedStateHandle, "id") ?: throw RuntimeException("'id' argument is mandatory, but was not present!"),
	    )
	}
}