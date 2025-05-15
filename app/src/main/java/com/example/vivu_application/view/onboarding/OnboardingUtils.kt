package com.example.vivu_application.view.onboarding


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
        return sharedPreferences.getBoolean("login", false )
    }

    //Lưu trạng thái đăng nhập
    fun setLogIn(logIn: Boolean) {
        sharedPreferences.edit()
            .putBoolean("login", logIn)
            .apply()
    }

    // Đặt lại trạng thái đăng nhập về false (cho chức năng đăng xuất)
    fun setLogOut() {
        sharedPreferences.edit()
            .putBoolean("login", false)
            .apply()
    }

    // Kiểm tra trạng thái hoàn thành tạo tài khoản
    fun isAccountCreated(): Boolean {
        return sharedPreferences.getBoolean("account_created", false)
    }

    // Lưu trạng thái hoàn thành tạo tài khoản
    fun setAccountCreated(created: Boolean) {
        sharedPreferences.edit()
            .putBoolean("account_created", created)
            .apply()
    }
}