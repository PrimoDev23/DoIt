package com.example.doit.ui.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun InputTitle(
    text: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(bottom = 8.dp)
) {
    Text(
        modifier = Modifier
            .padding(contentPadding)
            .then(modifier),
        text = text,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Normal
    )
}