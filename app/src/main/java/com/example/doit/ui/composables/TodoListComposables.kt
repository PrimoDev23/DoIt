package com.example.doit.ui.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.doit.R
import com.example.doit.ui.composables.destinations.AddEntryScreenDestination
import com.example.doit.ui.viewmodels.TodoListViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RootNavGraph(start = true)
@Destination
@Composable
fun TodoListScreen(
    navigator: DestinationsNavigator,
    viewModel: TodoListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TodoListTopBar(modifier = Modifier.fillMaxWidth())
        },
        floatingActionButton = {
            TodoListFloatingActionButton(
                onClick = {
                    navigator.navigate(AddEntryScreenDestination)
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = state.items,
                key = { item ->
                    item.id
                }
            ) { item ->
                TodoItemListEntry(
                    modifier = Modifier.fillMaxWidth(),
                    done = item.done,
                    onDoneChanged = { done ->
                        viewModel.onDoneChanged(item, done)
                    },
                    title = item.title,
                    description = item.description,
                    onClick = {}
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListTopBar(
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(id = R.string.todo_list_title),
                fontWeight = FontWeight.Bold
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoItemListEntry(
    done: Boolean,
    onDoneChanged: (Boolean) -> Unit,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    top = 16.dp,
                    end = 8.dp,
                    bottom = 16.dp
                )
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
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