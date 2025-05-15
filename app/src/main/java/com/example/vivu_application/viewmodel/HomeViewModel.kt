package com.example.vivu_application.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vivu_application.data.model.LocationDetail
import com.example.vivu_application.data.model.Review
import com.example.vivu_application.data.model.TourDetail
import com.example.vivu_application.data.repository.LocationRepository
import com.example.vivu_application.data.repository.ReviewRepository
import com.example.vivu_application.data.repository.TourRepository
import com.example.vivu_application.model.DisplayItem
import com.example.vivu_application.data.local.PreferencesManager
import com.example.vivu_application.data.local.TokenManager
import com.example.vivu_application.data.model.UserProfile
import com.example.vivu_application.data.network.AuthApiService
import com.example.vivu_application.data.network.RetrofitClient
import com.example.vivu_application.data.repository.FavoriteRepository

// Import cho debounce và các flow operators khác
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow // ++ THÊM IMPORT NÀY ++
import kotlinx.coroutines.flow.asSharedFlow // ++ THÊM IMPORT NÀY ++
import retrofit2.HttpException // ++ THÊM IMPORT NÀY ++


// Data class HomeListUiState (Không có isLoadingMore)
data class HomeListUiState(
    val items: List<DisplayItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategory: String = "location",
    val currentPage: Int = 0,
    val isLastPage: Boolean = false
)

// Các data class khác (DetailData, DetailScreenUiState, ReviewUiState) giữ nguyên
sealed class DetailData {
    object Loading : DetailData()
    data class TourSuccess(val tourDetail: TourDetail) : DetailData()
    data class LocationSuccess(val locationDetail: LocationDetail) : DetailData()
    data class Error(val message: String) : DetailData()
    object Idle : DetailData()
}

data class DetailScreenUiState(
    val detailData: DetailData = DetailData.Idle
)

data class ReviewUiState(
    val reviews: List<Review> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val isLastPage: Boolean = false,
    val targetType: String? = null,
    val targetId: Int? = null
)

