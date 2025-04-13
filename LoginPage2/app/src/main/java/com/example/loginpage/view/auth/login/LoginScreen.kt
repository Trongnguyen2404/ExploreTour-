// LoginScreen.kt
package com.example.loginpage.view.auth.login
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.loginpage.R
import com.example.loginpage.view.onboarding.OnboardingUtils


@Composable
fun LoginScreen(navController: NavController,
                onboardingUtils: OnboardingUtils): Unit {


    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailOrPasswordError by remember { mutableStateOf("") }

    val fakeemail = "123@gmail.com"
    val fakepassword = "12345678"

    val focusManager = LocalFocusManager.current

    fun validateAndProceed(){
        when {
            email.isBlank() -> emailOrPasswordError = "Email or username cannot be empty"
            password.isBlank() -> emailOrPasswordError = "Password cannot be empty"
            email != fakeemail || password != fakepassword -> emailOrPasswordError =
                "Incorrect email or password"

            else -> {
                emailOrPasswordError = ""
                onboardingUtils.setLogIn() // Lưu trạng thái đăng nhập
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true } // Xóa LoginScreen khỏi back stack
                }
            }
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
                value = email,
                onValueChange = { email = it },
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
                    imeAction = ImeAction.Next, // Chuyển sang trường tiếp theo khi nhấn Enter
                    keyboardType = KeyboardType.Text // Bàn phím cho mật khẩu
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down) // Chuyển focus xuống dưới
                    }
                ),
                singleLine = true ,// Ngăn xuống dòng
                textStyle = TextStyle(fontSize = 20.sp) // Tăng cỡ chữ trong khung nhập
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password", color = Color.Gray) },
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
                    imeAction = ImeAction.Done, // Chuyển sang hoàn thành
                    keyboardType = KeyboardType.Password // Bàn phím cho mật khẩu
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        validateAndProceed() // gọi hàm khi nhấn enter
                    }
                ),
                singleLine = true ,// Ngăn xuống dòng
                textStyle = TextStyle(fontSize = 20.sp) // Tăng cỡ chữ trong khung nhập
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Hiển thị thông báo lỗi nếu có, nếu không thì không chừa khoảng trống
        if (emailOrPasswordError.isNotEmpty()) {
            Text(
                text = emailOrPasswordError,
                color = Color.Red,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(10.dp)) // Khoảng cách nhỏ sau dòng lỗi
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Forgot password?",
                modifier = Modifier.clickable { navController.navigate("forgotPassword") },
                color = Color.Blue,
                textDecoration = TextDecoration.Underline
            )
        }

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
                defaultElevation = 5.dp, // Độ cao mặc định tạo bóng
                pressedElevation = 5.dp, // Độ cao khi nhấn
                disabledElevation = 0.dp // Độ cao khi vô hiệu hóa
            )
        ) {
            Text(
                text = "Sign in",
                style = TextStyle(fontSize = 20.sp) // Cỡ chữ của text trong button
            )
        }


        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Create new account",
                fontSize = 30.sp,
                modifier = Modifier.clickable { navController.navigate("createAccount") },
                color = Color.Blue,
                textDecoration = TextDecoration.Underline
            )
        }
    }


}

