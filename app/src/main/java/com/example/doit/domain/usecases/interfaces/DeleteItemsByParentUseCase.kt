package com.example.doit.domain.usecases.interfaces

interface DeleteItemsByParentUseCase {
    suspend operator fun invoke(parent: String)
}