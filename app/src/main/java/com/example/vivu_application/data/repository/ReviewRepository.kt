package com.example.vivu_application.data.repository

import android.util.Log
import com.example.vivu_application.data.model.Review
import com.example.vivu_application.data.model.ReviewResponse
import com.example.vivu_application.data.model.SubmitReviewRequestBody
import com.example.vivu_application.data.network.AuthApiService
import com.example.vivu_application.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import retrofit2.HttpException
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
    suspend fun submitNewReview(
        targetType: String,
        targetId: Int,
        rating: Int,
        comment: String
    ): Result<Review> { // Giả sử API trả về Review, nếu không thì Result<Unit>
        val requestBody = SubmitReviewRequestBody(
            targetType = targetType,
            targetId = targetId,
            rating = rating,
            comment = comment
        )
        return withContext(Dispatchers.IO) {
            try {
                Log.d("ReviewRepository", "Submitting review: $requestBody")
                val response = apiService.submitReview(requestBody)
                if (response.isSuccessful && response.body() != null) {
                    Log.d("ReviewRepository", "Submit review successful: ${response.body()}")
                    Result.success(response.body()!!) // Trả về đối tượng Review mới
                } else {
                    val errorMsg = "API Error (submitReview): ${response.code()} - ${response.message()} - ${response.errorBody()?.string()}"
                    Log.e("ReviewRepository", errorMsg)
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: HttpException) {
                Log.e("ReviewRepository", "Submit review failed - HttpException: ${e.message}", e)
                Result.failure(Exception("Network error (HTTP - submitReview): ${e.message}"))
            } catch (e: IOException) {
                Log.e("ReviewRepository", "Submit review failed - IOException: ${e.message}", e)
                Result.failure(Exception("Network error (IO - submitReview): ${e.message}"))
            } catch (e: Exception) {
                Log.e("ReviewRepository", "Submit review failed - General Exception: ${e.message}", e)
                Result.failure(e)
            }
        }
    }
}