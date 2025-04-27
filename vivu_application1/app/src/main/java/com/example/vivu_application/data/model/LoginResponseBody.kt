package com.example.vivu_application.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data class đại diện cho cấu trúc JSON được trả về từ API đăng nhập thành công.
 * Các @SerializedName phải khớp chính xác với tên key trong JSON response.
 */
data class LoginResponseBody(

    // Key trong JSON là "token"
    @SerializedName("token")
    val accessToken: String?, // Giữ tên biến là accessToken cho rõ ràng

    // Key trong JSON là "type" (Thường là "Bearer")
    @SerializedName("type")
    val tokenType: String?, // Thêm biến này nếu bạn cần dùng (ví dụ: để thêm "Bearer " vào header)

    // Key trong JSON là "refreshToken"
    @SerializedName("refreshToken")
    val refreshToken: String?,

    // Key trong JSON là "id"
    @SerializedName("id")
    val id: Long?, // Kiểu Long vì thường là số nguyên lớn

    // Key trong JSON là "username"
    @SerializedName("username")
    val username: String?, // Có vẻ API đang trả về email trong trường username? Cần xem xét

    // Key trong JSON là "email"
    @SerializedName("email")
    val email: String?,

    // Key trong JSON là "roles" (là một danh sách các chuỗi)
    @SerializedName("roles")
    val roles: List<String>? // Kiểu List<String>

)