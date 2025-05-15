package com.example.vivu_application.view.chat


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vivu_application.R
import com.example.vivu_application.ui.theme.ColorModelMessage
import com.example.vivu_application.ui.theme.ColorUserMessage
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.foundation.clickable
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState


@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel
) {

    val isRefreshing by viewModel.isRefreshing.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AppHeader(
            navController = navController,
            onRefreshClick = viewModel::clearHistory, // Truyền tham chiếu hàm
            isRefreshing = isRefreshing // Truyền trạng thái refresh
        )

        MessageList(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            messageList = viewModel.messageList,
            navController = navController

        )

        MessageInput(
            onMessageSend = { viewModel.sendMessage(it) },
            modifier = Modifier.fillMaxWidth()
                .imePadding() // đẩy lên theo bàn phím
        )
    }
}


@Composable
fun MessageList(modifier: Modifier = Modifier, messageList : List<MessageModel>, navController: NavController) {
    if(messageList.isEmpty()) { // Kiểm tra nếu danh sách tin nhắn rỗng
        Column (
            modifier = modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ai_icon),
                contentDescription = "Icon",
                modifier = Modifier.size(90.dp),
            )
            Text(text = "Ask me anything", fontSize = 22.sp)
        }
    } else { // Nếu danh sách không rỗng, hiển thị tin nhắn
        LazyColumn(
            modifier = modifier,
            reverseLayout = true
        ) {
            items(messageList.reversed()) {
                MessageRow(messageModel = it, navController = navController)
            }
        }
    }
}


@Composable
fun MessageRow(messageModel: MessageModel, navController: NavController) {
    val isModel = messageModel.role == "model"
    val textColor = if (isModel) Color.Black else Color.White
    val backgroundColor = if (isModel) ColorModelMessage else ColorUserMessage // Thay ColorModelMessage, ColorUserMessage
    val modelBorderColor = Color.DarkGray
    val modelBorderSize = 1.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Avatar cho AI (bên trái)
        if (isModel) {
            Icon(
                painter = painterResource(id = R.drawable.ai_icon),
                contentDescription = "AI Avatar",
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.Transparent),
                tint = Color.Unspecified
            )
        } else {
            Spacer(modifier = Modifier.width(36.dp)) // Giữ khoảng trống để căn chỉnh tin nhắn người dùng
        }

        // Cột chứa thời gian, tin nhắn, và suggestions
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(
                    start = if (isModel) 8.dp else 0.dp,
                    end = if (isModel) 24.dp else 8.dp
                ),
            horizontalAlignment = if (isModel) Alignment.Start else Alignment.End
        ) {
            // Thời gian ở trên tin nhắn
            Text(
                text = messageModel.time,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Nội dung tin nhắn
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(backgroundColor)
                    .border(
                        width = if (isModel && backgroundColor == Color.White) modelBorderSize else Dp.Hairline,
                        color = if (isModel && backgroundColor == Color.White) modelBorderColor else Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                SelectionContainer {
                    Text(
                        text = cleanMarkdown(messageModel.message),
                        fontWeight = FontWeight.W400,
                        color = textColor,
                        fontSize = 16.sp,

                    )

                }
            }

            // Hiển thị suggestions nếu có
            messageModel.suggestions?.forEach { suggestion ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        // Tiêu đề
                        suggestion.title?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                        // Ảnh
                        suggestion.imageUrl?.let { url ->
                            AsyncImage(
                                model = url,
                                contentDescription = suggestion.title,
                                modifier = Modifier
                                    .size(250.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .padding(bottom = 4.dp)
                                    .clickable {
                                        suggestion.id?.let { id ->
                                            try {
                                                val idInt = id.toInt() // Chuyển id sang Int
                                                val route = when (suggestion.type?.lowercase()) {
                                                    "tour" -> "tourDetail/$idInt"
                                                    "location" -> "locationDetail/$idInt"
                                                    else -> {
                                                        return@clickable
                                                    }
                                                }
                                                navController.navigate(route)
                                            } catch (e: NumberFormatException) {
                                            }
                                        }
                                    },
                                contentScale = ContentScale.Crop
                            )
                        }
                        suggestion.summary?.let {
                            Text(
                                text = it,
                                fontSize = 14.sp,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun MessageInput(onMessageSend: (String) -> Unit, modifier: Modifier) {
    var message by remember { mutableStateOf("") }

    Row(
        modifier = modifier
            .padding(vertical = 15.dp)
            .windowInsetsPadding(
                WindowInsets
                    .navigationBars // Lấy chiều cao của thanh điều hướng
                    .only(WindowInsetsSides.Bottom)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 48.dp, max = 120.dp)
                .border(
                    width = 1.dp, // Độ dày viền tùy chỉnh
                    color = Color.Gray,
                )
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = modifier
                    .heightIn(min = 48.dp, max = 120.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent, // vien khi focus
                    unfocusedBorderColor = Color.Transparent, // Tắt viền mặc định
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                minLines = 1,
                maxLines = 4, // Giới hạn tối đa 4 dòng
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                shape = RoundedCornerShape(8.dp),
                trailingIcon = {
                    IconButton(onClick = {
                        if (message.isNotEmpty()) {
                            onMessageSend(message)
                            message = ""
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = Color(0xFF03A9F4),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun AppHeader(
    navController: NavController,
    onRefreshClick: () -> Unit, // ++ THÊM CALLBACK ++
    isRefreshing: Boolean      // ++ THÊM STATE ++
)  {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .zIndex(1f)
    ) {
        // Chữ "Chat with AI" ở giữa
        Text(
            text = "Chat with AI",
            color = Color(0xFF9ECEEB),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )

        // Nút Back và chữ "Back" ở bên trái
        Box(
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.home_ic),
                        contentDescription = "Back",
                        tint = Color(0xFF9ECEEB),
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        // Nút Refresh ở bên phải
        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
            IconButton(
                onClick = onRefreshClick, // ++ GỌI CALLBACK KHI NHẤN ++
                enabled = !isRefreshing // ++ VÔ HIỆU HÓA KHI ĐANG REFRESH ++
            ) {
                // ++ HIỂN THỊ LOADING HOẶC ICON REFRESH ++
                if (isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFF9ECEEB), // Màu loading
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.refresh_icon_ai), // Icon refresh
                        contentDescription = "Refresh Chat",
                        tint = Color(0xFF9ECEEB),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

fun cleanMarkdown(text: String): String {
    return text
        .replace(Regex("\\*\\*(.*?)\\*\\*"), "$1") // remove bold
        .replace(Regex("\\*(.*?)\\*"), "$1")       // remove italic
        .replace("\n* ", "\n• ")                   // bullet points
        .trim()
        .replace("\\s+".toRegex(), " ") // Thay thế nhiều khoảng trắng liên tiếp bằng một khoảng trắng đơn
}