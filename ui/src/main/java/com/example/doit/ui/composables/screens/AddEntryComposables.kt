package com.example.doit.ui.composables.screens

import android.Manifest
import android.os.Build
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
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.doit.common.R
import com.example.doit.domain.models.Priority
import com.example.doit.domain.models.Subtask
import com.example.doit.domain.models.Tag
import com.example.doit.ui.arguments.AddEntryNavArgs
import com.example.doit.ui.composables.DateTextField
import com.example.doit.ui.composables.DateTimeDialog
import com.example.doit.ui.composables.DoItCheckbox
import com.example.doit.ui.composables.DoItTextField
import com.example.doit.ui.composables.InputTitle
import com.example.doit.ui.composables.PriorityItem
import com.example.doit.ui.composables.TagListEntry
import com.example.doit.ui.composables.VerticalGrid
import com.example.doit.ui.composables.locals.LocalDrawerState
import com.example.doit.ui.composables.rememberFocusRequester
import com.example.doit.ui.viewmodels.AddEntryEvent
import com.example.doit.ui.viewmodels.AddEntryState
import com.example.doit.ui.viewmodels.AddEntryViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.getViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph
@Destination(navArgsDelegate = AddEntryNavArgs::class)
@Composable
fun AddEntryScreen(
    navArgs: AddEntryNavArgs,
    navigator: DestinationsNavigator,
    viewModel: AddEntryViewModel = getViewModel()
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

    AddEntryContent(
        modifier = Modifier.fillMaxSize(),
        edit = navArgs.edit,
        state = state,
        onBackClicked = viewModel::onBackClicked,
        onDeleteClicked = viewModel::onDeleteClicked,
        onSaveClicked = viewModel::onSaveClicked,
        onTitleChanged = viewModel::onTitleChanged,
        onDescriptionChanged = viewModel::onDescriptionChanged,
        onSubtaskAdded = viewModel::onSubtaskAdded,
        onSubtaskTitleUpdated = viewModel::onSubtaskTitleUpdated,
        onSubtaskDoneChanged = viewModel::onSubtaskDoneChanged,
        onRemoveSubtaskClicked = viewModel::onSubtaskRemoveClicked,
        onDueDateClicked = {
            showDatePickerDialog = true
        },
        onNotificationDateTimePicked = viewModel::onNotificationDateTimePicked,
        onTagSearchTermChanged = viewModel::onTagSearchTermChanged,
        onTagClicked = viewModel::onTagClicked,
        onPriorityChanged = viewModel::onPriorityChanged
    )

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
            Spacer(modifier = Modifier.height(16.dp))

            DatePicker(
                state = viewModel.datePickerState,
                title = null
            )
        }
    }
}

@Preview(apiLevel = 32) // API 32 to disable permission check
@Composable
fun AddEntryContentPreview() {
    CompositionLocalProvider(LocalDrawerState provides rememberDrawerState(initialValue = DrawerValue.Closed)) {
        AddEntryContent(
            edit = false,
            state = AddEntryState(
                title = "Test",
                titleHasError = false,
                description = "Test description",
                dueDate = LocalDate.now(),
                notificationDateTime = LocalDateTime.now(),
                tagSearchTerm = "",
                tags = persistentListOf(),
                selectedTags = persistentListOf(),
                priority = Priority.HIGH,
                subtasks = persistentListOf()
            ),
            onBackClicked = {},
            onDeleteClicked = {},
            onSaveClicked = {},
            onTitleChanged = {},
            onDescriptionChanged = {},
            onSubtaskAdded = {},
            onSubtaskTitleUpdated = { _, _ -> },
            onSubtaskDoneChanged = { _, _ -> },
            onRemoveSubtaskClicked = {},
            onDueDateClicked = {},
            onNotificationDateTimePicked = {},
            onTagSearchTermChanged = {},
            onTagClicked = {},
            onPriorityChanged = {}
        )
    }
}

