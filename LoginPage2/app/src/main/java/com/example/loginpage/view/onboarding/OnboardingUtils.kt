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
        return sharedPreferences.getBoolean("login", false )
    }

    //Lưu trạng thái đăng nhập
    fun setLogIn(logIn: Boolean) {
        sharedPreferences.edit()
            .putBoolean("login", logIn)
            .apply()
    }
}