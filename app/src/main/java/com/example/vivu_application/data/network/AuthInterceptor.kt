    package com.example.vivu_application.data.network


    import okhttp3.Interceptor
    import okhttp3.Response
    import android.util.Log
    import com.example.vivu_application.data.local.TokenManager

    class AuthInterceptor : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            // Lấy request gốc
            val originalRequest = chain.request()
            Log.d("AuthInterceptor", "Intercepting request for URL: ${originalRequest.url}")

            // Lấy access token từ TokenManager
            val accessToken = TokenManager.getAccessToken() // Gọi hàm lấy token đã lưu

            // Nếu có token, thêm header Authorization
            if (accessToken != null && accessToken.isNotBlank()) {
                Log.d("AuthInterceptor", "Access token found. Adding Authorization header.")
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $accessToken") // Thêm header
                    .build()
                // Thực hiện request mới với header đã thêm
                return chain.proceed(newRequest)
            } else {
                Log.w("AuthInterceptor", "Access token is null or blank. Proceeding without Authorization header.")
                // Nếu không có token (ví dụ: chưa đăng nhập), thực hiện request gốc
                return chain.proceed(originalRequest)
            }
        }
    }