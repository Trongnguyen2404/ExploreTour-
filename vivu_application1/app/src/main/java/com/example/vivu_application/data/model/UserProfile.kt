package com.example.vivu_application.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data class đại diện cho thông tin hồ sơ người dùng được trả về từ API.
 * Các trường nên được đặt là nullable (`?`) để xử lý trường hợp dữ liệu có thể bị thiếu từ API.
 * Sử dụng @SerializedName để ánh xạ tên key trong JSON response với tên biến Kotlin nếu chúng khác nhau.
 */
data class UserProfile(

    // Username (có thể trùng với email hoặc là một giá trị riêng)
    @SerializedName("username") // Giả sử key JSON là "username"
    val username: String?,

    // Email của người dùng
    @SerializedName("email") // Giả sử key JSON là "email"
    val email: String?,

    // Tên đầy đủ của người dùng
    // Key này có thể là "fullName", "name", hoặc tương tự tùy thuộc vào API của bạn
    @SerializedName("fullName") // <-- KIỂM TRA LẠI KEY NÀY VỚI API CỦA BẠN
    val name: String?, // Sử dụng tên biến 'name' hoặc 'fullName' tùy ý, miễn là @SerializedName đúng

    // Ngày sinh của người dùng
    // API thường trả về định dạng "YYYY-MM-DD"
    @SerializedName("dateOfBirth") // <-- KIỂM TRA LẠI KEY NÀY VỚI API CỦA BẠN
    val dateOfBirth: String?,

    // Số điện thoại di động
    // Key này có thể là "phoneNumber", "mobile", hoặc tương tự
    @SerializedName("phoneNumber") // <-- KIỂM TRA LẠI KEY NÀY VỚI API CỦA BẠN
    val mobile: String?, // Sử dụng tên biến 'mobile' hoặc 'phoneNumber' tùy ý

    // URL dẫn đến ảnh đại diện của người dùng
    @SerializedName("profilePictureUrl") // <-- KIỂM TRA LẠI KEY NÀY VỚI API CỦA BẠN
    val profilePictureUrl: String?,

    // LƯU Ý: KHÔNG nên bao gồm mật khẩu (password) trong dữ liệu profile trả về từ API
)