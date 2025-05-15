package com.example.vivu_application.data.repository

import com.example.vivu_application.data.model.TourDetail
import com.example.vivu_application.data.model.TourResponse
import com.example.vivu_application.data.network.AuthApiService
import com.example.vivu_application.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TourRepository(
    private val apiService: AuthApiService = RetrofitClient.authApiService
) {

    // Lấy danh sách tour
    suspend fun getTours(
        page: Int,
        size: Int = 10,
        searchQuery: String? = null
    ): Result<TourResponse> {
        return try {
            val response = apiService.getTours(
                page = page,
                size = size,
                search = searchQuery
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("API Error (getTours): ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- THÊM MỚI: Lấy chi tiết tour ---
    suspend fun getTourDetail(tourId: Int): Result<TourDetail> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getTourDetail(tourId = tourId) // Gọi API chi tiết
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
    // --- KẾT THÚC THÊM MỚI ---
}