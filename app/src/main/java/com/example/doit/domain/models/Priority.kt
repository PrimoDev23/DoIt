package com.example.doit.domain.models

import androidx.compose.ui.graphics.Color

enum class Priority(val color: Color) {
    NONE(Color(0xFF607D8B)),
    LOW(Color(0xFF2196F3)),
    MEDIUM(Color(0xFFFF9800)),
    HIGH(Color(0xFFF44336))
}