package com.example.vivu_application.data.model

import com.google.gson.annotations.SerializedName

data class RefreshTokenRequestBody(
    @SerializedName("refreshToken") // Key backend cần cho refresh token
    val refreshToken: String
)