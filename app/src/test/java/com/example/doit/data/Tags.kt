package com.example.doit.data

import androidx.compose.ui.graphics.Color
import com.example.doit.domain.models.Tag

object Tags {

    val tagList = listOf(
        Tag(
            id = 0L,
            title = "Tag1",
            color = Color.Red
        ),
        Tag(
            id = 1L,
            title = "Tag2",
            color = Color.Green
        )
    )

}