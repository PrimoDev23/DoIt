package com.example.doit.ui.composables.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.doit.R
import com.example.doit.domain.models.Priority
import com.example.doit.domain.models.Tag
import com.example.doit.domain.models.TodoItem
import com.example.doit.ui.composables.DateTextField
import com.example.doit.ui.composables.DoItCheckbox
import com.example.doit.ui.composables.DoItTextField
import com.example.doit.ui.composables.InputTitle
import com.example.doit.ui.composables.PriorityItem
import com.example.doit.ui.composables.TagListEntry
import com.example.doit.ui.composables.VerticalGrid
import com.example.doit.ui.composables.rememberFocusRequester
import com.example.doit.ui.composables.screens.destinations.AddEntryScreenDestination
import com.example.doit.ui.navigation.arguments.AddEntryNavArgs
import com.example.doit.ui.viewmodels.AddEntryEvent
import com.example.doit.ui.viewmodels.AddEntryViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph
@Destination(navArgsDelegate = AddEntryNavArgs::class)
@Composable
fun AddEntryScreen(
    navArgs: AddEntryNavArgs,
    navigator: DestinationsNavigator,
    viewModel: AddEntryViewModel = hiltViewModel()
) {
    var showDatePickerDialog by remember {
        mutableStateOf(false)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    BackHandler(onBack = viewModel::onBackClicked)

    LaunchedEffect(true) {
        viewModel.events.collectLatest {
            when (it) {
                is AddEntryEvent.PopBackStack -> navigator.popBackStack()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AddEntryTopBar(
                modifier = Modifier.fillMaxWidth(),
                title = if (navArgs.parent == null) {
                    stringResource(id = R.string.add_entry_title)
                } else {
                    stringResource(id = R.string.add_subtask_title)
                },
                onBackClicked = viewModel::onBackClicked,
                onDeleteClicked = viewModel::onDeleteClicked
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            val focusRequester = rememberFocusRequester()

            LaunchedEffect(true) {
                if (!navArgs.edit) {
                    focusRequester.requestFocus()
                }
            }

            DoItTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = state.title,
                onValueChange = viewModel::onTitleChanged,
                label = stringResource(id = R.string.add_entry_title_title),
                singleLine = true
            )

            DoItTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.description,
                onValueChange = viewModel::onDescriptionChanged,
                label = stringResource(id = R.string.add_entry_description_title),
                maxLines = 5,
                minLines = 3
            )

            DateTextField(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(id = R.string.add_entry_due_date_title),
                value = state.dueDate,
                onClick = {
                    showDatePickerDialog = true
                }
            )

            AddEntryTagSelection(
                modifier = Modifier.fillMaxWidth(),
                tags = state.tags,
                onTagClicked = viewModel::onTagClicked
            )

            PrioritySelection(
                modifier = Modifier.fillMaxWidth(),
                priority = state.priority,
                onPriorityChanged = viewModel::onPriorityChanged
            )

            SubtaskSection(
                modifier = Modifier.fillMaxWidth(),
                onAddSubtaskClicked = {
                    navigator.navigate(
                        AddEntryScreenDestination(
                            id = UUID.randomUUID().toString(),
                            parent = navArgs.id,
                            edit = false
                        )
                    )
                },
                subtasks = state.subtasks,
                onSubtaskClicked = { item ->
                    navigator.navigate(
                        AddEntryScreenDestination(
                            id = item.id,
                            parent = navArgs.id,
                            edit = true
                        )
                    )
                },
                onDoneChanged = viewModel::onSubtaskDoneChanged,
                onRemoveClicked = viewModel::onSubtaskRemoveClicked
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (showDatePickerDialog) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePickerDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePickerDialog = false
                        viewModel.onDateConfirmed()
                    }
                ) {
                    Text(text = stringResource(id = R.string.general_ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePickerDialog = false

                        viewModel.onDateCleared()
                    }
                ) {
                    Text(text = stringResource(id = R.string.general_clear))
                }
            }
        ) {
            DatePicker(
                state = viewModel.datePickerState,
                title = {
                    Text(
                        modifier = Modifier.padding(
                            start = 24.dp,
                            end = 12.dp,
                            top = 16.dp
                        ),
                        text = stringResource(id = R.string.add_entry_due_date_dialog_title)
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntryTopBar(
    onBackClicked: () -> Unit,
    title: String,
    onDeleteClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClicked) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_chevron_left_24),
                    contentDescription = stringResource(id = R.string.general_back)
                )
            }
        },
        actions = {
            IconButton(onClick = onDeleteClicked) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_delete_24),
                    contentDescription = stringResource(id = R.string.general_delete)
                )
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AddEntryTagSelection(
    tags: List<Tag>,
    onTagClicked: (Tag) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchTerm by remember {
        mutableStateOf("")
    }
    val searchedTags by remember(tags) {
        derivedStateOf { tags.search(searchTerm) }
    }
    val hasTags = remember(searchedTags) {
        searchedTags.isNotEmpty()
    }

    Column(modifier = modifier) {
        DoItTextField(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(id = R.string.add_entry_add_tags_title),
            value = searchTerm,
            onValueChange = {
                searchTerm = it
            },
            placeholder = {
                Text(text = stringResource(id = R.string.add_entry_tag_search_placeholder))
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        searchTerm = ""
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_close_24),
                        contentDescription = stringResource(id = R.string.general_clear_textfield)
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = hasTags,
                label = "TagListAnimation"
            ) { innerHasTags ->
                if (innerHasTags) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(
                            items = searchedTags,
                            key = { tag ->
                                tag.id
                            }
                        ) { tag ->
                            TagListEntry(
                                modifier = Modifier
                                    .animateItemPlacement()
                                    .height(64.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        onTagClicked(tag)
                                    },
                                title = tag.title,
                                color = tag.color,
                                selected = tag.selected
                            )
                        }
                    }
                } else {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.add_entry_tags_empty),
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

private fun List<Tag>.search(term: String): List<Tag> {
    return if (term.isBlank()) {
        this
    } else {
        this.filter { tag ->
            tag.title.contains(
                other = term,
                ignoreCase = true
            )
        }
    }
}

@Composable
fun PrioritySelection(
    priority: Priority,
    onPriorityChanged: (Priority) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        InputTitle(text = stringResource(id = R.string.add_entry_priority_title))

        Spacer(modifier = Modifier.height(8.dp))

        VerticalGrid(
            modifier = Modifier.fillMaxWidth(),
            columns = 2,
            items = Priority.entries
        ) { item ->
            PriorityItem(
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(),
                selected = priority == item,
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_flag_24),
                        contentDescription = null,
                        tint = item.color
                    )
                },
                text = stringResource(id = item.title),
                onClick = {
                    onPriorityChanged(item)
                }
            )
        }
    }
}

