package com.example.loginpage.view.auth.register
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.loginpage.R


@Composable
fun NameInCreateNewAccount(navController: NavController): Unit {

    var username by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf("") }

    fun validateAndProceed(){
        if (username.isBlank()){
            usernameError = "Username cannot be empty"
        } else {
            usernameError = ""
            navController.popBackStack("login", inclusive = false)
        }
    }

    Column(
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to Color(0xFFFFFFFF),
                        0.78f to Color(0xFF72DADE),
                        1.0f to Color(0xFFA2C9F0)
                    )
                )
            )
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.group_1),
            contentDescription = "Login image",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))


        Text(
            text = "Sign in",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = { Text("Email or username", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)), // Cắt nền theo viền bo tròn
                shape = RoundedCornerShape(12.dp), // Bo tròn khung
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Gray, // Màu viền khi focus
                    unfocusedIndicatorColor = Color.Gray, // Màu viền khi không focus
                    focusedContainerColor = Color.White, // Nền khi focus
                    unfocusedContainerColor = Color.White // Nền khi không focus
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, // Hoàn thành khi nhấn Enter
                    keyboardType = KeyboardType.Text // Bàn phím cho username
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        validateAndProceed() // Gọi hàm kiểm tra khi nhấn Enter
                    }
                ),
                singleLine = true // Ngăn xuống dòng
            )
        }

        Text(
            text = usernameError, // hiển thị lỗi nếu có
            color = Color.Red,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )


        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                validateAndProceed()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFA1C9F1), // Màu nền của button
                contentColor = Color.Black // Màu chữ trên button
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 16.dp, // Độ cao mặc định tạo bóng
                pressedElevation = 20.dp, // Độ cao khi nhấn
                disabledElevation = 0.dp // Độ cao khi vô hiệu hóa
            )
        ) {
            Text(
                text = "Confirm",
                style = TextStyle(fontSize = 20.sp) // Cỡ chữ của text trong button
            )
        }




    }
}



