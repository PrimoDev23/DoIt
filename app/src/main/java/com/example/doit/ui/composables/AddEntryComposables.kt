package com.example.doit.ui.composables

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.doit.R
import com.example.doit.ui.viewmodels.AddEntryEvent
import com.example.doit.ui.viewmodels.AddEntryViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest

@RootNavGraph
@Destination
@Composable
fun AddEntryScreen(
    navigator: DestinationsNavigator,
    viewModel: AddEntryViewModel = hiltViewModel()
) {
    var showDismissDialog by remember {
        mutableStateOf(false)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val isValid by remember {
        derivedStateOf {
            state.isValid()
        }
    }

    BackHandler(onBack = viewModel::onBackClicked)

    LaunchedEffect(true) {
        viewModel.events.collectLatest {
            when (it) {
                is AddEntryEvent.ShowDismissDialog -> showDismissDialog = true
                is AddEntryEvent.PopBackStack -> navigator.popBackStack()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AddEntryTopBar(
                modifier = Modifier.fillMaxWidth(),
                onBackClicked = viewModel::onBackClicked
            )
        },
        bottomBar = {
            AddEntryBottomBar(
                modifier = Modifier
                    .padding(16.dp)
                    .height(56.dp)
                    .fillMaxWidth(),
                onSaveClicked = viewModel::onSaveClicked,
                enabled = isValid
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            DoItTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.title,
                onValueChange = viewModel::onTitleChanged,
                label = stringResource(id = R.string.add_entry_title_title),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            DoItTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.description,
                onValueChange = viewModel::onDescriptionChanged,
                label = stringResource(id = R.string.add_entry_description_title),
                maxLines = 5,
                minLines = 3
            )
        }
    }

    if (showDismissDialog) {
        AddEntryDismissDialog(
            onConfirm = {
                navigator.popBackStack()
            },
            onDismiss = {
                showDismissDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntryTopBar(
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(id = R.string.add_entry_title),
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
        }
    )
}

@Composable
fun AddEntryBottomBar(
    onSaveClicked: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = enabled,
        label = "SaveButtonEnableAnimation"
    ) { isEnabled ->
        Button(
            modifier = modifier,
            onClick = onSaveClicked,
            enabled = isEnabled,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = stringResource(id = R.string.general_save))
        }
    }
}

@Composable
fun AddEntryDismissDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        title = {
            Text(text = stringResource(id = R.string.add_entry_dismiss_dialog_title))
        },
        text = {
            Text(text = stringResource(id = R.string.add_entry_dismiss_dialog_text))
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.general_cancel))
            }
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = R.string.add_entry_dismiss_dialog_confirm))
            }
        }
    )
}