package com.example.doit.ui.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import com.example.doit.R
import com.example.doit.ui.composables.locals.LocalDrawerState
import com.example.doit.ui.composables.screens.NavGraphs
import com.example.doit.ui.composables.screens.appCurrentDestinationAsState
import com.example.doit.ui.composables.screens.destinations.Destination
import com.example.doit.ui.composables.screens.destinations.InfoScreenDestination
import com.example.doit.ui.composables.screens.destinations.TagListScreenDestination
import com.example.doit.ui.composables.screens.destinations.TodoListScreenDestination
import com.example.doit.ui.composables.screens.startAppDestination
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.launch

@Composable
fun DrawerMenuButton(
    modifier: Modifier = Modifier
) {
    val drawerState = LocalDrawerState.current
    val scope = rememberCoroutineScope()

    IconButton(
        modifier = modifier,
        onClick = {
            scope.launch {
                drawerState.open()
            }
        }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.outline_menu_24),
            contentDescription = stringResource(id = R.string.drawer_menu_open)
        )
    }
}

@Composable
fun DrawerMenu(
    navController: NavController,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentDestination: Destination = navController.appCurrentDestinationAsState().value
        ?: NavGraphs.root.startAppDestination

    ModalDrawerSheet(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    vertical = 4.dp,
                    horizontal = 12.dp
                )
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            DrawerMenuItem(
                modifier = Modifier.fillMaxWidth(),
                icon = painterResource(id = R.drawable.outline_checklist_rtl_24),
                title = stringResource(id = R.string.todo_list_title),
                selected = currentDestination == TodoListScreenDestination,
                onClick = {
                    navController.navigate(TodoListScreenDestination) {
                        buildNavigationOptions(navController)
                    }
                    onDismiss()
                }
            )

            DrawerMenuItem(
                modifier = Modifier.fillMaxWidth(),
                icon = painterResource(id = R.drawable.outline_label_24),
                title = stringResource(id = R.string.tag_overview_title),
                selected = currentDestination == TagListScreenDestination,
                onClick = {
                    navController.navigate(TagListScreenDestination) {
                        buildNavigationOptions(navController)
                    }
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Divider(modifier = Modifier.padding(vertical = 4.dp))

            DrawerMenuItem(
                modifier = Modifier.fillMaxWidth(),
                icon = painterResource(id = R.drawable.outline_info_24),
                title = stringResource(id = R.string.info_screen_title),
                selected = currentDestination == InfoScreenDestination,
                onClick = {
                    navController.navigate(InfoScreenDestination) {
                        buildNavigationOptions(navController)
                    }
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(0.dp))
        }
    }
}

private fun NavOptionsBuilder.buildNavigationOptions(navController: NavController) {
    // Pop up to the start destination of the graph to
    // avoid building up a large stack of destinations
    // on the back stack as users select items
    popUpTo(navController.graph.startDestinationId) {
        saveState = true
    }
    // Avoid multiple copies of the same destination when
    // reselecting the same item
    launchSingleTop = true
    // Restore state when reselecting a previously selected item
    restoreState = true
}

@Composable
fun DrawerMenuItem(
    icon: Painter,
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        } else {
            Color.Transparent
        },
        label = "DrawerMenuItemBackgroundAnimation"
    )

    Row(
        modifier = Modifier
            .height(56.dp)
            .then(modifier)
            .background(
                color = backgroundColor,
                shape = shape
            )
            .clip(shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = onClick
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = icon,
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge
        )
    }
}