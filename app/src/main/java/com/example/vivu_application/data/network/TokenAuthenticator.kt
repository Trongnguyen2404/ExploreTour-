package com.example.vivu_application.data.network

import android.util.Log
import com.example.vivu_application.data.local.TokenManager
import com.example.vivu_application.data.model.RefreshTokenRequestBody
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.io.IOException

class TokenAuthenticator(
    // Cần truyền vào context hoặc một cách để lấy ApiService
    // Cách đơn giản là truyền thẳng ApiService (nhưng cẩn thận vòng lặp dependency nếu ApiService dùng RetrofitClient)
    // Một cách tốt hơn là dùng Dagger/Hilt để inject
    // Tạm thời, chúng ta sẽ tạo một instance Retrofit riêng cho việc refresh để tránh vòng lặp
    // private val context: Context // Nếu muốn tạo RetrofitClient mới ở đây
) : Authenticator {

    companion object {
        private const val TAG = "TokenAuthenticator"
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        Log.d(TAG, "Authentication required. Response code: ${response.code}")

        // 1. Lấy refresh token hiện tại
        val currentRefreshToken = TokenManager.getRefreshToken()
        if (currentRefreshToken.isNullOrBlank()) {
            Log.w(TAG, "No refresh token available. Cannot refresh.")
            // Không có refresh token, không thể làm gì -> logout hoặc trả về null
            // Trigger logout ở đây nếu cần (ví dụ: gửi event, cập nhật state chung)
            TokenManager.clearTokens() // Xóa token (nếu có) để chắc chắn
            return null // OkHttp sẽ trả về lỗi 401 gốc
        }

        // 2. Đồng bộ hóa để tránh nhiều thread cùng lúc gọi refresh
        synchronized(this) {
            // Kiểm tra lại xem access token có được refresh bởi thread khác chưa
            val newAccessTokenAfterSync = TokenManager.getAccessToken()
            if (response.request.header("Authorization") == "Bearer $newAccessTokenAfterSync") {
                Log.d(TAG, "Access token was already refreshed by another thread. Retrying with new token.")
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $newAccessTokenAfterSync")
                    .build()
            }

            // 3. Gọi API refresh token (ĐỒNG BỘ)
            Log.d(TAG, "Attempting to refresh token with: $currentRefreshToken")
            try {
                // Tạo một Retrofit client riêng cho việc refresh để tránh vòng lặp interceptor
                // Hoặc đảm bảo API refresh không cần qua AuthInterceptor (vd: đánh dấu bằng header đặc biệt)
                // Đây là cách đơn giản, nhưng Dagger/Hilt sẽ tốt hơn
                val refreshApiService = RetrofitClient.authApiService // Giả sử RetrofitClient đã có sẵn

                val call = refreshApiService.refreshToken(RefreshTokenRequestBody(currentRefreshToken))
                val refreshResponse = call.execute() // Gọi ĐỒNG BỘ

                if (refreshResponse.isSuccessful) {
                    val loginResponseBody = refreshResponse.body()
                    val newAccessToken = loginResponseBody?.accessToken
                    val newRefreshToken = loginResponseBody?.refreshToken // Backend có thể trả về RT mới

                    if (!newAccessToken.isNullOrBlank()) {
                        Log.i(TAG, "Token refreshed successfully. New access token acquired.")
                        // Lưu token mới
                        if (!newRefreshToken.isNullOrBlank()) {
                            TokenManager.saveTokens(newAccessToken, newRefreshToken)
                            Log.d(TAG, "Both new access and refresh tokens saved.")
                        } else {
                            // Nếu backend không trả RT mới, chỉ lưu AT mới (RT cũ vẫn dùng được)
                            TokenManager.saveTokens(newAccessToken, currentRefreshToken)
                            Log.d(TAG, "New access token saved, old refresh token retained.")
                        }

                        // Tạo lại yêu cầu ban đầu với access token mới
                        return response.request.newBuilder()
                            .header("Authorization", "Bearer $newAccessToken")
                            .build()
                    } else {
                        Log.e(TAG, "Refresh API successful but new access token is missing.")
                    }
                } else {
                    // Lỗi khi gọi API refresh (ví dụ: refresh token hết hạn hoặc không hợp lệ)
                    Log.e(TAG, "Failed to refresh token. Code: ${refreshResponse.code()}, Message: ${refreshResponse.message()}, Body: ${refreshResponse.errorBody()?.string()}")
                    // Nếu refresh thất bại, xóa token và yêu cầu đăng nhập lại
                    TokenManager.clearTokens()
                    // TODO: Thông báo cho người dùng cần đăng nhập lại (ví dụ: qua EventBus, SharedFlow)
                    // Hoặc để lỗi 401 gốc được truyền lên UI
                }
            } catch (e: IOException) {
                Log.e(TAG, "IOException during token refresh: ${e.message}", e)
                // Lỗi mạng khi refresh
            } catch (e: Exception) {
                Log.e(TAG, "Generic exception during token refresh: ${e.message}", e)
            }
            // Nếu không thể refresh, trả về null để OkHttp trả lỗi 401 gốc
            return null
        }
    }
}