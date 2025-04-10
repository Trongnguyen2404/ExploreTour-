package com.example.vivu_app.data.local

import android.content.Context
import com.example.vivu_app.model.Comment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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


    // phần comment
    private val COMMENT_KEY = "comments_map"

    fun saveComments(postId: Int, comments: List<Comment>) {
        val allComments = getAllComments().toMutableMap()
        allComments[postId] = comments

        val jsonString = Json.encodeToString(allComments)
        sharedPreferences.edit().putString(COMMENT_KEY, jsonString).apply()
    }

    fun getComments(postId: Int): List<Comment> {
        return getAllComments()[postId] ?: emptyList()
    }

    private fun getAllComments(): Map<Int, List<Comment>> {
        val json = sharedPreferences.getString(COMMENT_KEY, null) ?: return emptyMap()
        return try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            emptyMap()
        }
    }
}