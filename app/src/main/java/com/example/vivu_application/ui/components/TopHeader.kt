package com.example.vivu_application.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize // << THÊM NẾU CẦN CHO COIL (GIỮ NGUYÊN)
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset // GIỮ NGUYÊN (NẾU CÓ SỬ DỤNG OFFSET TRONG CODE GỐC CỦA BẠN)
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale // << THÊM CHO COIL (GIỮ NGUYÊN)
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight // GIỮ NGUYÊN
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter // << THÊM IMPORT CHO COIL (GIỮ NGUYÊN)
import com.example.vivu_application.R

@Composable
fun TopHeader(
    modifier: Modifier = Modifier,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    userName: String?,
    avatarUrl: String?,
    // ++ THÊM THAM SỐ MỚI ĐỂ ĐIỀU KHIỂN HIỂN THỊ SEARCHBAR ++
    showSearchBar: Boolean = true // Mặc định là true (hiển thị)
) {
    Row(
        modifier = modifier
            .padding(top = 15.dp) // GIỮ NGUYÊN
            .zIndex(2f), // GIỮ NGUYÊN
        horizontalArrangement = Arrangement.SpaceBetween, // GIỮ NGUYÊN
        verticalAlignment = Alignment.CenterVertically // GIỮ NGUYÊN
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_vivu), // GIỮ NGUYÊN
            contentDescription = "VIVU Logo", // GIỮ NGUYÊN
            modifier = Modifier // GIỮ NGUYÊN
                .size(130.dp)
                .height(40.dp)
                .padding(bottom = 20.dp)
        )

        // Spacer(modifier = Modifier.height(5.dp)) // GIỮ NGUYÊN (NẾU CÓ)

        Column(horizontalAlignment = Alignment.End) { // GIỮ NGUYÊN
            Row(verticalAlignment = Alignment.CenterVertically) { // GIỮ NGUYÊN
                Text(
                    text = userName ?: "User", // GIỮ NGUYÊN
                    style = MaterialTheme.typography.bodyLarge, // GIỮ NGUYÊN
                    fontWeight = FontWeight.Bold, // GIỮ NGUYÊN
                    modifier = Modifier // GIỮ NGUYÊN
                        .padding(end = 8.dp)
                        .padding(top = if (userName.isNullOrBlank()) 0.dp else 5.dp)
                )

                Image(
                    painter = if (!avatarUrl.isNullOrBlank()) { // GIỮ NGUYÊN
                        rememberAsyncImagePainter(
                            model = avatarUrl,
                            placeholder = painterResource(id = R.drawable.avatar),
                            error = painterResource(id = R.drawable.avatar)
                        )
                    } else {
                        painterResource(id = R.drawable.avatar)
                    },
                    contentDescription = "Avatar", // GIỮ NGUYÊN
                    modifier = Modifier // GIỮ NGUYÊN
                        .size(35.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop // GIỮ NGUYÊN
                )
            }

            // ++ CHỈ THÊM LOGIC ĐIỀU KIỆN Ở ĐÂY ++
            if (showSearchBar) {
                Spacer(modifier = Modifier.height(10.dp)) // GIỮ NGUYÊN
                SearchBar( // GIỮ NGUYÊN
                    searchText = searchText,
                    onSearchTextChange = onSearchTextChange
                )
            }
            // ++ KẾT THÚC THÊM LOGIC ĐIỀU KIỆN ++
        }
    }
}