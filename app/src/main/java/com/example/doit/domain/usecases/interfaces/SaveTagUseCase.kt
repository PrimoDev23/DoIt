package com.example.doit.domain.usecases.interfaces

import androidx.compose.ui.graphics.Color

interface SaveTagUseCase {
    suspend fun save(title: String, color: Color)
}