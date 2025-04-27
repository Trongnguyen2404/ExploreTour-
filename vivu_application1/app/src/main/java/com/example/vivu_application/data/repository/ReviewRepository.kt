package com.example.vivu_application.data.repository

import com.example.vivu_application.data.model.ReviewResponse
import com.example.vivu_application.data.network.AuthApiService
import com.example.vivu_application.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReviewRepository(
    private val apiService: AuthApiService = RetrofitClient.authApiService
) {

    // Lấy danh sách review từ API
    suspend fun getReviews(
        targetType: String,
        targetId: Int,
        page: Int,
        size: Int = 5 // Kích thước trang mặc định
    ): Result<ReviewResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getReviews(
                    targetType = targetType,
                    targetId = targetId,
                    page = page,
                    size = size
                )
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!) // Thành công
                } else {
                    // Lỗi từ server
                    Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                // Lỗi mạng hoặc lỗi khác
                Result.failure(e)
            }
        }
    }
}