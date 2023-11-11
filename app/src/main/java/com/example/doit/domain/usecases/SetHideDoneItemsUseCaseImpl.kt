package com.example.doit.domain.usecases

import com.example.doit.domain.repositories.PreferencesRepository
import com.example.doit.domain.usecases.interfaces.SetHideDoneItemsUseCase
import javax.inject.Inject

class SetHideDoneItemsUseCaseImpl @Inject constructor(
    private val repo: PreferencesRepository
) : SetHideDoneItemsUseCase {
    override suspend fun invoke(hide: Boolean) {
        repo.setHideDoneItems(hide)
    }
}