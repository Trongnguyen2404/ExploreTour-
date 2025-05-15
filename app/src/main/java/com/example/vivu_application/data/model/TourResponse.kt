package com.example.vivu_application.data.model

data class TourResponse(
    val content: List<Tour>,
    val pageNo: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
)