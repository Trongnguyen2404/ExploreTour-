package com.example.vivu_app.ui.components

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vivu_app.R


@Composable
fun SearchBar() {
    var searchText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()  //  Chiếm toàn bộ chiều rộng có thể
            .height(40.dp) //  Tăng chiều cao một chút
            .background(Color.White, shape = RoundedCornerShape(50))
            .border(2.dp, Color.Black, shape = RoundedCornerShape(50)),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = searchText,
            onValueChange = { searchText = it },
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Start
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 50.dp, top = 8.dp, bottom = 8.dp) // Tăng padding bên trái
        )

        if (searchText.isEmpty()) {
            Text(
                text = "Search...",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp) // Giữ text placeholder căn trái
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = "Search Icon",
            tint = Color.Unspecified, // Giữ màu gốc của icon
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(35.dp) // Tăng kích thước icon lớn hơn
                .padding(end = 12.dp) // Tạo khoảng trống giữa icon và cạnh phải
        )
    }
}