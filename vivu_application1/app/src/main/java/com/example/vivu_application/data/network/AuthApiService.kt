package com.example.vivu_application.data.network // Or your package

import com.example.vivu_application.data.model.ChangePasswordRequestBody
import com.example.vivu_application.data.model.ChatRequestBody
import com.example.vivu_application.data.model.ChatResponseBody
import com.example.vivu_application.data.model.CompleteRegistrationBody // ++ ADD IMPORT ++
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
import com.example.vivu_application.data.model.ReviewResponse
import com.example.vivu_application.data.model.SetNewPasswordBody
import com.example.vivu_application.data.model.TourDetail
import com.example.vivu_application.data.model.TourResponse
import com.example.vivu_application.data.model.VerifyEmailChangeBody
import com.example.vivu_application.data.model.VerifyPasswordRequestBody
import com.example.vivu_application.data.model.UpdateProfileRequestBody
import com.example.vivu_application.data.model.UserProfile
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


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

    // --- API Danh sách ---
    @GET("api/v1/tours")
    suspend fun getTours(
        @Query("page") page: Int,
        @Query("size") size: Int = 10
    ): Response<TourResponse>

    @GET("api/v1/locations")
    suspend fun getLocations(
        @Query("page") page: Int,
        @Query("size") size: Int = 10
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
    // --- KẾT THÚC THÊM MỚI ---
}