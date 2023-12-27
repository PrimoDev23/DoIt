package com.example.doit.domain.usecases

import com.example.doit.common.constants.WorkerConstants
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.repositories.SubtaskRepository
import com.example.doit.domain.repositories.TodoItemRepository
import com.example.doit.domain.usecases.interfaces.SaveTodoItemUseCase
import com.example.doit.domain.utils.interfaces.WorkScheduler
import java.time.LocalDateTime

class SaveTodoItemUseCaseImpl(
    private val workScheduler: WorkScheduler,
    private val todoItemRepository: TodoItemRepository,
    private val subtaskRepository: SubtaskRepository
) : SaveTodoItemUseCase {

    override suspend fun save(item: TodoItem) {
        todoItemRepository.saveTodoItem(item)
        subtaskRepository.saveSubtasksForParent(item.id, item.subtasks)

        workScheduler.cancelById(item.id)

        if (item.notificationDateTime != null) {
            val now = LocalDateTime.now()

            if (item.notificationDateTime.isAfter(now)) {
                workScheduler.cancelById(item.id)
                workScheduler.enqueueNotification(
                    item.id,
                    item.notificationDateTime,
                    mapOf(WorkerConstants.ITEM_ID_KEY to item.id)
                )
            }
        }
    }

}