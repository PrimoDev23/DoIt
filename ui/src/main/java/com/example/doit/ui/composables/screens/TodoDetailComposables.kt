package com.example.doit.ui.composables.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.doit.common.R
import com.example.doit.domain.models.Priority
import com.example.doit.domain.models.Subtask
import com.example.doit.domain.models.Tag
import com.example.doit.domain.models.TodoItem
import com.example.doit.ui.arguments.TodoDetailNavArgs
import com.example.doit.ui.composables.DoItCheckbox
import com.example.doit.ui.viewmodels.TodoDetailViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.getViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@RootNavGraph
@Destination(navArgsDelegate = TodoDetailNavArgs::class)
@Composable
fun TodoDetailScreen(
    navigator: DestinationsNavigator,
    viewModel: TodoDetailViewModel = getViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TodoDetailTopBar(
                modifier = Modifier.fillMaxWidth(),
                onBackClicked = {
                    navigator.popBackStack()
                }
            )
        }
    ) {
        val isLoading by remember {
            derivedStateOf {
                state.item == null
            }
        }

        AnimatedContent(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            targetState = isLoading,
            label = "TodoDetailContent"
        ) { loading ->
            if (loading) {
                TodoDetailLoadingState(modifier = Modifier.fillMaxSize())
            } else {
                state.item?.let { item ->
                    TodoDetailContent(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        item = item,
                        onSubtaskDoneChanged = viewModel::onSubtaskDoneChanged
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailTopBar(
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        title = {},
        navigationIcon = {
            IconButton(onClick = onBackClicked) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_chevron_left_24),
                    contentDescription = stringResource(id = R.string.general_back)
                )
            }
        },
        actions = {

        }
    )
}

@Composable
fun TodoDetailLoadingState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun TodoDetailContent(
    item: TodoItem,
    onSubtaskDoneChanged: (Subtask, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = item.title,
            style = MaterialTheme.typography.titleLarge
        )

        val placeholderStyle = MaterialTheme.typography.bodyLarge.copy(
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        TodoDetailTextRow(
            modifier = Modifier.fillMaxWidth(),
            icon = painterResource(id = R.drawable.outline_notes_24),
            contentDescription = stringResource(id = R.string.todo_detail_description),
            text = item.description.ifBlank {
                stringResource(id = R.string.todo_detail_no_description)
            },
            maxLines = Int.MAX_VALUE,
            style = if (item.description.isNotBlank()) {
                MaterialTheme.typography.bodyLarge
            } else {
                placeholderStyle
            }
        )

        TodoDetailTextRow(
            modifier = Modifier.fillMaxWidth(),
            icon = painterResource(id = R.drawable.outline_flag_24),
            contentDescription = stringResource(id = R.string.todo_detail_priority),
            tint = item.priority.color,
            text = stringResource(id = item.priority.title)
        )

        val dateFormatter = remember {
            DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
        }

        TodoDetailTextRow(
            modifier = Modifier.fillMaxWidth(),
            icon = painterResource(id = R.drawable.outline_access_time_24),
            contentDescription = stringResource(id = R.string.todo_detail_due_date),
            text = item.dueDate?.format(dateFormatter)
                ?: stringResource(id = R.string.todo_detail_no_due_date),
            style = if (item.dueDate != null) {
                MaterialTheme.typography.bodyLarge
            } else {
                placeholderStyle
            }
        )

        val dateTimeFormatter = remember {
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT)
                .withZone(ZoneId.systemDefault())
        }

        TodoDetailTextRow(
            modifier = Modifier.fillMaxWidth(),
            icon = painterResource(id = R.drawable.outline_notifications_24),
            contentDescription = stringResource(id = R.string.todo_detail_notification),
            text = item.notificationDateTime?.format(dateTimeFormatter)
                ?: stringResource(id = R.string.todo_detail_no_notification),
            style = if (item.notificationDateTime != null) {
                MaterialTheme.typography.bodyLarge
            } else {
                placeholderStyle
            }
        )

        TodoDetailTags(
            modifier = Modifier.fillMaxWidth(),
            icon = painterResource(id = R.drawable.outline_label_24),
            contentDescription = null,
            tags = item.tags,
            placeholderStyle = placeholderStyle
        )

        if (item.subtasks.isNotEmpty()) {
            TodoDetailSubtasks(
                modifier = Modifier.fillMaxWidth(),
                subtasks = item.subtasks,
                onSubtaskDoneChanged = onSubtaskDoneChanged
            )
        }
    }
}

