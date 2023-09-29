package com.example.doit.domain.models

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.example.doit.R

enum class Priority(
    val color: Color,
    @StringRes val title: Int
) {
    NONE(Color(0xFF607D8B), R.string.general_priority_none),
    LOW(Color(0xFF2196F3), R.string.general_priority_low),
    MEDIUM(Color(0xFFFF9800), R.string.general_priority_medium),
    HIGH(Color(0xFFF44336), R.string.general_priority_high)
}