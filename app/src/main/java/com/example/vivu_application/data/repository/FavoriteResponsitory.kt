package com.example.vivu_application.data.repository

import android.util.Log
import com.example.vivu_application.data.model.FavoriteRequestBody
import com.example.vivu_application.data.model.FavoritesApiResponse
import com.example.vivu_application.data.network.AuthApiService
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

class FavoriteRepository(private val authApiService: AuthApiService) {

    private val gson = Gson() // Để parse error body nếu cần

    suspend fun getFavorites(page: Int, size: Int): Result<FavoritesApiResponse> {
        return try {
            val response = authApiService.getFavorites(page, size)
            if (response.isSuccessful && response.body() != null) {
                Log.d("FavoriteRepository", "Get favorites successful: Page $page, Size $size, Data: ${response.body()}")
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    try {
                        // Cố gắng parse error body nếu backend trả về JSON error message
                        // Ví dụ: val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                        // errorResponse.message
                        "Error ${response.code()}: $errorBody" // Hoặc chỉ errorBody
                    } catch (e: Exception) {
                        "Error ${response.code()}: ${response.message()} (Error body parsing failed)"
                    }
                } else {
                    "Error ${response.code()}: ${response.message()}"
                }
                Log.e("FavoriteRepository", "Get favorites failed: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: HttpException) {
            Log.e("FavoriteRepository", "Get favorites failed - HttpException: ${e.message()}", e)
            Result.failure(Exception("Network error (HTTP): ${e.message()}"))
        } catch (e: IOException) {
            Log.e("FavoriteRepository", "Get favorites failed - IOException: ${e.message}", e)
            Result.failure(Exception("Network error (IO): ${e.message}"))
        } catch (e: Exception) {
            Log.e("FavoriteRepository", "Get favorites failed - General Exception: ${e.message}", e)
            Result.failure(Exception("An unexpected error occurred: ${e.message}"))
        }
    }

    suspend fun addFavorite(favoriteType: String, favoriteId: String): Result<Unit> {
        val requestBody = FavoriteRequestBody(favoriteType = favoriteType, favoriteId = favoriteId)
        return try {
            val response = authApiService.addFavorite(requestBody)
            if (response.isSuccessful) {
                Log.d("FavoriteRepository", "Add favorite successful: Type $favoriteType, ID $favoriteId")
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = "Error ${response.code()}: ${errorBody ?: response.message()}"
                Log.e("FavoriteRepository", "Add favorite failed: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: HttpException) {
            Log.e("FavoriteRepository", "Add favorite failed - HttpException: ${e.message()}", e)
            Result.failure(Exception("Network error (HTTP): ${e.message()}"))
        } catch (e: IOException) {
            Log.e("FavoriteRepository", "Add favorite failed - IOException: ${e.message}", e)
            Result.failure(Exception("Network error (IO): ${e.message}"))
        } catch (e: Exception) {
            Log.e("FavoriteRepository", "Add favorite failed - General Exception: ${e.message}", e)
            Result.failure(Exception("An unexpected error occurred: ${e.message}"))
        }
    }

    suspend fun removeFavorite(favoriteType: String, favoriteId: String): Result<Unit> {
        val requestBody = FavoriteRequestBody(favoriteType = favoriteType, favoriteId = favoriteId)
        return try {
            val response = authApiService.removeFavorite(requestBody)
            if (response.isSuccessful) {
                Log.d("FavoriteRepository", "Remove favorite successful: Type $favoriteType, ID $favoriteId")
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = "Error ${response.code()}: ${errorBody ?: response.message()}"
                Log.e("FavoriteRepository", "Remove favorite failed: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: HttpException) {
            Log.e("FavoriteRepository", "Remove favorite failed - HttpException: ${e.message()}", e)
            Result.failure(Exception("Network error (HTTP): ${e.message()}"))
        } catch (e: IOException) {
            Log.e("FavoriteRepository", "Remove favorite failed - IOException: ${e.message}", e)
            Result.failure(Exception("Network error (IO): ${e.message}"))
        } catch (e: Exception) {
            Log.e("FavoriteRepository", "Remove favorite failed - General Exception: ${e.message}", e)
            Result.failure(Exception("An unexpected error occurred: ${e.message}"))
        }
    }
}