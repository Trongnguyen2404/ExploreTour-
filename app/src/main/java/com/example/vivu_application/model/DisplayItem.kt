package com.example.vivu_application.model

import com.example.vivu_application.data.model.Location
import com.example.vivu_application.data.model.Tour

// Interface chung để biểu diễn một item trong danh sách (Tour hoặc Location)
sealed interface DisplayItem {
    val id: Int // ID chung để làm key và cho chức năng yêu thích (nếu cần)

    data class TourItem(val tour: Tour) : DisplayItem {
        override val id: Int get() = tour.id
    }

    data class LocationItem(val location: Location) : DisplayItem {
        override val id: Int get() = location.id
    }
}