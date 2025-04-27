package com.example.vivu_application.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

// Sử dụng object nếu bạn muốn singleton đơn giản
object TokenManager {

    private const val PREF_NAME = "auth_prefs_encrypted"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"

    private var encryptedPrefs: SharedPreferences? = null

    // Phải gọi hàm này một lần, lý tưởng là từ Application class hoặc khi cần dùng lần đầu
    fun initialize(context: Context) {
        if (encryptedPrefs == null) {
            try {
                val masterKey = MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

                encryptedPrefs = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
                Log.i("TokenManager", "EncryptedSharedPreferences initialized successfully.")
            } catch (e: Exception) {
                Log.e("TokenManager", "Error initializing EncryptedSharedPreferences", e)
                // Xử lý lỗi: Có thể fallback về SharedPreferences thường (không an toàn) hoặc báo lỗi
                // Fallback ví dụ (KHÔNG KHUYẾN NGHỊ CHO TOKEN):
                // encryptedPrefs = context.getSharedPreferences(PREF_NAME + "_unencrypted", Context.MODE_PRIVATE)
            }
        }
    }

    // Đảm bảo SharedPreferences đã được khởi tạo
    private fun requirePrefs(): SharedPreferences {
        return encryptedPrefs ?: throw IllegalStateException(
            "TokenManager must be initialized before use. Call TokenManager.initialize(context)."
        )
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        try {
            requirePrefs().edit()
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .apply()
            Log.d("TokenManager", "Access and Refresh tokens saved.")
            // Log giá trị token chỉ khi debug, xóa đi trong production
//             Log.d("TokenManager", "Access Token saved: $accessToken")
//             Log.d("TokenManager", "Refresh Token saved: $refreshToken")
        } catch (e: Exception) {
            Log.e("TokenManager", "Error saving tokens", e)
        }
    }

    fun getAccessToken(): String? {
        return try {
            requirePrefs().getString(KEY_ACCESS_TOKEN, null)
        } catch (e: Exception) {
            Log.e("TokenManager", "Error getting access token", e)
            null
        }
    }

    fun getRefreshToken(): String? {
        return try {
            requirePrefs().getString(KEY_REFRESH_TOKEN, null)
        } catch (e: Exception) {
            Log.e("TokenManager", "Error getting refresh token", e)
            null
        }
    }

    fun clearTokens() {
        try {
            requirePrefs().edit()
                .remove(KEY_ACCESS_TOKEN)
                .remove(KEY_REFRESH_TOKEN)
                .apply()
            Log.d("TokenManager", "Tokens cleared.")
        } catch (e: Exception) {
            Log.e("TokenManager", "Error clearing tokens", e)
        }
    }
}
