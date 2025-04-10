package com.example.vivu_app.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ExpandableText(
    text: String,
    maxLines: Int = 3
) {
    var expanded by remember { mutableStateOf(false) }
    var shouldShowExpand by remember { mutableStateOf(false) }

    Column {
        Text(
            text = text,
            maxLines = if (expanded) Int.MAX_VALUE else maxLines,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            onTextLayout = { textLayoutResult ->
                // Kiểm tra nếu có bị cắt dòng không => mới hiện "Xem thêm..."
                if (!expanded) {
                    shouldShowExpand = textLayoutResult.hasVisualOverflow
                }
            },
            modifier = Modifier.animateContentSize() // Cho smooth khi expand
        )

        // Chỉ hiển thị khi nội dung dài
        if (shouldShowExpand) {
            Text(
                text = if (expanded) "Thu gọn" else "Xem thêm...",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { expanded = !expanded }
                    .padding(top = 4.dp)
            )
        }
    }
}