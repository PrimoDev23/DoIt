package com.example.doit.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import com.example.doit.R
import com.example.doit.ui.composables.destinations.Destination
import com.example.doit.ui.composables.destinations.TagListScreenDestination
import com.example.doit.ui.composables.destinations.TodoListScreenDestination
import com.ramcosta.composedestinations.navigation.navigate

@Composable
fun DrawerMenuButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        modifier = modifier,
        onClick = onClick
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
                .fillMaxWidth()
                .verticalScroll(state = rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            DrawerMenuItem(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
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
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
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

            Spacer(modifier = Modifier.height(16.dp))
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
    shape: Shape = RoundedCornerShape(8.dp)
) {
    val backgroundModifier = if (selected) {
        Modifier.background(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            shape = shape
        )
    } else {
        Modifier
    }

    Row(
        modifier = Modifier
            .heightIn(min = 56.dp)
            .then(modifier)
            .then(backgroundModifier)
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

        Text(text = title)
    }
}