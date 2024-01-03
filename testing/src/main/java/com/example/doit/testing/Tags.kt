package com.example.doit.testing

import androidx.compose.ui.graphics.Color
import com.example.doit.domain.models.Tag
import kotlinx.collections.immutable.persistentListOf

object Tags {

    val tagOne = Tag(
        id = 0L,
        title = "Tag1",
        color = Color.Red
    )

    val tagTwo = Tag(
        id = 1L,
        title = "Tag2",
        color = Color.Green
    )

    val tagList = persistentListOf(
        tagOne,
        tagTwo
    )

}