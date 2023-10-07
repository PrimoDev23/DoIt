package com.example.doit.ui.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.doit.R
import com.example.doit.domain.models.Priority
import com.example.doit.domain.models.Tag
import com.example.doit.domain.models.TodoItemSortType
import com.example.doit.ui.composables.destinations.AddEntryScreenDestination
import com.example.doit.ui.viewmodels.TodoListViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun TodoListScreen(
    navigator: DestinationsNavigator,
    viewModel: TodoListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val hasItemsSelected by remember {
        derivedStateOf {
            state.selectedItems.isNotEmpty()
        }
    }
    var showTagFilterBottomSheet by remember {
        mutableStateOf(false)
    }
    var showPrioFilterBottomSheet by remember {
        mutableStateOf(false)
    }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    RootScaffold(
        modifier = Modifier.fillMaxSize(),
        title = stringResource(id = R.string.todo_list_title),
        actions = {
            val hasOneItemSelected by remember {
                derivedStateOf {
                    state.selectedItems.size == 1
                }
            }

            TodoListToolbarActions(
                hasItemsSelected = hasItemsSelected,
                hasOneItemSelected = hasOneItemSelected,
                onEditClicked = {
                    val id = state.selectedItems.first().id

                    navigator.navigate(AddEntryScreenDestination(id = id))
                    viewModel.onEditClicked()
                },
                onDeleteClicked = viewModel::onDeleteClicked
            )
        },
        floatingActionButton = {
            TodoListFloatingActionButton(
                onClick = {
                    navigator.navigate(AddEntryScreenDestination(id = 0))
                }
            )
        },
        navigationIcon = {
            AnimatedContent(
                targetState = hasItemsSelected,
                label = "ShowClearIconAnimation"
            ) { showClearSelection ->
                if (showClearSelection) {
                    ClearSelectionButton(onClick = viewModel::onClearSelectionClicked)
                } else {
                    DrawerMenuButton()
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            val tagFilterSelected by remember {
                derivedStateOf {
                    state.selectedTag != null
                }
            }
            val priorityFilterSelected by remember {
                derivedStateOf {
                    state.selectedPriority != null
                }
            }
            val filteredItems by remember {
                derivedStateOf {
                    state.items.applyFilter(state.selectedTag, state.selectedPriority)
                }
            }
            val hasItems = remember(filteredItems) {
                filteredItems.isNotEmpty()
            }

            TodoListSettingsRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .horizontalScroll(state = rememberScrollState()),
                sortType = state.sortType,
                onSortTypeChanged = viewModel::onSortTypeChanged,
                hideDoneItems = state.hideDoneItems,
                onHideDoneItemsChanged = viewModel::onHideDoneItemsChanged,
                tagFilterSelected = tagFilterSelected,
                onTagFilterClicked = {
                    if (state.tags.isNotEmpty()) {
                        showTagFilterBottomSheet = true
                    }
                },
                selectedTag = state.selectedTag,
                priorityFilterSelected = priorityFilterSelected,
                onPriorityFilterClicked = {
                    showPrioFilterBottomSheet = true
                },
                selectedPriority = state.selectedPriority
            )

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
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            items = filteredItems,
                            key = { item ->
                                item.id
                            }
                        ) { item ->
                            val selected by remember {
                                derivedStateOf {
                                    state.selectedItems.contains(item)
                                }
                            }

                            val backgroundColor by animateColorAsState(
                                targetValue = if (selected) {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                } else {
                                    Color.Unspecified
                                },
                                label = "TodoItemBackgroundAnimation"
                            )

                            TodoItemListEntry(
                                modifier = Modifier
                                    .animateItemPlacement()
                                    .fillMaxWidth(),
                                backgroundColor = backgroundColor,
                                priority = item.priority,
                                done = item.done,
                                onDoneChanged = { done ->
                                    viewModel.onDoneChanged(item, done)
                                },
                                title = item.title,
                                description = item.description,
                                onClick = {
                                    if (state.selectedItems.isNotEmpty()) {
                                        viewModel.onItemSelected(item)
                                    }
                                },
                                onLongClick = {
                                    viewModel.onItemSelected(item)
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

    if (showTagFilterBottomSheet) {
        TodoListTagFilterBottomSheet(
            onDismiss = {
                showTagFilterBottomSheet = false
            },
            state = bottomSheetState,
            selectedTag = state.selectedTag,
            tags = state.tags,
            onTagClicked = viewModel::onTagFilterClicked
        )
    }

    if (showPrioFilterBottomSheet) {
        TodoListPriorityFilterBottomSheet(
            selectedPriority = state.selectedPriority,
            onPrioritySelected = viewModel::onPrioritySelected,
            onDismiss = {
                showPrioFilterBottomSheet = false
            }
        )
    }
}

@Composable
fun TodoListToolbarActions(
    hasItemsSelected: Boolean,
    hasOneItemSelected: Boolean,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        EditToolbarItem(
            isVisible = hasOneItemSelected,
            onClick = onEditClicked
        )

        DeleteToolbarItem(
            isVisible = hasItemsSelected,
            onClick = onDeleteClicked
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListSettingsRow(
    sortType: TodoItemSortType,
    onSortTypeChanged: (TodoItemSortType) -> Unit,
    hideDoneItems: Boolean,
    onHideDoneItemsChanged: (Boolean) -> Unit,
    tagFilterSelected: Boolean,
    onTagFilterClicked: () -> Unit,
    selectedTag: Tag?,
    priorityFilterSelected: Boolean,
    onPriorityFilterClicked: () -> Unit,
    selectedPriority: Priority?,
    modifier: Modifier = Modifier
) {
    var sortMenuExpanded by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row {
            SuggestionChip(
                onClick = {
                    sortMenuExpanded = !sortMenuExpanded
                },
                label = {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_sort_24),
                        contentDescription = null
                    )
                }
            )

            TodoListSortDropDownMenu(
                expanded = sortMenuExpanded,
                onDismiss = {
                    sortMenuExpanded = false
                },
                sortType = sortType,
                onSortTypeChanged = onSortTypeChanged,
                hideDoneItems = hideDoneItems,
                onHideDoneItemsChanged = onHideDoneItemsChanged
            )
        }

        FilterChip(
            selected = tagFilterSelected,
            onClick = onTagFilterClicked,
            label = {
                Text(
                    text = selectedTag?.title
                        ?: stringResource(id = R.string.todo_list_all_tags)
                )
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.outline_arrow_drop_down_24),
                    contentDescription = null
                )
            }
        )

        FilterChip(
            selected = priorityFilterSelected,
            onClick = onPriorityFilterClicked,
            label = {
                Text(
                    text = stringResource(
                        id = selectedPriority?.title ?: R.string.todo_list_all_priorities
                    )
                )
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.outline_arrow_drop_down_24),
                    contentDescription = null
                )
            }
        )
    }
}

@Composable
fun TodoListSortDropDownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    sortType: TodoItemSortType,
    onSortTypeChanged: (TodoItemSortType) -> Unit,
    hideDoneItems: Boolean,
    onHideDoneItemsChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        modifier = modifier,
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        TodoItemSortType.entries.forEach { type ->
            ToggleableDropDownMenuItem(
                checked = sortType == type,
                onCheckedChange = {
                    if (it) {
                        onSortTypeChanged(type)
                    }
                },
                text = stringResource(id = type.title)
            )
        }

        Divider()

        ToggleableDropDownMenuItem(
            checked = hideDoneItems,
            onCheckedChange = onHideDoneItemsChanged,
            text = stringResource(id = R.string.todo_list_hide_done_items)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TodoItemListEntry(
    backgroundColor: Color,
    priority: Priority,
    done: Boolean,
    onDoneChanged: (Boolean) -> Unit,
    title: String,
    description: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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