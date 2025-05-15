package com.example.vivu_application.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Dùng IP thật cho real device (không đổi)
    private const val BASE_URL = "http://192.168.102.6:8080/" // Nhớ dấu / ở cuối
    public val Api = BASE_URL;
    // Logging mức BASIC để tránh lag do log quá nhiều
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC // Hoặc NONE nếu release
    }

    private val authInterceptor = AuthInterceptor()
    // ++ TẠO INSTANCE CỦA TokenAuthenticator ++
    private val tokenAuthenticator = TokenAuthenticator() // Nếu TokenAuthenticator cần Context, bạn cần truyền vào đây

    // Cấu hình OkHttpClient
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
