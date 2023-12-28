package com.example.doit.ui.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.doit.common.R
import com.example.doit.domain.models.Priority
import com.example.doit.domain.models.Tag
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.models.TodoItemSortType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoItemsFilterBottomSheet(
    onDismiss: () -> Unit,
    onResetClicked: () -> Unit,
    selectedSortType: TodoItemSortType,
    onSortTypeClicked: (TodoItemSortType) -> Unit,
    hideDoneItems: Boolean,
    onHideDoneItemsChanged: (Boolean) -> Unit,
    tags: List<Tag>,
    selectedTags: List<Tag>,
    onTagClicked: (Tag) -> Unit,
    selectedPriorities: List<Priority>,
    onPriorityClicked: (Priority) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TodoItemsFilterHeader(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                onResetClicked = {
                    onResetClicked()
                    onDismiss()
                }
            )

            TodoItemsFilterSort(
                modifier = Modifier.fillMaxWidth(),
                selectedSortType = selectedSortType,
                onSortTypeClicked = onSortTypeClicked
            )

            Divider(modifier = Modifier.padding(horizontal = 16.dp))

            TodoItemsFilterSwitch(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.todo_list_hide_done_items),
                checked = hideDoneItems,
                onCheckedChanged = onHideDoneItemsChanged
            )

            Divider(modifier = Modifier.padding(horizontal = 16.dp))

            if (tags.isNotEmpty()) {
                TodoItemsFilterTags(
                    modifier = Modifier.fillMaxWidth(),
                    tags = tags,
                    selectedTags = selectedTags,
                    onTagClicked = onTagClicked
                )

                Divider(modifier = Modifier.padding(horizontal = 16.dp))
            }

            TodoItemsFilterPriorities(
                modifier = Modifier.fillMaxWidth(),
                selectedPriorities = selectedPriorities,
                onPriorityClicked = onPriorityClicked
            )
        }
    }
}

@Preview
@Composable
fun TodoItemsFilterBottomSheetPreview() {
    val tags = listOf(
        Tag(
            id = 0,
            title = "Tag1",
            color = Color.Green
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TodoItemsFilterHeader(
            modifier = Modifier.fillMaxWidth(),
            onResetClicked = {}
        )

        Divider()

        TodoItemsFilterSort(
            modifier = Modifier.fillMaxWidth(),
            selectedSortType = TodoItemSortType.ALPHABETICAL,
            onSortTypeClicked = {}
        )

        TodoItemsFilterSwitch(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.todo_list_hide_done_items),
            checked = true,
            onCheckedChanged = {}
        )

        if (tags.isNotEmpty()) {
            TodoItemsFilterTags(
                modifier = Modifier.fillMaxWidth(),
                tags = tags,
                selectedTags = tags,
                onTagClicked = {}
            )
        }

        TodoItemsFilterPriorities(
            modifier = Modifier.fillMaxWidth(),
            selectedPriorities = emptyList(),
            onPriorityClicked = {}
        )
    }
}

@Composable
fun TodoItemsFilterSwitch(
    text: String,
    checked: Boolean,
    onCheckedChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp)
) {
    Row(
        modifier = modifier.padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChanged
        )
    }
}

@Composable
fun TodoItemsFilterTags(
    tags: List<Tag>,
    selectedTags: List<Tag>,
    onTagClicked: (Tag) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp)
) {
    TodoItemsFilterSection(
        modifier = modifier,
        title = stringResource(id = R.string.todo_list_tags_filter_title),
        contentPadding = contentPadding
    ) {
        val layoutDirection = LocalLayoutDirection.current
        val startPadding = contentPadding.calculateStartPadding(layoutDirection)
        val endPadding = contentPadding.calculateEndPadding(layoutDirection)

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                start = startPadding,
                end = endPadding
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = tags,
                key = {
                    it.id
                }
            ) { tag ->
                val selected = remember(selectedTags, tag) {
                    selectedTags.contains(tag)
                }

                TodoItemsFilterTag(
                    tag = tag,
                    onClick = {
                        onTagClicked(tag)
                    },
                    selected = selected
                )
            }
        }
    }
}

@Composable
fun TodoItemsFilterTag(
    tag: Tag,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 8.dp
    ),
    shape: Shape = RoundedCornerShape(8.dp)
) {
    val borderColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        label = "TodoItemsFilterTagBorderColor"
    )

    Row(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape
            )
            .clip(shape)
            .clickable(onClick = onClick)
            .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_label_24),
            contentDescription = null,
            tint = tag.color
        )

        Text(text = tag.title)
    }
}

