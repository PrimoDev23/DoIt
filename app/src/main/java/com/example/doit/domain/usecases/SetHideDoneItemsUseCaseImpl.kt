package com.example.doit.domain.usecases

import com.example.doit.domain.preferences.TodoListPrefs
import com.example.doit.domain.usecases.interfaces.SetHideDoneItemsUseCase

class SetHideDoneItemsUseCaseImpl(
    private val repo: TodoListPrefs
) : SetHideDoneItemsUseCase {
    override suspend fun invoke(hide: Boolean) {
        repo.setHideDoneItems(hide)
    }
}