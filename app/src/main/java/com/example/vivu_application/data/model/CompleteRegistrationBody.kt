package com.example.vivu_application.data.model // Or your package

data class CompleteRegistrationBody(
    val email: String,
    val otp: String,
    val username: String,
    val password: String,
    val repeatPassword: String // API requires this, send the same as password
)