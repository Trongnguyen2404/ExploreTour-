package com.example.vivu_app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.vivu_app.R


@Composable
fun TopHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(top = 15.dp)
            .zIndex(2f),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_vivu),
            contentDescription = "VIVU Logo",
            modifier = Modifier
                .size(130.dp)
                .height(40.dp) // Giới hạn rõ chiều cao
                .padding(bottom = 20.dp) // Như hiệu ứng xích lên
//                .offset(y = (-20).dp),
        )
        Spacer(modifier = Modifier.height(5.dp)) // Tạo khoảng cách nhỏ phía dưới
        Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Tên của bạn",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .offset(y = 5.dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.avatar),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            SearchBar()
        }
    }
}
