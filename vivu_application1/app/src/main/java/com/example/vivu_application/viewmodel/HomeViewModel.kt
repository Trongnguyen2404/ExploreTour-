package com.example.vivu_application.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// Import các model chi tiết
import com.example.vivu_application.data.model.LocationDetail
import com.example.vivu_application.data.model.Review // Import Review model
import com.example.vivu_application.data.model.TourDetail
// Import các repository và model cũ
import com.example.vivu_application.data.repository.LocationRepository
import com.example.vivu_application.data.repository.ReviewRepository // Import ReviewRepository
import com.example.vivu_application.data.repository.TourRepository
import com.example.vivu_application.model.DisplayItem // Import DisplayItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// State cho danh sách Home
data class HomeListUiState(
    val items: List<DisplayItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategory: String = "location",
    val currentPage: Int = 0,
    val isLastPage: Boolean = false
)

// State cho màn hình chi tiết (Dữ liệu chính của Tour/Location)
sealed class DetailData {
    object Loading : DetailData() // Trạng thái đang tải chi tiết chính
    data class TourSuccess(val tourDetail: TourDetail) : DetailData()
    data class LocationSuccess(val locationDetail: LocationDetail) : DetailData()
    data class Error(val message: String) : DetailData() // Lỗi tải chi tiết chính
    object Idle : DetailData() // Trạng thái chưa tải hoặc đã clear
}

data class DetailScreenUiState(
    val detailData: DetailData = DetailData.Idle
)

// State cho danh sách Reviews (trong màn hình chi tiết)
data class ReviewUiState(
    val reviews: List<Review> = emptyList(),
    val isLoading: Boolean = false, // Loading riêng cho reviews
    val error: String? = null,      // Lỗi riêng cho reviews
    val currentPage: Int = 0,
    val isLastPage: Boolean = false,
    val targetType: String? = null, // Lưu loại target của reviews
    val targetId: Int? = null      // Lưu ID target của reviews
)

