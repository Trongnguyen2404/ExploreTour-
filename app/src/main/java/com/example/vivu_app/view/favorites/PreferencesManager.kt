package com.example.vivu_app.preferences

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class PreferencesManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val _favoritePosts = MutableStateFlow<Set<Int>>(
        sharedPreferences.getStringSet("favorites", emptySet())!!.map { it.toInt() }.toSet()
    )

    val favoritePosts: StateFlow<Set<Int>> = _favoritePosts

    suspend fun saveFavoritePosts(favorites: Set<Int>) {
        withContext(Dispatchers.IO) {
            _favoritePosts.value = favorites
            sharedPreferences.edit().putStringSet(
                "favorites", favorites.map { it.toString() }.toSet()
            ).apply()
        }
    }
}