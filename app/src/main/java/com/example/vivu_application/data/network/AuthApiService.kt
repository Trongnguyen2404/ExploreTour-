package com.example.vivu_application.data.network // Or your package

import com.example.vivu_application.data.model.ChangePasswordRequestBody
import com.example.vivu_application.data.model.ChatRequestBody
import com.example.vivu_application.data.model.ChatResponseBody
import com.example.vivu_application.data.model.CompleteRegistrationBody // ++ ADD IMPORT ++
import com.example.vivu_application.data.model.FavoriteRequestBody
import com.example.vivu_application.data.model.FavoritesApiResponse
import com.example.vivu_application.data.model.LocationDetail
import com.example.vivu_application.data.model.LocationResponse
import com.example.vivu_application.data.model.RequestOtpBody
import com.example.vivu_application.data.model.VerifyOtpBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import com.example.vivu_application.data.model.LoginRequestBody
import com.example.vivu_application.data.model.LoginResponseBody
import com.example.vivu_application.data.model.RequestEmailChangeBody
import com.example.vivu_application.data.model.Review
import com.example.vivu_application.data.model.ReviewResponse
import com.example.vivu_application.data.model.SetNewPasswordBody
import com.example.vivu_application.data.model.SubmitReviewRequestBody
import com.example.vivu_application.data.model.TourDetail
import com.example.vivu_application.data.model.TourResponse
import com.example.vivu_application.data.model.VerifyEmailChangeBody
import com.example.vivu_application.data.model.VerifyPasswordRequestBody
import com.example.vivu_application.data.model.UpdateProfileRequestBody
import com.example.vivu_application.data.model.UserProfile
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import com.example.vivu_application.data.model.RefreshTokenRequestBody
import retrofit2.Call
import okhttp3.MultipartBody // ++ THÊM IMPORT NÀY ++
import okhttp3.RequestBody
import retrofit2.http.Multipart // ++ THÊM ANNOTATION NÀY ++
import retrofit2.http.Part


interface AuthApiService {

    @POST("api/v1/auth/register/request-otp")
    suspend fun requestOtp(
        @Body requestBody: RequestOtpBody
    ): Response<Unit>

    @POST("api/v1/auth/register/verify-otp")
    suspend fun verifyOtp(
        @Body requestBody: VerifyOtpBody
    ): Response<Unit> // Or Response<YourVerifyResponseData> if it returns data

    // ++ ADD THIS METHOD ++
    @POST("api/v1/auth/register/complete")
    suspend fun completeRegistration(
        @Body requestBody: CompleteRegistrationBody
    ): Response<Unit> // Assuming success is just a 2xx status code
    // Change Unit if API returns data (e.g., user info, token)

    @POST("api/v1/auth/login") // Thay bằng đường dẫn API login thực tế
    suspend fun login(@Body requestBody: LoginRequestBody): Response<LoginResponseBody> // Trả về LoginResponseBody

    @POST("api/v1/auth/logout")
    // API này CẦN được xác thực (Interceptor thêm Authorization header)
    // Nó thường không cần request body
    suspend fun logout(): Response<Unit> // Giả sử chỉ cần status thành công

    @POST("api/v1/auth/forgot-password")
    suspend fun requestPasswordResetOtp(
        @Body requestBody: RequestOtpBody // Dùng lại RequestOtpBody vì chỉ cần email
    ): Response<Unit> // Giả sử API chỉ trả về status thành công, không có body
    // Nếu API trả về gì đó, thay Unit bằng data class tương ứng

    @POST("api/v1/auth/forgot-password/verify-otp")
    suspend fun verifyPasswordResetOtp(
        @Body requestBody: VerifyOtpBody // Dùng lại VerifyOtpBody
    ): Response<Unit> // Giả sử chỉ trả về status thành công.
    // Nếu API trả về token xác thực tạm thời cho bước đặt lại MK,
    // hãy tạo data class và thay Unit bằng Response<YourVerificationTokenData>

    @POST("api/v1/auth/forgot-password/set-new-password")
    suspend fun setNewPassword(
        @Body requestBody: SetNewPasswordBody
    ): Response<Unit> // Giả sử chỉ trả về status thành công

    @POST("api/v1/auth/verify-password-for-email-change")
    suspend fun verifyPasswordForEmailChange(
        @Body requestBody: VerifyPasswordRequestBody
    ): Response<Unit> // Giả sử chỉ cần status thành công

