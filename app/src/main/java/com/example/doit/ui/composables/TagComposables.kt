package com.example.doit.ui.composables

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.doit.R
import com.example.doit.domain.models.Tag

@Composable
fun TagListEntry(
    tag: Tag,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 8.dp
    )
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) {
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
        verticalAlignment = Alignment.CenterVertically
    ) {
        Crossfade(
            targetState = tag.selected,
            label = "LabelSelectionAnimation"
        ) { selected ->
            if (selected) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_label_24),
                    contentDescription = null,
                    tint = tag.color
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.outline_label_24),
                    contentDescription = null,
                    tint = tag.color
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = tag.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}