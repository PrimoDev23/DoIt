package com.example.doit.ui.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
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
import com.example.doit.ui.composables.destinations.AddEntryScreenDestination
import com.example.doit.ui.viewmodels.TodoListViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

private const val ALL_TAGS_KEY = "allTags"

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun TodoListScreen(
    navigator: DestinationsNavigator,
    onMenuClicked: () -> Unit,
    viewModel: TodoListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var showBottomSheet by remember {
        mutableStateOf(false)
    }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    RootScaffold(
        modifier = Modifier.fillMaxSize(),
        onMenuClicked = onMenuClicked,
        title = stringResource(id = R.string.todo_list_title),
        actions = {
            val showEditAction by remember {
                derivedStateOf {
                    state.selectedItems.size == 1
                }
            }

            val showDeleteAction by remember {
                derivedStateOf {
                    state.selectedItems.isNotEmpty()
                }
            }

            EditToolbarItem(
                isVisible = showEditAction,
                onClick = {
                    val id = state.selectedItems.first().id

                    navigator.navigate(AddEntryScreenDestination(id = id))
                    viewModel.onEditClicked()
                }
            )

            DeleteToolbarItem(
                isVisible = showDeleteAction,
                onClick = viewModel::onDeleteClicked
            )
        },
        floatingActionButton = {
            TodoListFloatingActionButton(
                onClick = {
                    navigator.navigate(AddEntryScreenDestination(id = 0))
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            val filteredItems by remember {
                derivedStateOf {
                    if (state.selectedTag == null) {
                        state.items
                    } else {
                        state.items.filter { item ->
                            item.tags.contains(state.selectedTag)
                        }
                    }
                }
            }
            val hasItems = remember(filteredItems) {
                filteredItems.isNotEmpty()
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .horizontalScroll(state = rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val tagFilterSelected by remember {
                    derivedStateOf {
                        state.selectedTag != null
                    }
                }

                FilterChip(
                    selected = tagFilterSelected,
                    onClick = {
                        if (state.tags.isNotEmpty()) {
                            showBottomSheet = true
                        }
                    },
                    label = {
                        val tag = state.selectedTag

                        Text(
                            text = tag?.title
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
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(id = R.string.todo_list_empty_text))
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        TodoListTagFilterBottomSheet(
            onDismiss = {
                showBottomSheet = false
            },
            state = bottomSheetState,
            selectedTag = state.selectedTag,
            tags = state.tags,
            onTagClicked = viewModel::onTagFilterClicked
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
                val contentColor = LocalContentColor.current
                val strikethroughProgress by animateFloatAsState(
                    targetValue = if (done) {
                        1f
                    } else {
                        0f
                    },
                    label = "StrikethroughAnimation"
                )

                Text(
                    modifier = Modifier
                        .drawWithContent {
                            drawContent()

                            val strokeWidth = (1.5).dp.toPx()
                            val lineWidth = strikethroughProgress * this.size.width

                            drawLine(
                                color = contentColor,
                                start = Offset(
                                    x = 0f,
                                    y = center.y
                                ),
                                end = Offset(
                                    x = lineWidth,
                                    y = center.y
                                ),
                                strokeWidth = strokeWidth
                            )
                        },
                    text = title,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListTagFilterBottomSheet(
    onDismiss: () -> Unit,
    selectedTag: Tag?,
    tags: List<Tag>,
    onTagClicked: (Tag?) -> Unit,
    modifier: Modifier = Modifier,
    state: SheetState = rememberModalBottomSheetState(),
    shape: Shape = RoundedCornerShape(8.dp)
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismiss,
        sheetState = state
    ) {
        Column(
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 48.dp
                )
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.todo_list_tags_filter_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = shape
                    )
                    .fillMaxWidth()
            ) {
                item(key = ALL_TAGS_KEY) {
                    val backgroundColor by animateColorAsState(
                        targetValue = if (selectedTag == null) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        } else {
                            Color.Unspecified
                        },
                        label = "AllTagsBackgroundAnimation"
                    )

                    val clickShape = remember(tags) {
                        if (tags.isNotEmpty()) {
                            RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                        } else {
                            shape
                        }
                    }

                    Row(
                        modifier = Modifier
                            .height(56.dp)
                            .fillMaxWidth()
                            .clip(clickShape)
                            .background(
                                color = backgroundColor,
                                shape = clickShape
                            )
                            .clickable {
                                onTagClicked(null)
                                onDismiss()
                            }
                            .padding(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_label_24),
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(id = R.string.todo_list_all_tags)
                        )
                    }

                    if (tags.isNotEmpty()) {
                        Divider(
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                itemsIndexed(
                    items = tags,
                    key = { _, item ->
                        item.id
                    }
                ) { index, item ->
                    val clickShape = remember(tags) {
                        if (index == tags.lastIndex) {
                            RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                        } else {
                            RectangleShape
                        }
                    }

                    TagListEntry(
                        modifier = Modifier
                            .height(56.dp)
                            .fillMaxWidth()
                            .clip(clickShape)
                            .clickable {
                                onTagClicked(item)
                                onDismiss()
                            },
                        title = item.title,
                        color = item.color,
                        highlighted = selectedTag == item
                    )

                    if (index < tags.lastIndex) {
                        Divider(
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}