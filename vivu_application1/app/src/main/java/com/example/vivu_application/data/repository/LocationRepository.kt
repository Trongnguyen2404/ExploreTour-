package com.example.vivu_application.data.repository

import com.example.vivu_application.data.model.LocationDetail
import com.example.vivu_application.data.model.LocationResponse
import com.example.vivu_application.data.network.AuthApiService
import com.example.vivu_application.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocationRepository(
    private val apiService: AuthApiService = RetrofitClient.authApiService
) {

    // Lấy danh sách location
    suspend fun getLocations(page: Int): Result<LocationResponse> {
        // ... code giữ nguyên ...
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getLocations(page = page)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("API Error: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // --- THÊM MỚI: Lấy chi tiết location ---
    suspend fun getLocationDetail(locationId: Int): Result<LocationDetail> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getLocationDetail(locationId = locationId) // Gọi API chi tiết
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