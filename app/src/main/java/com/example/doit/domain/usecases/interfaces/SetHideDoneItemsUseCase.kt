package com.example.doit.domain.usecases.interfaces

interface SetHideDoneItemsUseCase {
    suspend operator fun invoke(hide: Boolean)
}