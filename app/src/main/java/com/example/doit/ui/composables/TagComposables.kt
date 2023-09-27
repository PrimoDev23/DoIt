package com.example.doit.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.doit.domain.models.Tag

@Composable
fun TagItem(
    title: String,
    color: Color,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
    onClick: () -> Unit = {}
) {
    AssistChip(
        modifier = modifier,
        onClick = onClick,
        label = {
            Text(
                text = title,
                color = Color.Unspecified,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        shape = shape,
        colors = AssistChipDefaults.assistChipColors(
            labelColor = color.copy(alpha = 0.7f)
        ),
        border = AssistChipDefaults.assistChipBorder(
            borderColor = color.copy(alpha = 0.5f)
        )
    )

    /*Box(
        modifier = modifier
            .clip(shape)
            .background(
                color = color.copy(alpha = 0.3f),
                shape = shape
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            )
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
    }*/
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagBox(
    tags: List<Tag>,
    onTagClicked: (Tag) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.inverseOnSurface,
                shape = shape
            )
            .padding(16.dp)
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            tags.forEach { tag ->
                TagItem(
                    modifier = Modifier
                        .height(48.dp)
                        .widthIn(max = 200.dp),
                    title = tag.title,
                    color = tag.color,
                    onClick = {
                        onTagClicked(tag)
                    }
                )
            }
        }
    }
}