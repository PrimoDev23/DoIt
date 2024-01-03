package com.example.doit.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.PersistentList

@Composable
fun <T> VerticalGrid(
    columns: Int,
    items: PersistentList<T>,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    item: @Composable (T) -> Unit
) {
    val chunkedItems = remember(items, columns) {
        items.chunked(columns)
    }

    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement
    ) {
        for (chunk in chunkedItems) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = horizontalArrangement
            ) {
                for (column in 0 until columns) {
                    if (column <= chunk.lastIndex) {
                        Box(modifier = Modifier.weight(1f)) {
                            item(chunk[column])
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}