@Composable
fun AddEntryContent(
    edit: Boolean,
    state: AddEntryState,
    onBackClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onSaveClicked: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onSubtaskAdded: (Subtask) -> Unit,
    onSubtaskTitleUpdated: (Subtask, String) -> Unit,
    onSubtaskDoneChanged: (Subtask, Boolean) -> Unit,
    onRemoveSubtaskClicked: (Subtask) -> Unit,
    onDueDateClicked: () -> Unit,
    onNotificationDateTimePicked: (LocalDateTime?) -> Unit,
    onTagSearchTermChanged: (String) -> Unit,
    onTagClicked: (Tag) -> Unit,
    onPriorityChanged: (Priority) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            AddEntryTopBar(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(id = R.string.add_entry_title),
                onBackClicked = onBackClicked,
                onDeleteClicked = onDeleteClicked
            )
        },
        floatingActionButton = {
            SaveFloatingActionButton(onClick = onSaveClicked)
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
            val titleFocusRequester = rememberFocusRequester()

            LaunchedEffect(true) {
                if (!edit) {
                    titleFocusRequester.requestFocus()
                }
            }

            DoItTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(titleFocusRequester),
                value = state.title,
                onValueChange = onTitleChanged,
                label = stringResource(id = R.string.add_entry_title_title),
                singleLine = true,
                isError = state.titleHasError,
                errorText = stringResource(id = R.string.add_entry_title_error)
            )

            DoItTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.description,
                onValueChange = onDescriptionChanged,
                label = stringResource(id = R.string.add_entry_description_title),
                maxLines = 5,
                minLines = 3
            )

            SubtaskSection(
                modifier = Modifier.fillMaxWidth(),
                onSubtaskAdded = onSubtaskAdded,
                subtasks = state.subtasks,
                onTitleUpdated = onSubtaskTitleUpdated,
                onDoneChanged = onSubtaskDoneChanged,
                onRemoveClicked = onRemoveSubtaskClicked
            )

            DueDateSection(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(id = R.string.add_entry_due_date_title),
                date = state.dueDate,
                onClick = onDueDateClicked
            )

            NotificationRow(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(id = R.string.add_entry_notification_title),
                notificationDateTime = state.notificationDateTime,
                dueDate = state.dueDate,
                onDateTimePicked = onNotificationDateTimePicked
            )

            AddEntryTagSelection(
                modifier = Modifier.fillMaxWidth(),
                tagSearchTerm = state.tagSearchTerm,
                onTagSearchTermChanged = onTagSearchTermChanged,
                tags = state.tags,
                selectedTags = state.selectedTags,
                onTagClicked = onTagClicked
            )

            PrioritySelection(
                modifier = Modifier.fillMaxWidth(),
                priority = state.priority,
                onPriorityChanged = onPriorityChanged
            )

            Spacer(modifier = Modifier.height(8.dp))
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

@Composable
fun SaveFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_check_24),
            contentDescription = stringResource(id = R.string.general_save)
        )
    }
}

@Composable
fun DueDateSection(
    title: String,
    date: LocalDate?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        InputTitle(text = title)

        DateTextField(
            modifier = Modifier.fillMaxWidth(),
            value = date,
            onClick = onClick
        )
    }
}

