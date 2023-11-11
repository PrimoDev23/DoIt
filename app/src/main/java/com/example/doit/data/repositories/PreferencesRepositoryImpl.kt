package com.example.doit.data.repositories

import androidx.datastore.preferences.core.edit
import com.example.doit.data.AppPreferences
import com.example.doit.data.PreferencesKeys
import com.example.doit.domain.models.TodoItemSortType
import com.example.doit.domain.repositories.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(
    private val preferences: AppPreferences
) : PreferencesRepository {
    override fun getTodoItemSortTypeFlow(): Flow<TodoItemSortType> {
        return preferences.data.map { prefs ->
            prefs[PreferencesKeys.TODO_ITEM_SORT_TYPE]?.let {
                TodoItemSortType.valueOf(it)
            } ?: TodoItemSortType.ALPHABETICAL
        }
    }

    override suspend fun setTodoItemSortType(sortType: TodoItemSortType) {
        preferences.edit { prefs ->
            prefs[PreferencesKeys.TODO_ITEM_SORT_TYPE] = sortType.name
        }
    }
}