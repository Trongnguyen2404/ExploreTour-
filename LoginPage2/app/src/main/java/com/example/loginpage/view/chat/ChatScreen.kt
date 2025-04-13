package com.example.loginpage.view.chat

import android.graphics.Paint
import android.os.Message
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items


@Composable
fun ChatScreen(navController: NavController,
                modifier: Modifier = Modifier,
                viewModel: ChatViewModel,

    ) {

    Column (
        modifier = modifier
    ) {
        AppHeader()
        MessageList(messageList = viewModel.messageList)

        MessageInput (
            onMessageSend = {
                viewModel.sendMessage(it)

            }
        )
    }

}

@Composable
fun MessageList(modifier: Modifier = Modifier, messageList : List<MessageModel>) {
    LazyColumn {
        items(messageList) {
            Text(text = it.message)
        }
    }
}


@Composable
fun MessageInput(onMessageSend: (String) -> Unit) {

    var message by remember {
        mutableStateOf(value = "")
    }

    Row (
        modifier = Modifier.padding(8.dp),
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
            onMessageSend(message)
            message = ""

        }) {
            Icon(imageVector = Icons.Default.Send, contentDescription = "Send" )
        }
    }
}

 @Composable
 fun AppHeader() {
     Box (
         modifier = Modifier.fillMaxWidth()
             .background(MaterialTheme.colorScheme.primary)
     ) {
         Text(modifier = Modifier
             .padding(16.dp),
             text = "Easy bot",
             color = Color.White,
             fontSize = 22.sp
             )
     }

 }