@OptIn(FlowPreview::class) // Cho debounce
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    // --- KHỞI TẠO DEPENDENCIES BÊN TRONG (Đảm bảo constructor repo khớp) ---
    private val authApiService: AuthApiService = RetrofitClient.authApiService
    private val tourRepository: TourRepository = TourRepository(authApiService)
    private val locationRepository: LocationRepository = LocationRepository(authApiService)
    private val reviewRepository: ReviewRepository = ReviewRepository(authApiService)
    private val favoriteRepository: FavoriteRepository = FavoriteRepository(authApiService)
    private val preferencesManager: PreferencesManager = PreferencesManager(application.applicationContext)
    // --- KẾT THÚC KHỞI TẠO ---

    private val _listUiState = MutableStateFlow(HomeListUiState())
    val listUiState: StateFlow<HomeListUiState> = _listUiState.asStateFlow()

    private val _detailUiState = MutableStateFlow(DetailScreenUiState())
    val detailUiState: StateFlow<DetailScreenUiState> = _detailUiState.asStateFlow()

    private val _reviewState = MutableStateFlow(ReviewUiState())
    val reviewState: StateFlow<ReviewUiState> = _reviewState.asStateFlow()

    val favoriteItemIds: StateFlow<Set<Int>> = preferencesManager.favoritePosts
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()
    // --- THÊM STATE CHO SEARCH QUERY ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    // --- KẾT THÚC THÊM STATE ---

    // ++ BIẾN ĐỂ QUẢN LÝ JOB LOADING HIỆN TẠI CHO DANH SÁCH CHÍNH ++
    private var fetchListJob: Job? = null

    private val _navigateToLoginEvent = MutableSharedFlow<Unit>()
    val navigateToLoginEvent = _navigateToLoginEvent.asSharedFlow()

    init {
        // Không gọi fetch trực tiếp ở đây, để observeSearchQuery xử lý lần đầu
        // fetchDataForCategory(_listUiState.value.selectedCategory, isInitialLoad = true)

        TokenManager.initialize(application.applicationContext)
        syncFavoritesFromServer()
        observeSearchQuery()
        fetchUserProfile()// Bắt đầu lắng nghe thay đổi search query
    }

    // --- THÊM HÀM CẬP NHẬT SEARCH QUERY TỪ UI ---
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
    // --- KẾT THÚC THÊM HÀM ---

    // --- THÊM HÀM OBSERVE SEARCH QUERY VÀ TRIGGER FETCH ---
    private fun observeSearchQuery() {
        _searchQuery
            .debounce(500L)
            .distinctUntilChanged()
            .onEach { query ->
                Log.d("HomeViewModel", "Search query debounced: '$query'. Triggering data fetch.")
                // Khi query thay đổi (hoặc lần đầu), gọi setCategory để reset và tải lại
                // Truyền forceReload = true để đảm bảo tải lại từ đầu
                setCategory(_listUiState.value.selectedCategory, forceReload = true, newSearchQuery = query.ifBlank { null })
            }
            .launchIn(viewModelScope)
    }
    // --- KẾT THÚC THÊM HÀM ---

    private fun syncFavoritesFromServer() {
        viewModelScope.launch {
            val result = favoriteRepository.getFavorites(page = 0, size = 1000)
            result.onSuccess { response ->
                val serverFavoriteIds = response.content.map { it.itemId }.toSet()
                Log.d("HomeViewModel", "Synced favorites from server: $serverFavoriteIds")
                preferencesManager.saveFavoritePosts(serverFavoriteIds)
            }.onFailure { exception ->
                Log.e("HomeViewModel", "Failed to sync favorites from server", exception)
            }
        }
    }

    // --- SỬA ĐỔI fetchDataForCategory ĐỂ NHẬN SEARCH QUERY ---
    // Hàm này giờ chỉ tập trung vào việc gọi API
    private fun fetchDataForCategory(category: String, page: Int, searchQuery: String?) {
        // Không cần kiểm tra isLoading ở đây nữa vì setCategory đã xử lý

        fetchListJob?.cancel()
        fetchListJob = viewModelScope.launch { // Gán job mới
            try {
                val resultItems: List<DisplayItem>
                val resultPageNo: Int
                val resultIsLast: Boolean

                when (category.lowercase()) {
                    "tour" -> {
                        val response = tourRepository.getTours(page = page, searchQuery = searchQuery).getOrThrow()
                        resultItems = response.content.map { DisplayItem.TourItem(it) }
                        resultPageNo = response.pageNo
                        resultIsLast = response.last
                    }
                    "location" -> {
                        val response = locationRepository.getLocations(page = page, searchQuery = searchQuery).getOrThrow()
                        resultItems = response.content.map { DisplayItem.LocationItem(it) }
                        resultPageNo = response.pageNo
                        resultIsLast = response.last
                    }
                    else -> {
                        throw IllegalArgumentException("Unsupported category: $category")
                    }
                }

                Log.d("HomeViewModel", "Successfully fetched ${resultItems.size} items for $category, page $page. IsLast: $resultIsLast")
                _listUiState.update { currentState ->
                    currentState.copy(
                        // Nếu là trang đầu (page 0), thay thế items. Nếu không, nối vào.
                        items = if (page == 0) resultItems else currentState.items + resultItems,
                        isLoading = false,
                        currentPage = resultPageNo,
                        isLastPage = resultIsLast,
                        error = null // Xóa lỗi nếu thành công
                    )
                }

            } catch (e: Exception) {
                if (e is HttpException && (e.code() == 401 || e.code() == 403)) {
                    Log.w("HomeViewModel", "Auth error (${e.code()}) in fetchDataForCategory. Logging out.")
                    TokenManager.clearTokens() // Sử dụng Application Context nếu cần
                    _navigateToLoginEvent.emit(Unit)
                    // Không cập nhật _listUiState.error nữa vì sẽ điều hướng đi
                } else {
                    _listUiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "An error occurred while fetching data.",
                        )
                    }
                }
            }
        }
    }

    // --- SỬA ĐỔI fetchToursFromApi ĐỂ NHẬN SEARCH QUERY ---
    private suspend fun fetchToursFromApi(page: Int, searchQuery: String? = null) {
        val result = tourRepository.getTours(page = page, searchQuery = searchQuery) // Truyền searchQuery
        result.onSuccess { tourResponse ->
            val newTourItems = tourResponse.content.map { DisplayItem.TourItem(it) }
            _listUiState.update { currentState ->
                currentState.copy(
                    items = if (page == 0) newTourItems else currentState.items + newTourItems,
                    isLoading = false,
                    currentPage = tourResponse.pageNo,
                    isLastPage = tourResponse.last,
                    error = null
                )
            }
        }.onFailure { exception ->
            Log.e("HomeViewModel", "[List] Error tours page $page, search: '$searchQuery'", exception)
            _listUiState.update {
                it.copy(isLoading = false, error = exception.message ?: "Lỗi tải Tours")
            }
        }
    }
    // --- KẾT THÚC SỬA ĐỔI ---
    fun fetchUserProfile() {
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "Fetching user profile...")
                val response = authApiService.getUserProfile()
                if (response.isSuccessful) {
                    _userProfile.value = response.body()
                    Log.i("HomeViewModel", "User profile fetched: ${response.body()?.name}")
                } else {
                    Log.e("HomeViewModel", "Failed to fetch user profile: ${response.code()} - ${response.errorBody()?.string()}")
                    // ++ KIỂM TRA LỖI 401/403 ++
                    if (response.code() == 401 || response.code() == 403) {
                        Log.w("HomeViewModel", "Auth error (${response.code()}) in fetchUserProfile. Logging out.")
                        TokenManager.clearTokens() // Sử dụng Application Context nếu TokenManager cần
                        _navigateToLoginEvent.emit(Unit) // Thông báo cho UI
                    }
                    _userProfile.value = null // Đặt là null nếu lỗi (kể cả 401/403 trước khi điều hướng)
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception fetching user profile: ${e.message}", e)
                // ++ KIỂM TRA LỖI HTTP 401/403 TỪ EXCEPTION (NẾU RETROFIT NÉM RA) ++
                if (e is HttpException && (e.code() == 401 || e.code() == 403)) {
                    Log.w("HomeViewModel", "Auth error (${e.code()}) from exception in fetchUserProfile. Logging out.")
                    TokenManager.clearTokens()
                    _navigateToLoginEvent.emit(Unit)
                }
                _userProfile.value = null
            }
        }
    }
    // --- SỬA ĐỔI fetchLocationsFromApi ĐỂ NHẬN SEARCH QUERY ---
    private suspend fun fetchLocationsFromApi(page: Int, searchQuery: String? = null) {
        Log.d("HomeViewModel", "[List] Fetching locations page $page, search: '$searchQuery'")
        val result = locationRepository.getLocations(page = page, searchQuery = searchQuery) // Truyền searchQuery
        result.onSuccess { locationResponse ->
            Log.d("HomeViewModel", "[List] Success locations page $page: ${locationResponse.content.size}. Last: ${locationResponse.last}")
            val newLocationItems = locationResponse.content.map { DisplayItem.LocationItem(it) }
            _listUiState.update { currentState ->
                currentState.copy(
                    items = if (page == 0) newLocationItems else currentState.items + newLocationItems,
                    isLoading = false,
                    currentPage = locationResponse.pageNo,
                    isLastPage = locationResponse.last,
                    error = null
                )
            }
        }.onFailure { exception ->
            Log.e("HomeViewModel", "[List] Error locations page $page, search: '$searchQuery'", exception)
            _listUiState.update {
                it.copy(isLoading = false, error = exception.message ?: "Lỗi tải Locations")
            }
        }
    }
    // --- KẾT THÚC SỬA ĐỔI ---
