package com.example.doit.domain.usecases

import com.example.doit.domain.repositories.PreferencesRepository
import com.example.doit.domain.usecases.interfaces.SetHideDoneItemsUseCase

class SetHideDoneItemsUseCaseImpl(
    private val repo: PreferencesRepository
) : SetHideDoneItemsUseCase {
    override suspend fun invoke(hide: Boolean) {
        repo.setHideDoneItems(hide)
    }
}