    @POST("api/v1/auth/request-email-change")
    suspend fun requestEmailChange(
        @Body requestBody: RequestEmailChangeBody
    ): Response<Unit> // Giả sử chỉ cần status thành công (ngụ ý OTP đã gửi đến email MỚI)

    @POST("api/v1/auth/verify-email-change")
    suspend fun verifyEmailChange(
        @Body requestBody: VerifyEmailChangeBody
    ): Response<Unit> // Giả sử chỉ cần status thành công

    @POST("api/v1/auth/change-password") // Hoặc @PUT nếu backend dùng PUT
    suspend fun changePassword(
        @Body requestBody: ChangePasswordRequestBody
    ): Response<Unit> // Giả sử chỉ cần status thành công

    @GET("api/v1/users/profile")
    suspend fun getUserProfile(): Response<UserProfile>

    // ++ THÊM HÀM NÀY ++
    @PUT("api/v1/users/profile") // Hoặc @PATCH
    // API này CẦN xác thực (Interceptor)
    suspend fun updateUserProfile(
        @Body requestBody: UpdateProfileRequestBody
    ): Response<UserProfile> // Giả sử trả về profile đã cập nhật
    // Hoặc Response<Unit> nếu không trả về body


    @POST("api/v1/chatbot/message")
    // AuthInterceptor sẽ tự động thêm header Authorization
    suspend fun sendChatMessage(
        @Body requestBody: ChatRequestBody
    ): Response<ChatResponseBody> // Sử dụng các data class mới


    @DELETE("api/v1/chatbot/me/chatbot-history") // Hoặc @POST nếu backend yêu cầu
    suspend fun clearChatHistory(): Response<Unit> // Giả sử chỉ cần status thành công

    // --- API Danh sách ---
    @GET("api/v1/tours")
    suspend fun getTours(
        @Query("page") page: Int,
        @Query("size") size: Int = 10,
        @Query("search") search: String?
    ): Response<TourResponse>

    @GET("api/v1/locations")
    suspend fun getLocations(
        @Query("page") page: Int,
        @Query("size") size: Int = 10,
        @Query("search") search: String?
    ): Response<LocationResponse>

    // --- API Chi tiết (THÊM MỚI) ---
    @GET("api/v1/tours/{id}") // Đường dẫn với Path parameter {id}
    suspend fun getTourDetail(
        @Path("id") tourId: Int // Truyền ID vào Path
    ): Response<TourDetail> // Trả về TourDetail

    @GET("api/v1/locations/{id}") // Đường dẫn với Path parameter {id}
    suspend fun getLocationDetail(
        @Path("id") locationId: Int // Truyền ID vào Path
    ): Response<LocationDetail> // Trả về LocationDetail

    @GET("api/v1/reviews")
    suspend fun getReviews(
        @Query("targetType") targetType: String, // "TOUR" hoặc "LOCATION"
        @Query("targetId") targetId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int = 5 // Lấy 5 review mỗi lần (hoặc tùy chỉnh)
    ): Response<ReviewResponse>

    // --- API Favorites ---
    @GET("api/v1/favorites")
    suspend fun getFavorites(
        @Query("page") page: Int,
        @Query("size") size: Int = 10 // Bạn có thể điều chỉnh size mặc định
    ): Response<FavoritesApiResponse>

    @POST("api/v1/favorites")
    suspend fun addFavorite(
        @Body favoriteRequestBody: FavoriteRequestBody
    ): Response<Unit> // Giả sử API chỉ trả về status code thành công, không có body

    // API để xóa favorite, sử dụng DELETE method và có request body
    @HTTP(method = "DELETE", path = "api/v1/favorites", hasBody = true)
    suspend fun removeFavorite(
        @Body favoriteRequestBody: FavoriteRequestBody
    ): Response<Unit> // Giả sử API chỉ trả về status code thành công, không có body

    @POST("api/v1/reviews")
    suspend fun submitReview(
        @Body requestBody: SubmitReviewRequestBody
    ): Response<Review> // Giả sử API trả về đối tượng Review vừa được tạo khi thành công
    // Nếu API chỉ trả về status code (không có body), hãy dùng Response<Unit>

    @POST("api/v1/auth/refresh-token") // Hoặc endpoint refresh token của bạn
    fun refreshToken(@Body requestBody: RefreshTokenRequestBody): Call<LoginResponseBody>

}