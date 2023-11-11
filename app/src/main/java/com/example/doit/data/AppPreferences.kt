package com.example.doit.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

typealias AppPreferences = DataStore<Preferences>

val Context.dataStore: AppPreferences by preferencesDataStore(
    name = "AppPreferences"
)

object PreferencesKeys {
    val TODO_ITEM_SORT_TYPE = stringPreferencesKey("todo_item_sort_type")
}