@Composable
fun NotificationRow(
    title: String,
    notificationDateTime: LocalDateTime?,
    dueDate: LocalDate?,
    onDateTimePicked: (LocalDateTime?) -> Unit,
    modifier: Modifier = Modifier,
    formatter: DateTimeFormatter = remember { DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT) },
    noNotificationString: String = stringResource(id = R.string.add_entry_notification_empty)
) {
    NotificationRowWrapper(
        modifier = modifier,
        title = title
    ) {
        val text = remember(notificationDateTime) {
            notificationDateTime?.format(formatter) ?: noNotificationString
        }
        var showPickerDialog by remember {
            mutableStateOf(false)
        }

        DoItTextField(
            value = text,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                IconButton(
                    onClick = {
                        showPickerDialog = true
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_edit_notifications_24),
                        contentDescription = stringResource(id = R.string.add_entry_edit_notification)
                    )
                }
            }
        )

        if (showPickerDialog) {
            val realDate = remember(notificationDateTime, dueDate) {
                notificationDateTime ?: dueDate?.atStartOfDay()
            }

            DateTimeDialog(
                value = realDate,
                onDismiss = {
                    showPickerDialog = false
                },
                onConfirm = onDateTimePicked
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationRowWrapper(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        InputTitle(text = title)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationPermissionState =
                rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

            if (notificationPermissionState.status.isGranted) {
                content()
            } else {
                NotificationPermissionRationale(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        notificationPermissionState.launchPermissionRequest()
                    }
                )
            }
        } else {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationPermissionRationale(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .then(modifier),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_circle_notifications_24),
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(text = stringResource(id = R.string.add_entry_notification_rationale))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AddEntryTagSelection(
    tagSearchTerm: String,
    onTagSearchTermChanged: (String) -> Unit,
    tags: PersistentList<Tag>,
    selectedTags: PersistentList<Tag>,
    onTagClicked: (Tag) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        DoItTextField(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(id = R.string.add_entry_add_tags_title),
            value = tagSearchTerm,
            onValueChange = onTagSearchTermChanged,
            placeholder = {
                Text(text = stringResource(id = R.string.add_entry_tag_search_placeholder))
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        onTagSearchTermChanged("")
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
                targetState = tags.isNotEmpty(),
                label = "TagListAnimation"
            ) { hasTags ->
                if (hasTags) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(
                            items = tags,
                            key = { tag ->
                                tag.id
                            }
                        ) { tag ->
                            val selected = remember(selectedTags, tag) {
                                selectedTags.any {
                                    it.id == tag.id
                                }
                            }

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
                                selected = selected
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

@Composable
fun PrioritySelection(
    priority: Priority,
    onPriorityChanged: (Priority) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        InputTitle(text = stringResource(id = R.string.add_entry_priority_title))

        val entries = remember {
            Priority.entries.toPersistentList()
        }

        VerticalGrid(
            modifier = Modifier.fillMaxWidth(),
            columns = 2,
            items = entries
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
    onSubtaskAdded: (Subtask) -> Unit,
    subtasks: PersistentList<Subtask>,
    onTitleUpdated: (Subtask, String) -> Unit,
    onDoneChanged: (Subtask, Boolean) -> Unit,
    onRemoveClicked: (Subtask) -> Unit,
    modifier: Modifier = Modifier,
    itemShape: Shape = RoundedCornerShape(8.dp)
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier) {
        InputTitle(text = stringResource(id = R.string.add_entry_subtask_title))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            var addSubtaskDialogVisible by remember {
                mutableStateOf(false)
            }
            var editSubtaskItem by remember {
                mutableStateOf<Subtask?>(null)
            }

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
                        focusManager.clearFocus()
                        editSubtaskItem = task
                    }
                )
            }

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    focusManager.clearFocus()
                    addSubtaskDialogVisible = true
                },
                shape = itemShape
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_add_24),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(text = stringResource(id = R.string.add_entry_add_subtask))
            }

            if (addSubtaskDialogVisible) {
                AddSubtaskDialog(
                    onDismiss = {
                        addSubtaskDialogVisible = false
                    },
                    onConfirm = {
                        val subtask = Subtask(
                            id = UUID.randomUUID().toString(),
                            title = it,
                            done = false,
                            creationDateTime = LocalDateTime.now()
                        )

                        onSubtaskAdded(subtask)
                    }
                )
            }

            editSubtaskItem?.let { subtask ->
                EditSubtaskDialog(
                    title = subtask.title,
                    onDismiss = {
                        editSubtaskItem = null
                    },
                    onConfirm = {
                        onTitleUpdated(subtask, it)
                    }
                )
            }
        }
    }
}

@Composable
fun AddSubtaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shape = shape,
            tonalElevation = 1.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        top = 16.dp,
                        end = 16.dp,
                        bottom = 8.dp
                    )
                    .fillMaxWidth()
            ) {
                val focusRequester = rememberFocusRequester()
                var text by remember {
                    mutableStateOf("")
                }
                val confirmEnabled by remember {
                    derivedStateOf {
                        text.isNotBlank()
                    }
                }

                LaunchedEffect(true) {
                    focusRequester.requestFocus()
                }

                Text(
                    text = stringResource(id = R.string.add_entry_add_subtask_dialog_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(id = R.string.add_entry_add_subtask_dialog_description),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                DoItTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    value = text,
                    onValueChange = {
                        text = it
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.align(Alignment.End)) {
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(id = R.string.general_cancel))
                    }

                    TextButton(
                        enabled = confirmEnabled,
                        onClick = {
                            onConfirm(text)
                            onDismiss()
                        }
                    ) {
                        Text(text = stringResource(id = R.string.general_ok))
                    }
                }
            }
        }
    }
}

@Composable
fun EditSubtaskDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shape = shape,
            tonalElevation = 1.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        top = 16.dp,
                        end = 16.dp,
                        bottom = 8.dp
                    )
                    .fillMaxWidth()
            ) {
                val focusRequester = rememberFocusRequester()
                var text by remember(title) {
                    mutableStateOf(title)
                }
                val confirmEnabled by remember {
                    derivedStateOf {
                        text.isNotBlank()
                    }
                }

                LaunchedEffect(true) {
                    focusRequester.requestFocus()
                }

                Text(
                    text = stringResource(id = R.string.add_entry_edit_subtask_dialog_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(id = R.string.add_entry_edit_subtask_dialog_description),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                DoItTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    value = text,
                    onValueChange = {
                        text = it
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.align(Alignment.End)) {
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(id = R.string.general_cancel))
                    }

                    TextButton(
                        enabled = confirmEnabled,
                        onClick = {
                            onConfirm(text)
                            onDismiss()
                        }
                    ) {
                        Text(text = stringResource(id = R.string.general_ok))
                    }
                }
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