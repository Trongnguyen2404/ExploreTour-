package com.example.vivu_application.view.auth.login


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.vivu_application.R
import com.example.vivu_application.data.model.RequestOtpBody
import com.example.vivu_application.data.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var apiError by remember { mutableStateOf("") } // State lỗi API
    var isLoading by remember { mutableStateOf(false) } // State loading

    var validationOrApiError by remember { mutableStateOf("") }
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
        if (!email.endsWith("@gmail.com", ignoreCase = true)) { // ignoreCase = true để không phân biệt hoa thường
            validationOrApiError = "Email must be a @gmail.com address"
            return
        }

        validationOrApiError = "" // Xóa lỗi validation cũ
        isLoading = true
        focusManager.clearFocus()

        coroutineScope.launch {
            try {
                val requestBody = RequestOtpBody(email = email.trim())
                val response = authApiService.requestPasswordResetOtp(requestBody) // Hoặc hàm API tương ứng

                if (response.isSuccessful) {
                    val userEmail = email.trim()
                    navController.navigate("passwordRecoveryOTP/$userEmail") // Hoặc "profilePageOTPPassword/$userEmail"
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Failed to request OTP. Email might not exist."
                    validationOrApiError = "Account not found" // Hiển thị lỗi từ API
                }
            } catch (e: Exception) {
                validationOrApiError = "Network error or server unavailable."
            } finally {
                isLoading = false
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
            modifier = Modifier.size(200.dp).padding(top = 40.dp)
        )


        Spacer(modifier = Modifier.height(40.dp))


        Text(
            text = "Password recovery",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // --- Ô nhập Email ---
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                // Xóa lỗi ngay khi người dùng bắt đầu nhập lại
                if (validationOrApiError.isNotEmpty()) validationOrApiError = ""
            },
            placeholder = { Text("Enter your email", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Gray, unfocusedIndicatorColor = Color.Gray,
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                // Màu viền lỗi sẽ tự động được áp dụng khi isError = true
                errorContainerColor = Color(0xFFFFF0F0) // Nền khi lỗi (tùy chọn)
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Email),
            keyboardActions = KeyboardActions(onDone = { if (!isLoading) validateAndRequestOtp() }),
            singleLine = true,
            textStyle = TextStyle(fontSize = 18.sp),
            enabled = !isLoading
        )

        // --- Hiển thị lỗi VALIDATION hoặc API ---
        if (validationOrApiError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = validationOrApiError,
                // ++ MÀU ĐỎ CHO LỖI ++
                color = Color.Red, // Lấy màu lỗi từ theme
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold, // Có thể không cần Bold
                modifier = Modifier
                    .align(Alignment.Start) // Căn trái cho text lỗi
                    .padding(start = 20.dp, end = 16.dp) // Padding cho text lỗi
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

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
