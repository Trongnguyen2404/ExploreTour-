package com.example.vivu_application.ui.components

// ... (Các import cho Composable giữ nguyên) ...
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update // Để cập nhật StateFlow an toàn
import com.example.vivu_application.R

class SearchViewModel : ViewModel() {

    // 1. Tạo MutableStateFlow riêng tư cho searchText
    private val _searchText = MutableStateFlow("") // Giá trị khởi tạo là chuỗi rỗng

    // 2. Expose nó ra ngoài dưới dạng StateFlow công khai, không thể thay đổi từ bên ngoài
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    // Dữ liệu gốc (giữ nguyên)
    private val _originalList = listOf("An", "Bình", "Cường", "Dũng", "Huy", "Hạnh")

    // 3. Sử dụng StateFlow cho danh sách đã lọc (Tương tự searchText)
    private val _filteredList = MutableStateFlow(_originalList)
    val filteredList: StateFlow<List<String>> = _filteredList.asStateFlow()

    // Hàm được gọi khi text trong SearchBar thay đổi
    fun onSearchTextChange(newText: String) {
        // Cập nhật StateFlow của searchText
        _searchText.update { newText } // Cách cập nhật an toàn

        // Cập nhật StateFlow của danh sách đã lọc dựa trên newText
        _filteredList.update {
            if (newText.isBlank()) {
                _originalList // Trả về danh sách gốc nếu text rỗng
            } else {
                _originalList.filter { item ->
                    item.contains(newText, ignoreCase = true) // Logic lọc
                }
            }
        }
    }
}

// Composable SearchBar giữ nguyên, không cần thay đổi
@Composable
fun SearchBar(
    searchText: String, // Nhận giá trị String hiện tại
    onSearchTextChange: (String) -> Unit // Nhận lambda để gọi khi text thay đổi
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color.White, shape = RoundedCornerShape(50))
            .border(2.dp, Color.Black, shape = RoundedCornerShape(50)),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = searchText,
            onValueChange = onSearchTextChange, // Gọi lambda khi có thay đổi
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Start
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 50.dp, top = 8.dp, bottom = 8.dp)
        )

        if (searchText.isEmpty()) {
            Text(
                text = "Search...",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp)
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = "Search Icon",
            tint = Color.Unspecified,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(35.dp)
                .padding(end = 12.dp)
        )
    }
}