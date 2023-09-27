package com.example.doit.ui.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.doit.R
import com.example.doit.ui.composables.modifiers.softClickable
import com.example.doit.ui.viewmodels.TagListViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalLayoutApi::class)
@RootNavGraph
@Destination
@Composable
fun TagListScreen(
    navigator: DestinationsNavigator,
    onMenuClicked: () -> Unit,
    viewModel: TagListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var createDialogVisible by remember {
        mutableStateOf(false)
    }

    RootScaffold(
        modifier = Modifier.fillMaxSize(),
        onMenuClicked = onMenuClicked,
        title = stringResource(id = R.string.tag_overview_title),
        floatingActionButton = {
            TagListFloatingActionButton(
                onClick = {
                    createDialogVisible = true
                }
            )
        }
    ) {
        FlowRow(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            state.items.forEach { item ->
                TagItem(
                    modifier = Modifier
                        .height(48.dp)
                        .widthIn(max = 200.dp),
                    title = item.title,
                    color = item.color
                )
            }
        }
    }

    if (createDialogVisible) {
        val config = LocalConfiguration.current
        val maxHeight = remember {
            (config.screenHeightDp / 2f).dp
        }

        CreateTagDialog(
            modifier = Modifier.heightIn(max = maxHeight),
            onDismiss = {
                createDialogVisible = false
            },
            onConfirm = { title, color ->
                viewModel.onTagSaved(title, color)
            }
        )
    }
}

@Composable
fun TagListFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_add_24),
            contentDescription = stringResource(id = R.string.tag_list_fab)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateTagDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Color) -> Unit,
    modifier: Modifier = Modifier,
    colors: List<Color> = remember {
        listOf(
            Color(0xFFF44336),
            Color(0xFFE91E63),
            Color(0xFF9C27B0),
            Color(0xFF673AB7),
            Color(0xFF3F51B5),
            Color(0xFF2196F3),
            Color(0xFF03A9F4),
            Color(0xFF00BCD4),
            Color(0xFF009688),
            Color(0xFF4CAF50),
            Color(0xFF8BC34A),
            Color(0xFFCDDC39),
            Color(0xFFFFEB3B),
            Color(0xFFFFC107),
            Color(0xFFFF5722),
            Color(0xFF795548),
            Color(0xFF9E9E9E),

            Color(0xFFE53935),
            Color(0xFFD81B60),
            Color(0xFF8E24AA),
            Color(0xFF5E35B1),
            Color(0xFF3949AB),
            Color(0xFF1E88E5),
            Color(0xFF039BE5),
            Color(0xFF00ACC1),
            Color(0xFF00897B),
            Color(0xFF43A047),
            Color(0xFF7CB342),
            Color(0xFFC0CA33),
            Color(0xFFFDD835),
            Color(0xFFFFB300),
            Color(0xFFFB8C00),
            Color(0xFFF4511E),
            Color(0xFF6D4C41),
        )
    }
) {
    require(colors.isNotEmpty()) {
        "colors needs to have at least one item"
    }

    var tagName by remember {
        mutableStateOf("")
    }
    var tagColor by remember {
        mutableStateOf(colors.first())
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = modifier) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.create_tag_dialog_title),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(state = rememberScrollState())
                ) {
                    DoItTextField(
                        label = stringResource(id = R.string.create_tag_dialog_name_title),
                        value = tagName,
                        onValueChange = {
                            tagName = it
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(id = R.string.create_tag_dialog_colors_title),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        maxItemsInEachRow = 6,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        colors.forEach { color ->
                            CreateTagColorBubble(
                                modifier = Modifier.size(48.dp),
                                color = color,
                                selected = tagColor == color,
                                onClick = {
                                    tagColor = it
                                }
                            )
                        }
                    }
                }

                Row(modifier = Modifier.align(Alignment.End)) {
                    val saveEnabled by remember {
                        derivedStateOf {
                            tagName.isNotEmpty()
                        }
                    }

                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(id = R.string.general_cancel))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    AnimatedContent(
                        targetState = saveEnabled,
                        label = "SaveEnabledAnimation"
                    ) { isSaveEnabled ->
                        TextButton(
                            enabled = isSaveEnabled,
                            onClick = {
                                onConfirm(tagName, tagColor)
                                onDismiss()
                            }
                        ) {
                            Text(text = stringResource(id = R.string.general_save))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateTagColorBubble(
    color: Color,
    selected: Boolean,
    onClick: (Color) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape
) {
    Box(
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .then(modifier)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface,
                shape = shape
            )
            .background(
                color = color,
                shape = shape
            )
            .clip(shape)
            .softClickable {
                onClick(color)
            },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = selected,
            label = "CheckmarkAnimation"
        ) {
            Icon(
                modifier = Modifier.fillMaxSize(0.6f),
                painter = painterResource(id = R.drawable.baseline_check_24),
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}