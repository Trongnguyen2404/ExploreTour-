package com.example.loginpage.view.onboarding


import android.content.Context
class OnboardingUtils( private val context: Context) {

    private val sharedPreferences = context.getSharedPreferences("onboarding", Context.MODE_PRIVATE)

    //Kiểm tra trạng thái hoàn thành onboarding
    fun isOnboardingCompleted(): Boolean {
        return sharedPreferences.getBoolean("completed", false)
    }

    //Lưu trang thái hoàn thành onboarding
    fun setOnboardingCompleted() {
        sharedPreferences.edit()
            .putBoolean("completed", true)
            .apply()
    }

    //Kiểm tra trạng thái đăng nhập
    fun isLogIn(): Boolean {
        return sharedPreferences.getBoolean("loginPage", false )
    }

    //Lưu trạng thái đăng nhập
    fun setLogIn() { // Thêm phương thức để lưu trạng thái đăng nhập
        sharedPreferences.edit().putBoolean("isLogIn", true).apply()
    }

    fun clearLogIn() { // Thêm phương thức để xóa trạng thái đăng nhập nếu cần (ví dụ: khi đăng xuất)
        sharedPreferences.edit().putBoolean("isLogIn", false).apply()
    }
}