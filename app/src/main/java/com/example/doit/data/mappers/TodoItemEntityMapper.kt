package com.example.doit.data.mappers

import com.example.doit.data.models.local.TodoItemEntity
import com.example.doit.domain.models.TodoItem
import javax.inject.Inject

class TodoItemEntityMapper @Inject constructor(

) : BaseMapper<TodoItemEntity, TodoItem>() {
    override fun map(item: TodoItemEntity): TodoItem {
        return with(item) {
            TodoItem(
                id = id,
                title = title,
                description = description,
                done = done
            )
        }
    }

    override fun mapBack(item: TodoItem): TodoItemEntity {
        return with(item) {
            TodoItemEntity(
                id = id,
                title = title,
                description = description,
                done = done
            )
        }
    }
}