package com.example.vivu_application.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vivu_application.data.model.ApiFavoriteItem
import com.example.vivu_application.data.local.PreferencesManager
import com.example.vivu_application.data.network.AuthApiService
import com.example.vivu_application.data.network.RetrofitClient
import com.example.vivu_application.data.repository.FavoriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val favoriteItems: List<ApiFavoriteItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val isLastPage: Boolean = false,
    val isLoadingMore: Boolean = false // Để hiển thị loading khi tải thêm trang
)

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {

    private val authApiService: AuthApiService = RetrofitClient.authApiService
    private val favoriteRepository: FavoriteRepository = FavoriteRepository(authApiService)
    // PreferencesManager có thể không cần thiết trong ViewModel này nếu không cache gì đặc biệt cho FavoritesScreen,
    // nhưng có thể hữu ích nếu bạn muốn đồng bộ trạng thái xóa ngay lập tức với SharedPreferences
    // hoặc nếu có logic phức tạp hơn. Hiện tại, chúng ta sẽ không dùng nó trực tiếp nhiều ở đây.
    private val preferencesManager: PreferencesManager = PreferencesManager(application.applicationContext)

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavoriteItems(isInitialLoad = true)
    }

    fun loadFavoriteItems(isInitialLoad: Boolean = false) {
        if (_uiState.value.isLoading || (_uiState.value.isLoadingMore && !isInitialLoad)) {
            Log.d("FavoriteViewModel", "Already loading favorites.")
            return
        }

        if (isInitialLoad) {
            _uiState.update {
                it.copy(isLoading = true, error = null, favoriteItems = emptyList(), currentPage = 0, isLastPage = false)
            }
        } else {
            // Đang load thêm trang
            if (_uiState.value.isLastPage) {
                Log.d("FavoriteViewModel", "Already on the last page.")
                return
            }
            _uiState.update { it.copy(isLoadingMore = true, error = null) }
        }

        val pageToLoad = if (isInitialLoad) 0 else _uiState.value.currentPage + 1

        viewModelScope.launch {
            Log.d("FavoriteViewModel", "Fetching favorites page $pageToLoad")
            val result = favoriteRepository.getFavorites(page = pageToLoad, size = 10) // Size có thể tùy chỉnh
            result.onSuccess { response ->
                _uiState.update { currentState ->
                    val newItems = response.content
                    currentState.copy(
                        favoriteItems = if (isInitialLoad) newItems else currentState.favoriteItems + newItems,
                        isLoading = false,
                        isLoadingMore = false,
                        currentPage = response.pageNo,
                        isLastPage = response.last,
                        error = null
                    )
                }
                Log.d("FavoriteViewModel", "Successfully fetched favorites page ${response.pageNo}. Items: ${response.content.size}")
            }.onFailure { exception ->
                Log.e("FavoriteViewModel", "Failed to fetch favorites", exception)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        error = exception.message ?: "Lỗi không xác định khi tải danh sách yêu thích"
                    )
                }
            }
        }
    }
    fun removeFavoriteItem(itemId: Int, itemType: String) {
        Log.d("FavoriteViewModel", "Attempting to remove favorite: ID $itemId, Type $itemType")
        viewModelScope.launch {
            // Hiển thị loading hoặc xử lý UI tạm thời nếu cần
            // _uiState.update { it.copy(isProcessingDelete = true) } // Ví dụ

            val result = favoriteRepository.removeFavorite(favoriteType = itemType, favoriteId = itemId.toString())

            result.onSuccess {
                Log.d("FavoriteViewModel", "Successfully removed favorite: ID $itemId, Type $itemType from server.")
                // Xóa item khỏi StateFlow UI cục bộ để cập nhật giao diện ngay
                _uiState.update { currentState ->
                    val updatedItems = currentState.favoriteItems.filterNot { it.itemId == itemId && it.favoriteType == itemType }
                    currentState.copy(favoriteItems = updatedItems)
                }
                // Cập nhật lại SharedPreferences nếu cần đồng bộ trạng thái
                // Lấy danh sách ID hiện tại từ SharedPreferences, xóa ID vừa rồi, rồi lưu lại
                val currentStoredFavoriteIds = preferencesManager.favoritePosts.value.toMutableSet()
                if (currentStoredFavoriteIds.contains(itemId)) { // Chỉ xóa nếu nó thực sự tồn
                    currentStoredFavoriteIds.remove(itemId)
                    preferencesManager.saveFavoritePosts(currentStoredFavoriteIds)
                    Log.d("FavoriteViewModel", "Removed item ID $itemId from SharedPreferences.")
                }

            }.onFailure { exception ->
                Log.e("FavoriteViewModel", "Failed to remove favorite: ID $itemId, Type $itemType", exception)
                _uiState.update {
                    it.copy(error = exception.message ?: "Lỗi khi xóa mục yêu thích")
                    // isProcessingDelete = false // Ví dụ
                }
                // Thông báo lỗi cho người dùng nếu cần
            }
        }
    }
}