package com.example.doit.ui.composables

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.doit.common.R

@Composable
fun TagListEntry(
    title: String,
    color: Color,
    modifier: Modifier = Modifier,
    amount: Int? = null,
    selected: Boolean = false,
    highlighted: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 8.dp
    )
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (highlighted) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        } else {
            Color.Unspecified
        },
        label = "TagSelectionAnimation"
    )

    Row(
        modifier = modifier
            .background(color = backgroundColor)
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Crossfade(
            targetState = selected,
            label = "LabelSelectionAnimation"
        ) { selected ->
            if (selected) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_label_24),
                    contentDescription = null,
                    tint = color
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.outline_label_24),
                    contentDescription = null,
                    tint = color
                )
            }
        }

        Text(
            modifier = Modifier.weight(1f),
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (amount != null) {
            Text(
                text = amount.toString(),
                style = MaterialTheme.typography.labelMedium,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

@Preview
@Composable
fun TagListEntryPreview() {
    TagListEntry(
        modifier = Modifier.fillMaxWidth(),
        title = "Tag1234",
        color = Color.Red,
        amount = 20
    )
}