@Composable
fun TodoDetailTextRow(
    icon: Painter,
    contentDescription: String?,
    text: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp
    ),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(16.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    maxLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    Row(
        modifier = modifier.padding(contentPadding),
        horizontalArrangement = horizontalArrangement
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription,
            tint = tint
        )

        Text(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            text = text,
            style = style,
            maxLines = maxLines,
            overflow = overflow
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TodoDetailTags(
    icon: Painter,
    contentDescription: String?,
    tags: List<Tag>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp
    ),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(16.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface,
    placeholderStyle: TextStyle = MaterialTheme.typography.bodyMedium
) {
    Row(
        modifier = modifier.padding(contentPadding),
        horizontalArrangement = horizontalArrangement
    ) {
        Icon(
            modifier = if (tags.isNotEmpty()) {
                Modifier.padding(top = 8.dp)
            } else {
                Modifier
            },
            painter = icon,
            contentDescription = contentDescription,
            tint = tint
        )

        if (tags.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = horizontalArrangement,
                verticalArrangement = verticalArrangement
            ) {
                tags.forEach { tag ->
                    key(tag.id) {
                        TodoDetailTag(
                            modifier = Modifier.weight(
                                weight = 1f,
                                fill = false
                            ),
                            tag = tag
                        )
                    }
                }
            }
        } else {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                text = stringResource(id = R.string.todo_detail_no_tags),
                style = placeholderStyle
            )
        }
    }
}

@Composable
fun TodoDetailTag(
    tag: Tag,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 12.dp,
        vertical = 8.dp
    ),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    borderStroke: BorderStroke = BorderStroke(
        width = 1.dp,
        color = MaterialTheme.colorScheme.onSurface
    ),
    shape: Shape = RoundedCornerShape(8.dp)
) {
    Row(
        modifier = modifier
            .border(
                border = borderStroke,
                shape = shape
            )
            .padding(contentPadding),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_label_24),
            contentDescription = null,
            tint = tag.color
        )

        Text(
            modifier = Modifier.weight(
                weight = 1f,
                fill = false
            ),
            text = tag.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun TodoDetailSubtasks(
    subtasks: List<Subtask>,
    onSubtaskDoneChanged: (Subtask, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp
    ),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(16.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
) {
    Row(
        modifier = modifier.padding(contentPadding),
        horizontalArrangement = horizontalArrangement
    ) {
        Icon(
            modifier = Modifier
                .padding(top = 10.dp)
                .rotate(180f),
            painter = painterResource(id = R.drawable.outline_turn_left_24),
            contentDescription = stringResource(id = R.string.todo_detail_subtasks)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = verticalArrangement
        ) {
            subtasks.forEach { subtask ->
                key(subtask.id) {
                    TodoDetailSubtask(
                        modifier = Modifier.fillMaxWidth(),
                        subtask = subtask,
                        onDoneChanged = {
                            onSubtaskDoneChanged(subtask, it)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailSubtask(
    subtask: Subtask,
    onDoneChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(end = 16.dp),
    shape: Shape = RoundedCornerShape(8.dp)
) {
    ElevatedCard(
        modifier = modifier,
        shape = shape,
        onClick = {
            onDoneChanged(!subtask.done)
        }
    ) {
        Row(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DoItCheckbox(checked = subtask.done, onCheckedChange = onDoneChanged)

            Text(
                modifier = Modifier.weight(1f),
                text = subtask.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(
    name = "EmptyDetail",
    group = "TodoDetail",
    showSystemUi = true
)
@Composable
fun TodoDetailEmptyPreview() {
    TodoDetailContent(
        modifier = Modifier.fillMaxSize(),
        item = TodoItem(
            id = "",
            title = "Test1234",
            description = "",
            done = false,
            tags = emptyList(),
            priority = Priority.HIGH,
            dueDate = null,
            subtasks = emptyList(),
            notificationDateTime = null,
            creationDateTime = LocalDateTime.now()
        ),
        onSubtaskDoneChanged = { _, _ -> }
    )
}

@Preview(
    name = "FilledDetail",
    group = "TodoDetail",
    showSystemUi = true
)
@Composable
fun TodoDetailFilledPreview() {
    TodoDetailContent(
        modifier = Modifier.fillMaxSize(),
        item = TodoItem(
            id = "",
            title = "Test1234",
            description = "This is a description\nOver multiple lines",
            done = false,
            tags = listOf(
                Tag(
                    id = 0,
                    title = "Tag1",
                    color = Color.Red
                )
            ),
            priority = Priority.HIGH,
            dueDate = LocalDate.now(),
            subtasks = listOf(
                Subtask(
                    id = "",
                    title = "Test1234",
                    done = false,
                    creationDateTime = LocalDateTime.now()
                ),
                Subtask(
                    id = "1",
                    title = "Long subtask title to check if lines are properly formatted",
                    done = false,
                    creationDateTime = LocalDateTime.now()
                )
            ),
            notificationDateTime = LocalDateTime.now(),
            creationDateTime = LocalDateTime.now()
        ),
        onSubtaskDoneChanged = { _, _ -> })
}