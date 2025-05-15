package com.example.vivu_application.view.profile.detail


import android.util.Log // ++ THÊM ++
import android.widget.Toast // ++ THÊM ++
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // ++ THÊM ++
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext // ++ THÊM ++
import androidx.compose.ui.platform.LocalFocusManager // ++ THÊM ++
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.vivu_application.data.model.RequestOtpBody // ++ THÊM ++
import com.example.vivu_application.data.network.RetrofitClient // ++ THÊM ++
import kotlinx.coroutines.launch // ++ THÊM ++
import com.example.vivu_application.R

@Composable
fun ProfilePassword(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var apiError by remember { mutableStateOf("") } // State lỗi API
    var isLoading by remember { mutableStateOf(false) } // State loading


    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val authApiService = RetrofitClient.authApiService
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    fun validateAndRequestOtp() {
        // Client-side validation
        if (email.isBlank()) {
            apiError = "Email cannot be empty"
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            apiError = "Invalid email format"
            return
        }

        apiError = ""
        isLoading = true
        focusManager.clearFocus()

        coroutineScope.launch {
            try {
                val requestBody = RequestOtpBody(email = email.trim())
                // Gọi API forgot-password (cần xác thực không? Tùy backend)
                // Giả sử API này không cần xác thực access token
                val response = authApiService.requestPasswordResetOtp(requestBody)

                if (response.isSuccessful) {

                    // Điều hướng đến màn hình nhập OTP và truyền email
                    val userEmail = email.trim()
                    navController.navigate("profilePageOTPPassword/$userEmail") // ++ Đảm bảo route này tồn tại ++

                } else {
                    val errorBody = response.errorBody()?.string()
                        ?: "Failed to request OTP. Email might not exist."
                    apiError = " "
                }
            } catch (e: Exception) {
                apiError = "Network error or server unavailable."
            } finally {
                isLoading = false
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFececec))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Nút quay lại
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(top = 50.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back_ic),
                    contentDescription = "Back",
                    tint = Color(0xFF00BCD4)
                )
            }
        }

        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "You must enter your email to change password ",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(30.dp))

        // --- Ô nhập Email ---
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                if (apiError.isNotEmpty()) apiError = "" // Xóa lỗi
            },
            placeholder = { Text("Enter your email", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
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
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Email
            ),
            keyboardActions = KeyboardActions(onDone = { if (!isLoading) validateAndRequestOtp() }),
            singleLine = true,
            textStyle = TextStyle(fontSize = 18.sp),

            enabled = !isLoading
        )

        // --- Hiển thị lỗi API ---
        if (apiError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = apiError, // Hiển thị lỗi từ API
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start).padding(start = 20.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f)) // Đẩy nút xuống

        // --- Nút Next hoặc Loading ---
        Row( /* ... giữ nguyên style Box ... */
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(bottom = 350.dp).offset(60.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            } else {
                Button(
                    onClick = { validateAndRequestOtp() }, // Gọi hàm yêu cầu OTP
                    /* ... giữ nguyên style Button ... */
                    modifier = Modifier.fillMaxWidth(0.6f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.DarkGray,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(text = "Send Code", /* ... */) // Đổi chữ nút
                    // Bỏ icon nếu muốn
                }
            }
        }
    }
}
