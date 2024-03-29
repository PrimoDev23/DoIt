package com.example.doit.ui.composables.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.doit.common.R
import com.example.doit.domain.models.Priority
import com.example.doit.domain.models.Tag
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.models.TodoItemSortType
import com.example.doit.ui.composables.ClearSelectionButton
import com.example.doit.ui.composables.DeleteToolbarItem
import com.example.doit.ui.composables.DoItCheckbox
import com.example.doit.ui.composables.DrawerMenuButton
import com.example.doit.ui.composables.EditToolbarItem
import com.example.doit.ui.composables.FilterToolbarItem
import com.example.doit.ui.composables.RootScaffold
import com.example.doit.ui.composables.StrikethroughText
import com.example.doit.ui.composables.TodoItemsFilterBottomSheet
import com.example.doit.ui.composables.rememberSnackbarHostState
import com.example.doit.ui.composables.screens.destinations.AddEntryScreenDestination
import com.example.doit.ui.composables.screens.destinations.TodoDetailScreenDestination
import com.example.doit.ui.viewmodels.TodoListState
import com.example.doit.ui.viewmodels.TodoListViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import java.util.UUID

@RootNavGraph(start = true)
@Destination
@Composable
fun TodoListScreen(
    navigator: DestinationsNavigator,
    viewModel: TodoListViewModel = getViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    TodoListScreenContent(
        modifier = Modifier.fillMaxSize(),
        state = state,
        onEditClicked = {
            val id = state.selectedItems.first().id

            navigator.navigate(
                AddEntryScreenDestination(
                    id = id,
                    edit = true
                )
            )
            viewModel.onEditClicked()
        },
        onDeleteClicked = viewModel::onDeleteClicked,
        onAddEntryClicked = {
            navigator.navigate(
                AddEntryScreenDestination(
                    id = UUID.randomUUID().toString(),
                    edit = false
                )
            )
        },
        onClearSelectionClicked = viewModel::onClearSelectionClicked,
        onTodayInfoCardClicked = viewModel::onTodayInfoCardClicked,
        onDoneChanged = viewModel::onDoneChanged,
        onItemClicked = { item ->
            if (state.selectedItems.isNotEmpty()) {
                viewModel.onItemSelected(item)
            } else {
                navigator.navigate(TodoDetailScreenDestination(id = item.id))
            }
        },
        onItemSelected = viewModel::onItemSelected,
        onSortTypeChanged = viewModel::onSortTypeChanged,
        onHideDoneItemsChanged = viewModel::onHideDoneItemsChanged,
        onTagClicked = viewModel::onTagClicked,
        onResetTagsClicked = viewModel::onResetTagsClicked,
        onPriorityClicked = viewModel::onPriorityClicked,
        onResetPrioritiesClicked = viewModel::onResetPrioritiesClicked
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TodoListScreenContent(
    state: TodoListState,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onAddEntryClicked: () -> Unit,
    onClearSelectionClicked: () -> Unit,
    onTodayInfoCardClicked: (Boolean) -> Unit,
    onDoneChanged: (TodoItem, Boolean) -> Unit,
    onItemClicked: (TodoItem) -> Unit,
    onItemSelected: (TodoItem) -> Unit,
    onSortTypeChanged: (TodoItemSortType) -> Unit,
    onHideDoneItemsChanged: (Boolean) -> Unit,
    onTagClicked: (Tag) -> Unit,
    onResetTagsClicked: () -> Unit,
    onPriorityClicked: (Priority) -> Unit,
    onResetPrioritiesClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasItemsSelected = remember(state.selectedItems) {
        state.selectedItems.isNotEmpty()
    }

    val scope = rememberCoroutineScope()
    val snackbarHostState = rememberSnackbarHostState()

    var filterSheetVisible by remember {
        mutableStateOf(false)
    }
    val filterSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    RootScaffold(
        modifier = modifier,
        title = stringResource(id = R.string.todo_list_title),
        actions = {
            val hasOneItemSelected = remember(state.selectedItems.size) {
                state.selectedItems.size == 1
            }

            TodoListToolbarActions(
                hasItemsSelected = hasItemsSelected,
                hasOneItemSelected = hasOneItemSelected,
                onEditClicked = onEditClicked,
                onDeleteClicked = onDeleteClicked,
                onFilterClicked = {
                    filterSheetVisible = true
                }
            )
        },
        floatingActionButton = {
            TodoListFloatingActionButton(onClick = onAddEntryClicked)
        },
        navigationIcon = {
            AnimatedContent(
                targetState = hasItemsSelected,
                label = "ShowClearIconAnimation"
            ) { showClearSelection ->
                if (showClearSelection) {
                    ClearSelectionButton(onClick = onClearSelectionClicked)
                } else {
                    DrawerMenuButton()
                }
            }
        },
        snackbarHostState = snackbarHostState
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TodayInfoCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                filterActive = state.todayFilterActive,
                undone = state.todayUndone,
                done = state.todayDone,
                onClick = onTodayInfoCardClicked
            )

            val hasItems = remember(state.items) {
                state.items.isNotEmpty()
            }

            AnimatedContent(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                targetState = hasItems,
                label = "TodoListAnimation"
            ) { innerHasItems ->
                if (innerHasItems) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 88.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            items = state.items,
                            key = { item ->
                                item.id
                            }
                        ) { item ->
                            val selected = remember(state.selectedItems, item) {
                                state.selectedItems.contains(item)
                            }

                            TodoItemListEntry(
                                modifier = Modifier
                                    .animateItemPlacement()
                                    .fillMaxWidth(),
                                selected = selected,
                                priority = item.priority,
                                done = item.done,
                                onDoneChanged = { done ->
                                    onDoneChanged(item, done)
                                },
                                title = item.title,
                                description = item.description,
                                onClick = {
                                    onItemClicked(item)
                                },
                                onLongClick = {
                                    onItemSelected(item)
                                }
                            )
                        }
                    }
                } else {
                    TodoListEmptyState(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }

    if (filterSheetVisible) {
        TodoItemsFilterBottomSheet(
            sheetState = filterSheetState,
            onDismiss = {
                scope.launch {
                    filterSheetState.hide()
                }.invokeOnCompletion {
                    filterSheetVisible = false
                }
            },
            selectedSortType = state.sortType,
            onSortTypeClicked = onSortTypeChanged,
            hideDoneItems = state.hideDoneItems,
            onHideDoneItemsChanged = onHideDoneItemsChanged,
            tags = state.tags,
            selectedTags = state.selectedTags,
            onTagClicked = onTagClicked,
            onResetTagsClicked = onResetTagsClicked,
            selectedPriorities = state.selectedPriorities,
            onPriorityClicked = onPriorityClicked,
            onResetPrioritiesClicked = onResetPrioritiesClicked
        )
    }
}

@Composable
fun TodoListToolbarActions(
    hasItemsSelected: Boolean,
    hasOneItemSelected: Boolean,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onFilterClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        if (!hasItemsSelected) {
            FilterToolbarItem(onClick = onFilterClicked)
        }

        if (hasOneItemSelected) {
            EditToolbarItem(onClick = onEditClicked)
        }

        if (hasItemsSelected) {
            DeleteToolbarItem(onClick = onDeleteClicked)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderInfoCard(
    icon: @Composable () -> Unit,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    shape: Shape = RoundedCornerShape(8.dp),
    border: BorderStroke = CardDefaults.outlinedCardBorder(),
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedCard(
        modifier = modifier,
        onClick = onClick,
        shape = shape,
        border = border,
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Row(modifier = Modifier.padding(contentPadding)) {
            icon()

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                content()
            }
        }
    }
}

@Composable
fun HeaderCardItemInfo(
    title: String,
    info: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Crossfade(
            targetState = info,
            label = "HeaderCardItemInfoAnimation"
        ) { text ->
            Text(text = text)
        }
    }
}

@Composable
fun TodayInfoCard(
    filterActive: Boolean,
    undone: Int,
    done: Int,
    onClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (filterActive) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "TodayInfoCardBorderAnimation"
    )
    val borderStroke = remember(filterActive, borderColor) {
        BorderStroke(
            width = if (filterActive) {
                1.dp
            } else {
                0.dp
            },
            color = borderColor
        )
    }

    HeaderInfoCard(
        modifier = modifier,
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.outline_calendar_today_24),
                contentDescription = null
            )
        },
        title = stringResource(id = R.string.todo_list_today_card_title),
        onClick = {
            onClick(!filterActive)
        },
        border = borderStroke
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            HeaderCardItemInfo(
                title = stringResource(id = R.string.todo_list_undone_title),
                info = undone.toString()
            )

            HeaderCardItemInfo(
                title = stringResource(id = R.string.todo_list_done_title),
                info = done.toString()
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TodoItemListEntry(
    selected: Boolean,
    priority: Priority,
    done: Boolean,
    onDoneChanged: (Boolean) -> Unit,
    title: String,
    description: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        } else {
            Color.Unspecified
        },
        label = "TodoItemBackgroundAnimation"
    )

    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(),
                    onClick = onClick,
                    onLongClick = onLongClick
                )
                .background(color = backgroundColor)
                .padding(
                    start = 16.dp,
                    top = 16.dp,
                    end = 8.dp,
                    bottom = 16.dp
                )
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_flag_24),
                contentDescription = stringResource(id = priority.title),
                tint = priority.color
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                StrikethroughText(
                    text = title,
                    hasStrikethrough = done,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (description.isNotBlank()) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = description,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            DoItCheckbox(
                checked = done,
                onCheckedChange = {
                    onDoneChanged(it)
                }
            )
        }
    }
}

@Composable
fun TodoListEmptyState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(id = R.string.todo_list_empty_text))
    }
}

@Composable
fun TodoListFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_add_24),
            contentDescription = stringResource(id = R.string.todo_list_add)
        )
    }
}