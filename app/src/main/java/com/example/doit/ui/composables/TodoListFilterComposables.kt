package com.example.doit.ui.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.doit.R
import com.example.doit.domain.models.Priority
import com.example.doit.domain.models.Tag
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.models.TodoItemSortType

private const val ALL_TAGS_KEY = "allTags"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListFilterBottomSheet(
    onDismiss: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    state: SheetState = rememberModalBottomSheetState(),
    content: @Composable ColumnScope.() -> Unit
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
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            content()
        }
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
    TodoListFilterBottomSheet(
        modifier = modifier,
        onDismiss = onDismiss,
        title = stringResource(id = R.string.todo_list_tags_filter_title),
        state = state
    ) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListPriorityFilterBottomSheet(
    selectedPriority: Priority?,
    onDismiss: () -> Unit,
    onPrioritySelected: (Priority?) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    TodoListFilterBottomSheet(
        modifier = modifier,
        onDismiss = onDismiss,
        title = stringResource(id = R.string.todo_list_priority_filter_title)
    ) {
        Column(
            modifier = Modifier.border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface,
                shape = shape
            )
        ) {
            val allPrioBackgroundColor by animateColorAsState(
                targetValue = if (selectedPriority == null) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                } else {
                    Color.Unspecified
                },
                label = "AllPrioBackgroundAnimation"
            )
            val firstShape = RoundedCornerShape(
                topStart = 8.dp,
                topEnd = 8.dp
            )

            PriorityFilterItem(
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth()
                    .background(
                        color = allPrioBackgroundColor,
                        shape = firstShape
                    )
                    .clip(firstShape)
                    .clickable {
                        onPrioritySelected(null)
                        onDismiss()
                    },
                title = stringResource(id = R.string.todo_list_all_priorities),
                color = MaterialTheme.colorScheme.onSurface
            )

            Divider(
                color = MaterialTheme.colorScheme.onSurface
            )

            Priority.entries.forEachIndexed { index, priority ->
                val backgroundColor by animateColorAsState(
                    targetValue = if (selectedPriority == priority) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    } else {
                        Color.Unspecified
                    },
                    label = "PriorityBackgroundAnimation"
                )
                val prioShape = if (index < Priority.entries.lastIndex) {
                    RectangleShape
                } else {
                    RoundedCornerShape(
                        bottomStart = 8.dp,
                        bottomEnd = 8.dp
                    )
                }

                PriorityFilterItem(
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth()
                        .background(
                            color = backgroundColor,
                            shape = prioShape
                        )
                        .clip(prioShape)
                        .clickable {
                            onPrioritySelected(priority)
                            onDismiss()
                        },
                    title = stringResource(id = priority.title),
                    color = priority.color
                )

                if (index < Priority.entries.lastIndex) {
                    Divider(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

fun List<TodoItem>.applyFilter(
    tag: Tag?,
    priority: Priority?,
    hideDoneItems: Boolean
): List<TodoItem> {
    return this.filter {
        if (hideDoneItems && it.done) {
            return@filter false
        }

        if (tag != null && !it.tags.contains(tag)) {
            return@filter false
        }

        if (priority != null && it.priority != priority) {
            return@filter false
        }

        return@filter true
    }
}

fun List<TodoItem>.sort(type: TodoItemSortType): List<TodoItem> {
    return when (type) {
        TodoItemSortType.ALPHABETICAL -> this.sortedBy {
            it.title.lowercase()
        }

        TodoItemSortType.PRIORITY -> this.sortedByDescending {
            it.priority
        }

        TodoItemSortType.DUE_DATE -> this.sortedBy {
            it.dueDate
        }
    }
}