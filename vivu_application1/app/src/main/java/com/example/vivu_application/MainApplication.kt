package com.example.vivu_application

import android.app.Application
import com.example.vivu_application.data.local.TokenManager

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Khởi tạo TokenManager khi ứng dụng khởi chạy
        TokenManager.initialize(applicationContext)
    }
}