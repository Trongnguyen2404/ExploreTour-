package com.example.vivu_application.view.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // ++ THÊM ++
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext // ++ THÊM ++
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign // ++ THÊM ++
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vivu_application.data.model.SetNewPasswordBody // ++ THÊM ++
import com.example.vivu_application.data.network.RetrofitClient // ++ THÊM ++
import kotlinx.coroutines.launch // ++ THÊM ++
import com.example.vivu_application.R

@Composable
fun NewPasswordScreen(
    navController: NavController,
    email: String, // ++ NHẬN EMAIL ++
    verificationData: String // ++ NHẬN OTP/TOKEN XÁC THỰC ++
) {
    var password by remember { mutableStateOf("") }
    var repassword by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf("") } // Lỗi validation local
    var apiError by remember { mutableStateOf("") } // Lỗi từ API
    var isLoading by remember { mutableStateOf(false) } // State loading
    var passwordVisible by remember { mutableStateOf(false) }
    var repasswordVisible by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val authApiService = RetrofitClient.authApiService
    val scrollState = rememberScrollState()



    fun setNewPasswordAndProceed() {
        // --- Local Validation ---
        validationError = when {
            password.isBlank() -> "New password cannot be empty"
            password.length < 8 -> "Password must be at least 8 characters" // Giữ lại kiểm tra độ dài
            repassword.isBlank() -> "Please confirm your new password"
            password != repassword -> "Passwords do not match"
            else -> "" // Hợp lệ local
        }

        if (validationError.isEmpty()) {
            // --- Call API ---
            apiError = ""
            isLoading = true
            focusManager.clearFocus()

            coroutineScope.launch {
                try {
                    val requestBody = SetNewPasswordBody(
                        email = email,
                        otp = verificationData, // Dùng dữ liệu xác thực (OTP) đã nhận
                        newPassword = password, // Mật khẩu mới
                        repeatPassword = repassword // Xác nhận mật khẩu
                    )
                    val response = authApiService.setNewPassword(requestBody)

                    if (response.isSuccessful) {

                        // Điều hướng về trang Login, xóa toàn bộ stack quên mật khẩu
                        navController.navigate("login") {
                            // Xóa các màn hình từ forgotPassword đến màn hình này
                            popUpTo("forgotPassword") { inclusive = true }
                            launchSingleTop = true // Đảm bảo chỉ có 1 màn hình login
                        }

                    } else {
                        // Lỗi từ API (OTP sai/hết hạn lại, lỗi server...)
                        val errorBody = response.errorBody()?.string() ?: "Failed to set new password."
                        apiError = " "
                    }

                } catch (e: Exception) {
                    // Lỗi mạng hoặc lỗi khác
                    apiError = "Network error or server unavailable."
                } finally {
                    isLoading = false
                }
            }
        } else {
            isLoading = false // Đảm bảo isLoading là false nếu validation thất bại
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
            modifier = Modifier.size(200.dp).padding(top = 40.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Enter and confirm your new password for\n$email", // Hiển thị email
            fontSize = 20.sp, color = Color.DarkGray,
            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // --- New Password Input ---
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (validationError.isNotEmpty() || apiError.isNotEmpty()) {
                    validationError = ""
                    apiError = ""
                }
            },
            placeholder = { Text("Enter new password", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),

            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Gray, unfocusedIndicatorColor = Color.Gray,
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                cursorColor = Color.Black, // Hoặc màu bạn muốn cho con trỏ
                focusedTextColor = Color.Black, // Màu chữ khi focus
                unfocusedTextColor = Color.Black, // Màu chữ khi không focus
                errorContainerColor = Color.White,       // Giống unfocusedContainerColor (hoặc focused)
                errorCursorColor = Color.Black,            // Giống cursorColor
                errorTextColor = Color.Black,
            ),


            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Password),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }, enabled = !isLoading) {
                    Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                }
            },
            singleLine = true,
            textStyle = TextStyle(fontSize = 18.sp),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Confirm New Password Input ---
        OutlinedTextField(
            value = repassword,
            onValueChange = {
                repassword = it
                if (validationError.isNotEmpty() || apiError.isNotEmpty()) {
                    validationError = ""
                    apiError = ""
                }
            },
            placeholder = { Text("Re-enter new password", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Gray, unfocusedIndicatorColor = Color.Gray,
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                cursorColor = Color.Black, // Hoặc màu bạn muốn cho con trỏ
                focusedTextColor = Color.Black, // Màu chữ khi focus
                unfocusedTextColor = Color.Black, // Màu chữ khi không focus
                errorContainerColor = Color.White,       // Giống unfocusedContainerColor (hoặc focused)
                errorCursorColor = Color.Black,            // Giống cursorColor
                errorTextColor = Color.Black,
            ),

            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
            keyboardActions = KeyboardActions(onDone = { if (!isLoading) setNewPasswordAndProceed() }),
            visualTransformation = if (repasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (repasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { repasswordVisible = !repasswordVisible }, enabled = !isLoading) {
                    Icon(imageVector = image, contentDescription = if (repasswordVisible) "Hide password" else "Show password")
                }
            },
            singleLine = true,
            textStyle = TextStyle(fontSize = 18.sp),
            enabled = !isLoading
        )

        // --- Hiển thị lỗi validation và API ---
        if (validationError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = validationError,
                fontSize = 14.sp,
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start).padding(start = 4.dp)
            )
        }
        if (apiError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = apiError, // Hiển thị lỗi từ API
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start).padding(start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // --- Button hoặc Loading ---
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().height(50.dp)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            } else {
                Button(
                    onClick = { setNewPasswordAndProceed() }, // Gọi hàm đặt lại MK
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFA1C9F1),
                        contentColor = Color.Black
                    ),
                    elevation = ButtonDefaults.buttonElevation( defaultElevation = 8.dp ),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "Confirm",
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(30.dp)) // Khoảng trống cuối
    }
}