package com.example.doit.ui.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.doit.R
import com.example.doit.domain.models.Tag

@Composable
fun TagListEntry(
    tag: Tag,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 8.dp
    )
) {
    Row(
        modifier = modifier.padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedContent(
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