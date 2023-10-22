package com.example.doit.domain.usecases

import com.example.doit.domain.repositories.TodoItemRepository
import com.example.doit.domain.usecases.interfaces.DeleteItemsByParentUseCase
import javax.inject.Inject

class DeleteItemsByParentUseCaseImpl @Inject constructor(
    private val repo: TodoItemRepository
) : DeleteItemsByParentUseCase {
    override suspend fun invoke(parent: String) {
        repo.getItems(parent).forEach { child ->
            invoke(child.id)
        }

        repo.deleteItemsByParent(parent)
    }
}