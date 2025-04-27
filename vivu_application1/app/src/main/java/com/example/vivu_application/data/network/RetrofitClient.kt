package com.example.vivu_application.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // ✅ Dùng IP thật cho real device (không đổi)
    private const val BASE_URL = "http://172.20.10.3:8080/" // Nhớ dấu / ở cuối

    // 👇 Logging mức BASIC để tránh lag do log quá nhiều
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC // Hoặc NONE nếu release
    }

    // 👇 Cấu hình OkHttpClient
    private val okHttpClient = OkHttpClient.Builder()
        .retryOnConnectionFailure(true) // Nếu mạng yếu, tự thử lại
        .addInterceptor(AuthInterceptor()) // Gắn token tự động
        .addInterceptor(loggingInterceptor) // Ghi log
        .connectTimeout(30, TimeUnit.SECONDS) // Timeout đủ dài
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // 👇 Retrofit build với client đã config
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // 👇 Sử dụng AuthApiService
    val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }
}
