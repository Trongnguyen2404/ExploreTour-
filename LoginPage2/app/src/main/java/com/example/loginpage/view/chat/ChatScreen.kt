package com.example.loginpage.view.chat

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.loginpage.R
import com.example.loginpage.navigation.BottomNavigationBar
import com.example.loginpage.ui.theme.ColorModelMessage
import com.example.loginpage.ui.theme.ColorUserMessage
import androidx.compose.ui.unit.Dp

@Composable
fun ChatScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
        ) {
            AppHeader()
            MessageList(
                modifier = Modifier.weight(1f),
                messageList = viewModel.messageList
            )
            MessageInput(
                onMessageSend = {
                    viewModel.sendMessage(it)
                }
            )
        }
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
            .padding(horizontal = 8.dp, vertical = 2.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Avatar cho AI (bên trái)
        if (isModel) {
            Icon(
                painter = painterResource(id = R.drawable.message_ai),
                contentDescription = "AI Avatar",
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.LightGray),
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
fun MessageInput(onMessageSend: (String) -> Unit) {

    var message by remember {
        mutableStateOf(value = "")
    }

    Row (
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier
                .weight(1f),
            value = message,
            onValueChange = {
                message = it
            }
        )
        IconButton(onClick = {
            if(message.isNotEmpty()){
                onMessageSend(message)
                message = ""
            }


        }) {
            Icon(imageVector = Icons.Default.Send,
                contentDescription = "Send",
                tint = Color(0xFF03A9F4),
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
fun AppHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White) // Màu nền trắng giống hình
            .padding(horizontal = 16.dp, vertical = 4.dp) // Khoảng cách đều
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Căn đều các phần tử
        ) {

            // Tiêu đề ở giữa
            Text(
                text = "Chat with AI",
                color = Color(0xFF9ECEEB), // Màu chữ đen
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f), // Để chiếm không gian và căn giữa
                textAlign = TextAlign.Center // Căn giữa văn bản
            )

            // Nút Refresh bên phải
            IconButton(onClick = { /* Xử lý sự kiện Refresh */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.refresh_icon_ai), // Thay bằng icon refresh của bạn
                    contentDescription = "Refresh",
                    tint = Color(0xFF9ECEEB), //Màu icon
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}