@Composable
fun TodoItemsFilterPriorities(
    selectedPriorities: List<Priority>,
    onPriorityClicked: (Priority) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp)
) {
    TodoItemsFilterSection(
        modifier = modifier,
        title = stringResource(id = R.string.todo_list_priority_filter_title),
        contentPadding = contentPadding
    ) {
        val layoutDirection = LocalLayoutDirection.current
        val startPadding = contentPadding.calculateStartPadding(layoutDirection)
        val endPadding = contentPadding.calculateEndPadding(layoutDirection)
        val priorities = Priority.entries

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                start = startPadding,
                end = endPadding
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = priorities,
                key = {
                    it
                }
            ) { prio ->
                val selected = remember(selectedPriorities, prio) {
                    selectedPriorities.contains(prio)
                }

                TodoItemsFilterPriority(
                    prio = prio,
                    selected = selected,
                    onClick = {
                        onPriorityClicked(prio)
                    }
                )
            }
        }
    }
}

@Composable
fun TodoItemsFilterPriority(
    prio: Priority,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 8.dp
    ),
    shape: Shape = RoundedCornerShape(8.dp)
) {
    val borderColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        label = "TodoItemsFilterPriorityBorderColor"
    )

    Row(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape
            )
            .clip(shape)
            .clickable(onClick = onClick)
            .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.outline_flag_24),
            contentDescription = null,
            tint = prio.color
        )

        Text(text = stringResource(id = prio.title))
    }
}

@Composable
fun TodoItemsFilterSort(
    selectedSortType: TodoItemSortType,
    onSortTypeClicked: (TodoItemSortType) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp)
) {
    TodoItemsFilterSection(
        modifier = modifier,
        title = stringResource(id = R.string.todo_list_filter_sort),
        contentPadding = contentPadding
    ) {
        val layoutDirection = LocalLayoutDirection.current
        val startPadding = contentPadding.calculateStartPadding(layoutDirection)
        val endPadding = contentPadding.calculateEndPadding(layoutDirection)

        val sortTypes = TodoItemSortType.entries

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                start = startPadding,
                end = endPadding
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = sortTypes,
                key = {
                    it
                }
            ) { sort ->
                TodoItemsFilterSortType(
                    sort = sort,
                    selected = selectedSortType == sort,
                    onClick = {
                        onSortTypeClicked(sort)
                    }
                )
            }
        }
    }
}

@Composable
fun TodoItemsFilterSortType(
    sort: TodoItemSortType,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 8.dp
    ),
    shape: Shape = RoundedCornerShape(8.dp)
) {
    val borderColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        label = "TodoItemsFilterSortTypeBorderColor"
    )

    Row(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape
            )
            .clip(shape)
            .clickable(onClick = onClick)
            .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(id = sort.title))
    }
}

@Composable
fun TodoItemsFilterSection(
    title: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 8.dp
    ),
    content: @Composable ColumnScope.() -> Unit
) {
    val topPadding = contentPadding.calculateTopPadding()
    val bottomPadding = contentPadding.calculateBottomPadding()

    Column(
        modifier = modifier.padding(
            top = topPadding,
            bottom = bottomPadding
        ),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val layoutDirection = LocalLayoutDirection.current
        val startPadding = contentPadding.calculateStartPadding(layoutDirection)
        val endPadding = contentPadding.calculateEndPadding(layoutDirection)

        Text(
            modifier = Modifier.padding(
                start = startPadding,
                end = endPadding
            ),
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )

        content()
    }
}

@Composable
fun TodoItemsFilterHeader(
    onResetClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.todo_list_filter_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        TextButton(
            modifier = Modifier.align(Alignment.CenterEnd),
            onClick = onResetClicked
        ) {
            Text(text = stringResource(id = R.string.todo_list_filter_reset))
        }
    }
}

fun List<TodoItem>.applyFilter(
    tags: List<Tag>,
    priorities: List<Priority>,
    hideDoneItems: Boolean
): List<TodoItem> {
    return this.filter {
        if (hideDoneItems && it.done) {
            return@filter false
        }

        if (tags.isNotEmpty() && !it.tags.any(tags::contains)) {
            return@filter false
        }

        return@filter priorities.isEmpty() || priorities.contains(it.priority)
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

        TodoItemSortType.DUE_DATE -> this.sortedByDescending {
            it.dueDate
        }

        TodoItemSortType.CREATION_DATE -> this.sortedByDescending {
            it.creationDateTime
        }
    }
}