@Composable
fun SubtaskSection(
    onAddSubtaskClicked: () -> Unit,
    subtasks: List<TodoItem>,
    onSubtaskClicked: (TodoItem) -> Unit,
    onDoneChanged: (TodoItem, Boolean) -> Unit,
    onRemoveClicked: (TodoItem) -> Unit,
    modifier: Modifier = Modifier,
    itemShape: Shape = RoundedCornerShape(8.dp)
) {
    Column(modifier = modifier) {
        InputTitle(text = stringResource(id = R.string.add_entry_subtask_title))

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            subtasks.forEach { task ->
                SubtaskItem(
                    modifier = Modifier.fillMaxWidth(),
                    shape = itemShape,
                    done = task.done,
                    onDoneChanged = {
                        onDoneChanged(task, it)
                    },
                    title = task.title,
                    onRemoveClicked = {
                        onRemoveClicked(task)
                    },
                    onClick = {
                        onSubtaskClicked(task)
                    }
                )
            }

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onAddSubtaskClicked,
                shape = itemShape
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_add_24),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(text = stringResource(id = R.string.add_entry_add_subtask))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubtaskItem(
    done: Boolean,
    onDoneChanged: (Boolean) -> Unit,
    title: String,
    onRemoveClicked: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(8.dp),
    shape: Shape = RoundedCornerShape(8.dp)
) {
    ElevatedCard(
        modifier = modifier,
        shape = shape,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DoItCheckbox(
                checked = done,
                onCheckedChange = onDoneChanged
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                modifier = Modifier.weight(1f),
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(onClick = onRemoveClicked) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_close_24),
                    contentDescription = stringResource(id = R.string.add_entry_remove_subtask)
                )
            }
        }
    }
}