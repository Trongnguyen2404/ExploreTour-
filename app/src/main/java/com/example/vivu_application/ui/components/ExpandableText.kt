package com.example.vivu_application.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.vivu_application.R
import com.example.vivu_application.model.PostContentItem

// --- Composable ExpandableText giữ nguyên như bạn cung cấp ---
@Composable
fun ExpandableText(
    text: String,
    maxLines: Int = 2 // Mặc định gốc là 2
) {
    // ... (code ExpandableText giữ nguyên)
    var expanded by remember { mutableStateOf(false) }
    var shouldShowExpand by remember { mutableStateOf(false) }
    var textLayoutResultState by remember { mutableStateOf<TextLayoutResult?>(null)} // Use this to prevent recomposition loop

    Column {
        Text(
            text = text,
            maxLines = if (expanded) Int.MAX_VALUE else maxLines,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            onTextLayout = { textLayoutResult ->
                // Only check overflow when collapsed and layout changes
                if (!expanded && textLayoutResultState?.hasVisualOverflow != textLayoutResult.hasVisualOverflow) {
                    shouldShowExpand = textLayoutResult.hasVisualOverflow
                    textLayoutResultState = textLayoutResult // Store the result
                }
            },
            modifier = Modifier.animateContentSize() // Cho smooth khi expand
        )

        // Only show button if needed
        if (shouldShowExpand) {
            Text(
                text = if (expanded) "Thu gọn" else "Xem thêm...",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { expanded = !expanded }
                    .padding(top = 4.dp)
//                    .align(Alignment.CenterHorizontally) // Center align the button
            )
        }
    }
}

@Composable
fun CombinedExpandableContent(
    initialContent: String,
    detailContents: List<PostContentItem>?, // Hoặc giữ List<PostContentItem> nếu không bao giờ null
    modifier: Modifier = Modifier,
    maxLinesCollapsed: Int = 5 // Đặt mặc định là 5 như yêu cầu
) {
    var expanded by remember { mutableStateOf(false) }
    var textLayoutResultState by remember { mutableStateOf<TextLayoutResult?>(null) }

    // Kiểm tra xem initialContent có bị tràn khi ở trạng thái thu gọn không
    val isInitialTextLong = remember(textLayoutResultState) {
        val layoutResult = textLayoutResultState
        // Chỉ tính là dài nếu kết quả layout tồn tại và có tràn dòng
        layoutResult != null && layoutResult.hasVisualOverflow
    }

    // Kiểm tra xem có detailContents thực sự hay không (kể cả list rỗng cũng tính là không có)
    val hasDetailContent = detailContents?.isNotEmpty() == true // An toàn với null

    // Nút Xem thêm/Thu gọn hiện khi:
    // 1. initialContent dài HOẶC
    // 2. Có detailContents để hiển thị (ngay cả khi initialContent ngắn)
    val shouldShowToggle = remember(isInitialTextLong, hasDetailContent) {
        isInitialTextLong || hasDetailContent
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize() // Hiệu ứng mượt
    ) {
        // --- Phần Nội dung Ban đầu (initialContent) ---
        Text(
            text = initialContent,
            // *** SỬA LOGIC maxLines: ***
            // Luôn giới hạn bởi maxLinesCollapsed khi không mở rộng.
            maxLines = if (expanded) Int.MAX_VALUE else maxLinesCollapsed,
            overflow = TextOverflow.Ellipsis, // Dấu ...
            style = MaterialTheme.typography.bodyLarge,
            onTextLayout = { textLayoutResult ->
                // Chỉ cập nhật layout khi đang thu gọn để kiểm tra overflow
                if (!expanded) {
                    textLayoutResultState = textLayoutResult
                }
                // Nếu muốn reset khi mở rộng (tùy chọn, thường không cần)
                // else { textLayoutResultState = null }
            }
        )

        // --- Phần Chi tiết (detailContents) - Chỉ hiển thị khi mở rộng VÀ có nội dung ---
        if (expanded && hasDetailContent) {
            Spacer(modifier = Modifier.height(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Sử dụng list đã kiểm tra null an toàn
                detailContents!!.forEach { item -> // Dấu !! an toàn vì đã check hasDetailContent
                    when (item) {
                        is PostContentItem.Text -> {
                            Text(
                                text = item.content,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        is PostContentItem.Image -> {
                            RenderContentImage(item)
                        }
                    }
                }
            }
        }

        // --- Nút Xem thêm / Thu gọn ---
        // Chỉ hiển thị nút nếu cần thiết (đã tính toán ở shouldShowToggle)
        if (shouldShowToggle) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (expanded) "...THU GỌN..." else "...XEM THÊM...",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .clickable { expanded = !expanded }
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 4.dp)
            )
        }
    }
}

// Hàm con để render ảnh trong detailContents
@Composable
private fun RenderContentImage(item: PostContentItem.Image) {
    val hasValidRes = item.imageRes != null && item.imageRes != 0
    val hasValidUrl = !item.imageUrl.isNullOrBlank()

    Box( // Bọc trong Box để dễ dàng thêm nền hoặc xử lý lỗi
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp) // Chiều cao cố định cho ảnh chi tiết
            .clip(RoundedCornerShape(8.dp)) // Bo góc nhẹ
            .background(Color.LightGray.copy(alpha = 0.3f)) // Nền chờ nhẹ
    ) {
        when {
            hasValidRes -> {
                Image(
                    painter = painterResource(id = item.imageRes!!),
                    contentDescription = null, // Nên thêm mô tả nếu có thể
                    modifier = Modifier.matchParentSize(), // Lấp đầy Box
                    contentScale = ContentScale.Crop
                )
            }
            hasValidUrl -> {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = null, // Nên thêm mô tả nếu có thể
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.avatar), // Ảnh chờ
                    error = painterResource(id = R.drawable.avatar) // Ảnh lỗi
                )
            }
            else -> {
                // Có thể hiển thị icon hoặc text báo lỗi ảnh ở đây
                Box(
                    modifier = Modifier.matchParentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No dowload the picture", color = Color.Gray)
                }
            }
        }
    }
}