class HomeViewModel(
    // Inject các repositories
    private val tourRepository: TourRepository = TourRepository(),
    private val locationRepository: LocationRepository = LocationRepository(),
    private val reviewRepository: ReviewRepository = ReviewRepository() // Thêm ReviewRepository
) : ViewModel() {

    // StateFlow cho màn hình danh sách (Home)
    private val _listUiState = MutableStateFlow(HomeListUiState())
    val listUiState: StateFlow<HomeListUiState> = _listUiState.asStateFlow()

    // StateFlow cho màn hình chi tiết (Tour/Location data)
    private val _detailUiState = MutableStateFlow(DetailScreenUiState())
    val detailUiState: StateFlow<DetailScreenUiState> = _detailUiState.asStateFlow()

    // StateFlow cho danh sách Reviews (trong màn hình chi tiết)
    private val _reviewState = MutableStateFlow(ReviewUiState())
    val reviewState: StateFlow<ReviewUiState> = _reviewState.asStateFlow()

    // StateFlow cho danh sách ID yêu thích (chủ yếu cho Tour)
    private val _favoriteItemIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteItemIds: StateFlow<Set<Int>> = _favoriteItemIds.asStateFlow()

    init {
        // Load trang đầu tiên của danh sách Home khi ViewModel được tạo
        fetchDataForCategory(_listUiState.value.selectedCategory, isInitialLoad = true)
    }

    // --- Logic cho Danh sách Home (List) ---

    private fun fetchDataForCategory(category: String, page: Int = 0, isInitialLoad: Boolean = false) {
        // Ngăn chặn gọi API trùng lặp khi đang loading (trừ lần load đầu)
        if (_listUiState.value.isLoading && !isInitialLoad) {
            Log.d("HomeViewModel", "[List] Already loading, skipping fetch for category $category page $page")
            return
        }

        // Reset state khi load trang đầu tiên hoặc đổi category
        if (page == 0) {
            _listUiState.update {
                it.copy(isLoading = true, error = null, items = emptyList(), currentPage = 0, isLastPage = false)
            }
        } else {
            // Chỉ set loading khi load thêm trang
            _listUiState.update { it.copy(isLoading = true, error = null) }
        }

        viewModelScope.launch {
            when (category.lowercase()) {
                "tour" -> fetchToursFromApi(page)
                "location" -> fetchLocationsFromApi(page)
                else -> {
                    Log.w("HomeViewModel", "[List] Unsupported category: $category")
                    _listUiState.update { it.copy(isLoading = false, error = "Category không hỗ trợ") }
                }
            }
        }
    }

    private suspend fun fetchToursFromApi(page: Int) {
        Log.d("HomeViewModel", "[List] Fetching tours page $page...")
        val result = tourRepository.getTours(page = page)
        result.onSuccess { tourResponse ->
            Log.d("HomeViewModel", "[List] Success tours page $page: ${tourResponse.content.size}. Last: ${tourResponse.last}")
            val newTourItems = tourResponse.content.map { DisplayItem.TourItem(it) }
            _listUiState.update { currentState ->
                currentState.copy(
                    items = if (page == 0) newTourItems else currentState.items + newTourItems,
                    isLoading = false,
                    currentPage = tourResponse.pageNo,
                    isLastPage = tourResponse.last
                )
            }
        }.onFailure { exception ->
            Log.e("HomeViewModel", "[List] Error tours page $page", exception)
            _listUiState.update {
                it.copy(isLoading = false, error = exception.message ?: "Lỗi tải Tours")
            }
        }
    }

    private suspend fun fetchLocationsFromApi(page: Int) {
        Log.d("HomeViewModel", "[List] Fetching locations page $page...")
        val result = locationRepository.getLocations(page = page)
        result.onSuccess { locationResponse ->
            Log.d("HomeViewModel", "[List] Success locations page $page: ${locationResponse.content.size}. Last: ${locationResponse.last}")
            val newLocationItems = locationResponse.content.map { DisplayItem.LocationItem(it) }
            _listUiState.update { currentState ->
                currentState.copy(
                    items = if (page == 0) newLocationItems else currentState.items + newLocationItems,
                    isLoading = false,
                    currentPage = locationResponse.pageNo,
                    isLastPage = locationResponse.last
                )
            }
        }.onFailure { exception ->
            Log.e("HomeViewModel", "[List] Error locations page $page", exception)
            _listUiState.update {
                it.copy(isLoading = false, error = exception.message ?: "Lỗi tải Locations")
            }
        }
    }

    // Được gọi từ UI khi người dùng chọn category tab
    fun setCategory(category: String) {
        val lowerCaseCategory = category.lowercase()
        if (_listUiState.value.selectedCategory != lowerCaseCategory) {
            Log.d("HomeViewModel", "[List] Category changed to: $lowerCaseCategory.")
            _listUiState.update { it.copy(selectedCategory = lowerCaseCategory) }
            // Gọi fetch trang đầu tiên cho category mới
            fetchDataForCategory(lowerCaseCategory, page = 0, isInitialLoad = true)
        }
    }

    // Được gọi từ UI (PostListScreen) khi cuộn gần hết danh sách Home
    fun loadNextPage() { // Load more cho danh sách Home
        val currentListState = _listUiState.value
        if (!currentListState.isLoading && !currentListState.isLastPage) {
            Log.d("HomeViewModel", "[List] Loading next page for ${currentListState.selectedCategory}. Current: ${currentListState.currentPage}")
            val nextPage = currentListState.currentPage + 1
            fetchDataForCategory(currentListState.selectedCategory, page = nextPage)
        } else {
            Log.d("HomeViewModel", "[List] Cannot load next page. isLoading: ${currentListState.isLoading}, isLastPage: ${currentListState.isLastPage}")
        }
    }

    // Được gọi từ UI (PostItem) khi nhấn nút yêu thích
    fun toggleFavorite(itemId: Int) {
        // Logic toggle yêu thích (hiện chỉ là thay đổi state local)
        _favoriteItemIds.update { currentFavorites ->
            if (currentFavorites.contains(itemId)) {
                Log.d("HomeViewModel", "Removing item $itemId from favorites")
                currentFavorites - itemId
            } else {
                Log.d("HomeViewModel", "Adding item $itemId to favorites")
                currentFavorites + itemId
            }
        }
        // TODO: Lưu trạng thái yêu thích vào SharedPreferences/Room
    }

    // --- Logic cho Màn hình Chi tiết ---

    // Được gọi từ UI (PostDetailRouterScreen) khi cần hiển thị chi tiết Tour
    fun fetchTourDetail(tourId: Int) {
        val currentData = _detailUiState.value.detailData
        // Kiểm tra nếu đang loading hoặc đã load đúng ID rồi thì bỏ qua
        if (currentData == DetailData.Loading || (currentData is DetailData.TourSuccess && currentData.tourDetail.id == tourId)) {
            if(currentData != DetailData.Loading) Log.d("HomeViewModel", "[Detail] Tour detail for ID $tourId already loaded.")
            else Log.d("HomeViewModel", "[Detail] Already loading detail.")
            return
        }

        Log.d("HomeViewModel", "[Detail] Fetching tour detail for ID: $tourId")
        // Cập nhật state chi tiết sang Loading và reset state reviews
        _detailUiState.update { it.copy(detailData = DetailData.Loading) }
        _reviewState.update { ReviewUiState(targetType = "TOUR", targetId = tourId) } // Reset review state, đặt target

        viewModelScope.launch {
            val result = tourRepository.getTourDetail(tourId = tourId)
            result.onSuccess { tourDetail ->
                Log.d("HomeViewModel", "[Detail] Success fetching tour detail ID $tourId. Fetching reviews...")
                _detailUiState.update { it.copy(detailData = DetailData.TourSuccess(tourDetail)) }
                // Fetch trang đầu tiên của reviews sau khi có chi tiết tour
                fetchReviews(targetType = "TOUR", targetId = tourId, page = 0)
            }.onFailure { exception ->
                Log.e("HomeViewModel", "[Detail] Error fetching tour detail ID $tourId", exception)
                _detailUiState.update { it.copy(detailData = DetailData.Error(exception.message ?: "Lỗi tải chi tiết Tour")) }
                // Có thể reset review state nếu fetch detail lỗi
                _reviewState.update { ReviewUiState(error = "Không thể tải đánh giá do lỗi tải chi tiết") }
            }
        }
    }

    // Được gọi từ UI (PostDetailRouterScreen) khi cần hiển thị chi tiết Location
    fun fetchLocationDetail(locationId: Int) {
        val currentData = _detailUiState.value.detailData
        // Kiểm tra nếu đang loading hoặc đã load đúng ID rồi thì bỏ qua
        if (currentData == DetailData.Loading || (currentData is DetailData.LocationSuccess && currentData.locationDetail.id == locationId)) {
            if(currentData != DetailData.Loading) Log.d("HomeViewModel", "[Detail] Location detail for ID $locationId already loaded.")
            else Log.d("HomeViewModel", "[Detail] Already loading detail.")
            return
        }

        Log.d("HomeViewModel", "[Detail] Fetching location detail for ID: $locationId")
        // Cập nhật state chi tiết sang Loading và reset state reviews
        _detailUiState.update { it.copy(detailData = DetailData.Loading) }
        _reviewState.update { ReviewUiState(targetType = "LOCATION", targetId = locationId) } // Reset review state, đặt target

        viewModelScope.launch {
            val result = locationRepository.getLocationDetail(locationId = locationId)
            result.onSuccess { locationDetail ->
                Log.d("HomeViewModel", "[Detail] Success fetching location detail ID $locationId. Fetching reviews...")
                _detailUiState.update { it.copy(detailData = DetailData.LocationSuccess(locationDetail)) }
                // Fetch trang đầu tiên của reviews sau khi có chi tiết location
                fetchReviews(targetType = "LOCATION", targetId = locationId, page = 0)
            }.onFailure { exception ->
                Log.e("HomeViewModel", "[Detail] Error fetching location detail ID $locationId", exception)
                _detailUiState.update { it.copy(detailData = DetailData.Error(exception.message ?: "Lỗi tải chi tiết Location")) }
                // Có thể reset review state nếu fetch detail lỗi
                _reviewState.update { ReviewUiState(error = "Không thể tải đánh giá do lỗi tải chi tiết") }
            }
        }
    }

    // Được gọi từ UI (PostDetailRouterScreen) khi rời màn hình chi tiết
    fun clearDetailState() {
        Log.d("HomeViewModel", "[Detail] Clearing detail and review state.")
        _detailUiState.update { it.copy(detailData = DetailData.Idle) }
        _reviewState.update { ReviewUiState() } // Reset review state về trạng thái ban đầu
    }


    // --- Logic cho danh sách Reviews ---

    // Hàm nội bộ để fetch reviews, được gọi bởi fetchTourDetail/fetchLocationDetail và loadMoreReviews
    private fun fetchReviews(targetType: String, targetId: Int, page: Int) {
        // Ngăn chặn gọi API trùng lặp khi đang loading reviews
        if (_reviewState.value.isLoading) {
            Log.d("HomeViewModel", "[Review] Already loading reviews, skipping fetch.")
            return
        }
        // Đảm bảo có target hợp lệ
        if (targetType.isBlank() || targetId <= 0) {
            Log.w("HomeViewModel", "[Review] Invalid targetType or targetId for fetching reviews.")
            _reviewState.update { it.copy(isLoading = false, error = "Target không hợp lệ") } // Cập nhật lỗi nếu cần
            return
        }

        Log.d("HomeViewModel", "[Review] Fetching reviews for $targetType ID $targetId, page $page")
        // Cập nhật state loading cho reviews
        _reviewState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = reviewRepository.getReviews(
                targetType = targetType,
                targetId = targetId,
                page = page
            )
            result.onSuccess { reviewResponse ->
                Log.d("HomeViewModel", "[Review] Success reviews page $page: ${reviewResponse.content.size}. Last: ${reviewResponse.last}")
                _reviewState.update { currentState ->
                    currentState.copy(
                        // Nối reviews mới vào list cũ nếu là trang > 0
                        reviews = if (page == 0) reviewResponse.content else currentState.reviews + reviewResponse.content,
                        isLoading = false,
                        currentPage = reviewResponse.pageNo,
                        isLastPage = reviewResponse.last,
                        // Đảm bảo targetType và targetId được lưu đúng
                        targetType = targetType,
                        targetId = targetId
                    )
                }
            }.onFailure { exception ->
                Log.e("HomeViewModel", "[Review] Error reviews page $page", exception)
                _reviewState.update {
                    it.copy(isLoading = false, error = exception.message ?: "Lỗi tải đánh giá")
                }
            }
        }
    }

    // Được gọi từ UI (ReviewSection) khi nhấn nút "Xem thêm"
    fun loadMoreReviews() {
        val currentReviewState = _reviewState.value
        // Chỉ load thêm nếu không đang loading, chưa phải trang cuối và có target hợp lệ
        if (!currentReviewState.isLoading && !currentReviewState.isLastPage && currentReviewState.targetId != null && currentReviewState.targetType != null) {
            Log.d("HomeViewModel", "[Review] Loading next review page. Current: ${currentReviewState.currentPage}")
            val nextPage = currentReviewState.currentPage + 1
            // Gọi fetchReviews với target và trang tiếp theo
            fetchReviews(
                targetType = currentReviewState.targetType!!,
                targetId = currentReviewState.targetId!!,
                page = nextPage
            )
        } else {
            Log.d("HomeViewModel", "[Review] Cannot load next review page. State: $currentReviewState")
        }
    }
}