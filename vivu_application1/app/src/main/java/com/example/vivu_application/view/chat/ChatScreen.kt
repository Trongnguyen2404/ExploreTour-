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

@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AppHeader(navController = navController)

        MessageList(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            messageList = viewModel.messageList
        )

        MessageInput(
            onMessageSend = { viewModel.sendMessage(it) },
            modifier = Modifier.fillMaxWidth()
                .imePadding() // đẩy lên theo bàn phím
        )
    }
}


@Composable
fun MessageList(modifier: Modifier = Modifier, messageList : List<MessageModel>) {
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
                MessageRow(messageModel = it)
            }
        }
    }
}

@Composable
fun MessageRow(messageModel: MessageModel) {
    val isModel = messageModel.role == "model"
    val textColor = if (isModel) Color.Black else Color.White
    val backgroundColor = if (isModel) ColorModelMessage else ColorUserMessage
    val modelBorderColor = Color.LightGray
    val modelBorderSize = 1.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            //.imePadding() // Chỉ phần này co lại khi bàn phím xuất hiện
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

        // Cột chứa thời gian và tin nhắn
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(
                    start = if (isModel) 8.dp else 0.dp, // Khoảng cách bên trái cho AI
                    end = if (isModel) 24.dp else 8.dp // Giảm khoảng cách bên phải cho người dùng
                ),
            horizontalAlignment = if (isModel) Alignment.Start else Alignment.End
        ) {
            // Thời gian ở trên tin nhắn
            Text(
                text = messageModel.time,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier
                    .padding(bottom = 4.dp)
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
                    .padding(12.dp)
            ) {
                SelectionContainer {
                    Text(
                        text = messageModel.message,
                        fontWeight = FontWeight.W400,
                        color = textColor,
                        fontSize = 16.sp
                    )
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
fun AppHeader(navController: NavController) {
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
                        painter = painterResource(id = R.drawable.back_icon_ai),
                        contentDescription = "Back",
                        tint = Color(0xFF9ECEEB),
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    text = "Back",
                    color = Color(0xFF9ECEEB),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        // Nút Refresh ở bên phải
        Box(
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            IconButton(onClick = { /* Xử lý sự kiện Refresh */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.refresh_icon_ai),
                    contentDescription = "Refresh",
                    tint = Color(0xFF9ECEEB),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}