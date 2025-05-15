package com.example.vivu_application.data.model
import com.google.gson.annotations.SerializedName

data class FavoriteRequestBody(
    @SerializedName("favoriteType")
    val favoriteType: String, // "TOUR" hoặc "LOCATION"
    @SerializedName("favoriteId")
    val favoriteId: String    // ID của Tour/Location (dưới dạng String như API yêu cầu)
)

data class ApiFavoriteItem(
    @SerializedName("favoriteType")
    val favoriteType: String,

    @SerializedName("favoriteId") // Khớp với JSON
    val itemId: Int, // Tên biến trong Kotlin có thể là itemId

    // SỬA Ở ĐÂY: Thay đổi @SerializedName để khớp với JSON
    @SerializedName("tour") // JSON key là "tour"
    val tour: Tour?,

    @SerializedName("location") // JSON key là "location"
    val location: Location?
)

data class FavoritesApiResponse(
    @SerializedName("content")
    val content: List<ApiFavoriteItem>,
    @SerializedName("pageNo")
    val pageNo: Int,
    @SerializedName("pageSize")
    val pageSize: Int,
    @SerializedName("totalElements")
    val totalElements: Long,
    @SerializedName("totalPages")
    val totalPages: Int,
    @SerializedName("last")
    val last: Boolean
)