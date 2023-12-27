package com.example.doit.data.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.doit.data.AppPreferences
import com.example.doit.data.models.local.TodoListPreferences
import com.example.doit.domain.models.TodoItemSortType
import com.example.doit.domain.preferences.TodoListPrefs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TodoListPrefsImpl(
    private val preferences: AppPreferences
) : TodoListPrefs {

    override fun getFlow(): Flow<TodoListPreferences> {
        return preferences.data.map { prefs ->
            val sortType = prefs[TODO_ITEM_SORT_TYPE]?.let {
                TodoItemSortType.valueOf(it)
            } ?: TodoItemSortType.CREATION_DATE
            val hideDoneItems = prefs[HIDE_DONE_ITEMS] ?: false

            TodoListPreferences(
                sortType = sortType,
                hideDoneItems = hideDoneItems
            )
        }
    }

    override suspend fun setSortType(sortType: TodoItemSortType) {
        preferences.edit { prefs ->
            prefs[TODO_ITEM_SORT_TYPE] = sortType.name
        }
    }

    override suspend fun setHideDoneItems(hide: Boolean) {
        preferences.edit { prefs ->
            prefs[HIDE_DONE_ITEMS] = hide
        }
    }

    companion object {
        private val TODO_ITEM_SORT_TYPE = stringPreferencesKey("todo_item_sort_type")
        private val HIDE_DONE_ITEMS = booleanPreferencesKey("hide_done_items")
    }
}