// Hàm setCategory giữ nguyên, observeSearchQuery sẽ tự xử lý fetch lại
    fun setCategory(category: String, forceReload: Boolean = false, newSearchQuery: String? = _searchQuery.value.ifBlank { null }) {
        val lowerCaseCategory = category.lowercase()
        val currentUiState = _listUiState.value
        Log.d("HomeViewModel", "setCategory called. New: '$lowerCaseCategory', Current: '${currentUiState.selectedCategory}', ForceReload: $forceReload, Query: '$newSearchQuery', Error: ${currentUiState.error}")

        // Điều kiện để thực sự fetch lại:
        // 1. Category thay đổi.
        // 2. Hoặc `forceReload` là true (thường dùng khi search query thay đổi hoặc nhấn "Thử lại" từ UI).
        // 3. Hoặc đang có lỗi (currentUiState.error != null) - đây là trường hợp cho nút "Thử lại".
        val shouldFetch = currentUiState.selectedCategory != lowerCaseCategory ||
                forceReload ||
                currentUiState.error != null // ++ LUÔN FETCH NẾU ĐANG CÓ LỖI ++

        if (shouldFetch) {
            Log.d("HomeViewModel", "Proceeding with fetch. Category: $lowerCaseCategory, Query: $newSearchQuery")
            // Hủy job cũ nếu có
            fetchListJob?.cancel()
            // Cập nhật UI state để reset và hiển thị loading
            _listUiState.update {
                it.copy(
                    selectedCategory = lowerCaseCategory,
                    items = emptyList(), // Xóa item cũ khi đổi category hoặc thử lại
                    isLoading = true,    // Bắt đầu loading
                    error = null,        // Xóa lỗi cũ
                    currentPage = 0,     // Reset về trang đầu
                    isLastPage = false   // Reset cờ trang cuối
                )
            }
            // Gọi hàm fetch dữ liệu thực sự
            fetchDataForCategory(lowerCaseCategory, page = 0, searchQuery = newSearchQuery)
        } else {
            Log.d("HomeViewModel", "Skipping fetch. No category change, no force reload, no error.")
        }
    }

    // --- SỬA ĐỔI loadNextPage để truyền searchQuery ---
    fun loadNextPage() {
        val currentListState = _listUiState.value
        // Chỉ load thêm nếu không đang loading, chưa phải trang cuối, và không có lỗi ở lần tải trước đó
        if (!currentListState.isLoading && !currentListState.isLastPage && currentListState.error == null) {
            val nextPage = currentListState.currentPage + 1
            Log.d("HomeViewModel", "Loading next page ($nextPage) for ${currentListState.selectedCategory}, search: '${_searchQuery.value}'")
            // Không cần set isLoading = true ở _listUiState ngay đây vì fetchDataForCategory sẽ làm
            // Nhưng nếu muốn UI phản hồi nhanh hơn cho việc "đang tải thêm", có thể thêm:
            // _listUiState.update { it.copy(isLoading = true) }
            fetchDataForCategory(
                category = currentListState.selectedCategory,
                page = nextPage,
                searchQuery = _searchQuery.value.ifBlank { null }
            )
        } else {
            Log.d("HomeViewModel", "Cannot load next page. Conditions not met. isLoading: ${currentListState.isLoading}, isLastPage: ${currentListState.isLastPage}, error: ${currentListState.error}")
        }
    }
    // --- KẾT THÚC SỬA ĐỔI ---

    // Hàm toggleFavorite giữ nguyên
    fun toggleFavorite(itemId: Int, itemType: String) {
        viewModelScope.launch {
            val currentFavoriteIds = favoriteItemIds.value
            val isCurrentlyFavorite = currentFavoriteIds.contains(itemId)
            val operationResult: Result<Unit>

            val optimisticFavorites = currentFavoriteIds.toMutableSet()
            if (isCurrentlyFavorite) {
                optimisticFavorites.remove(itemId)
            } else {
                optimisticFavorites.add(itemId)
            }
            preferencesManager.saveFavoritePosts(optimisticFavorites)
            Log.d("HomeViewModel", "Optimistic update for favorite $itemId ($itemType). New state: ${!isCurrentlyFavorite}")

            if (isCurrentlyFavorite) {
                Log.d("HomeViewModel", "Calling API to remove item $itemId ($itemType) from favorites")
                operationResult = favoriteRepository.removeFavorite(itemType, itemId.toString())
            } else {
                Log.d("HomeViewModel", "Calling API to add item $itemId ($itemType) to favorites")
                operationResult = favoriteRepository.addFavorite(itemType, itemId.toString())
            }

            operationResult.onSuccess {
                Log.d("HomeViewModel", "API call for favorite $itemId ($itemType) successful.")
            }.onFailure { exception ->
                Log.e("HomeViewModel", "API call for favorite $itemId ($itemType) failed.", exception)
                Log.d("HomeViewModel", "Rolling back optimistic update for favorite $itemId ($itemType).")
                preferencesManager.saveFavoritePosts(currentFavoriteIds)
            }
        }
    }

    // Hàm fetchTourDetail giữ nguyên
    fun fetchTourDetail(tourId: Int) {
        val currentData = _detailUiState.value.detailData
        if (currentData == DetailData.Loading || (currentData is DetailData.TourSuccess && currentData.tourDetail.id == tourId)) {
            if(currentData != DetailData.Loading) Log.d("HomeViewModel", "[Detail] Tour detail for ID $tourId already loaded.")
            else Log.d("HomeViewModel", "[Detail] Already loading detail.")
            return
        }
        Log.d("HomeViewModel", "[Detail] Fetching tour detail for ID: $tourId")
        _detailUiState.update { it.copy(detailData = DetailData.Loading) }
        _reviewState.update { ReviewUiState(targetType = "TOUR", targetId = tourId) }
        viewModelScope.launch {
            val result = tourRepository.getTourDetail(tourId = tourId)
            result.onSuccess { tourDetail ->
                Log.d("HomeViewModel", "[Detail] Success fetching tour detail ID $tourId. Fetching reviews...")
                _detailUiState.update { it.copy(detailData = DetailData.TourSuccess(tourDetail)) }
                fetchReviews(targetType = "TOUR", targetId = tourId, page = 0)
            }.onFailure { exception ->
                Log.e("HomeViewModel", "[Detail] Error fetching tour detail ID $tourId", exception)
                _detailUiState.update { it.copy(detailData = DetailData.Error(exception.message ?: "Lỗi tải chi tiết Tour")) }
                _reviewState.update { ReviewUiState(error = "Không thể tải đánh giá do lỗi tải chi tiết") }
            }
        }
    }
    // Hàm fetchLocationDetail giữ nguyên
    fun fetchLocationDetail(locationId: Int) {
        val currentData = _detailUiState.value.detailData
        if (currentData == DetailData.Loading || (currentData is DetailData.LocationSuccess && currentData.locationDetail.id == locationId)) {
            if(currentData != DetailData.Loading) Log.d("HomeViewModel", "[Detail] Location detail for ID $locationId already loaded.")
            else Log.d("HomeViewModel", "[Detail] Already loading detail.")
            return
        }
        Log.d("HomeViewModel", "[Detail] Fetching location detail for ID: $locationId")
        _detailUiState.update { it.copy(detailData = DetailData.Loading) }
        _reviewState.update { ReviewUiState(targetType = "LOCATION", targetId = locationId) }
        viewModelScope.launch {
            val result = locationRepository.getLocationDetail(locationId = locationId)
            result.onSuccess { locationDetail ->
                Log.d("HomeViewModel", "[Detail] Success fetching location detail ID $locationId. Fetching reviews...")
                _detailUiState.update { it.copy(detailData = DetailData.LocationSuccess(locationDetail)) }
                fetchReviews(targetType = "LOCATION", targetId = locationId, page = 0)
            }.onFailure { exception ->
                Log.e("HomeViewModel", "[Detail] Error fetching location detail ID $locationId", exception)
                _detailUiState.update { it.copy(detailData = DetailData.Error(exception.message ?: "Lỗi tải chi tiết Location")) }
                _reviewState.update { ReviewUiState(error = "Không thể tải đánh giá do lỗi tải chi tiết") }
            }
        }
    }

    // Hàm clearDetailState giữ nguyên
    fun clearDetailState() {
        Log.d("HomeViewModel", "[Detail] Clearing detail and review state.")
        _detailUiState.update { it.copy(detailData = DetailData.Idle) }
        _reviewState.update { ReviewUiState() }
    }

    // Hàm fetchReviews giữ nguyên logic nhưng chỉ dùng isLoading
    private fun fetchReviews(targetType: String, targetId: Int, page: Int) {
        if (_reviewState.value.isLoading && page != 0) {
            Log.d("HomeViewModel", "[Review] Already loading reviews, skipping fetch.")
            return
        }
        if (targetType.isBlank() || targetId <= 0) {
            Log.w("HomeViewModel", "[Review] Invalid targetType or targetId for fetching reviews.")
            _reviewState.update { it.copy(isLoading = false, error = "Target không hợp lệ") }
            return
        }
        Log.d("HomeViewModel", "[Review] Fetching reviews for $targetType ID $targetId, page $page")
        _reviewState.update { it.copy(isLoading = true, error = null) } // Luôn set isLoading

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
                        reviews = if (page == 0) reviewResponse.content else currentState.reviews + reviewResponse.content.distinctBy { it.id },
                        isLoading = false, // Tắt isLoading
                        currentPage = reviewResponse.pageNo,
                        isLastPage = reviewResponse.last,
                        targetType = targetType,
                        targetId = targetId,
                        error = null
                    )
                }
            }.onFailure { exception ->
                Log.e("HomeViewModel", "[Review] Error reviews page $page", exception)
                _reviewState.update {
                    it.copy(
                        isLoading = false, // Tắt isLoading
                        error = exception.message ?: "Lỗi tải đánh giá"
                    )
                }
            }
        }
    }
    // Hàm loadMoreReviews giữ nguyên logic nhưng chỉ kiểm tra isLoading
    fun loadMoreReviews() {
        val currentReviewState = _reviewState.value
        if (!currentReviewState.isLoading && !currentReviewState.isLastPage && currentReviewState.targetId != null && currentReviewState.targetType != null) {
            Log.d("HomeViewModel", "[Review] Loading next review page. Current: ${currentReviewState.currentPage}")
            val nextPage = currentReviewState.currentPage + 1
            fetchReviews(
                targetType = currentReviewState.targetType!!,
                targetId = currentReviewState.targetId!!,
                page = nextPage
            )
        } else {
            Log.d("HomeViewModel", "[Review] Cannot load next review page. State: $currentReviewState")
        }
    }

    // Hàm addReview giữ nguyên
    fun addReview(targetType: String, targetId: Int, rating: Int, comment: String) {
        if (_reviewState.value.targetId != targetId || _reviewState.value.targetType != targetType) {
            Log.w("HomeViewModel", "Attempting to add review to a mismatched target.")
            return
        }
        viewModelScope.launch {
            val submitResult = reviewRepository.submitNewReview(
                targetType = targetType,
                targetId = targetId,
                rating = rating,
                comment = comment
            )
            submitResult.onSuccess { submittedReview ->
                Log.i("HomeViewModel", "Review submitted successfully: $submittedReview")
                Log.d("HomeViewModel", "Review submission successful. Refreshing review list (page 0).")
                fetchReviews(targetType = targetType, targetId = targetId, page = 0)
            }.onFailure { exception ->
                Log.e("HomeViewModel", "Failed to submit review", exception)
                _reviewState.update {
                    it.copy(error = " ")
                }
            }
        }
    }

    // Hàm refreshFavoriteStatus giữ nguyên
    fun refreshFavoriteStatus() {
        Log.d("HomeViewModel", "Explicitly refreshing favorite status by syncing from server.")
        syncFavoritesFromServer()
    }
}
