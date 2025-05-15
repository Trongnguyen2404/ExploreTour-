package com.example.vivu_application.view.profile.detail



import android.util.Log
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
import androidx.compose.ui.text.style.TextAlign // ++ THÊM ++
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.vivu_application.data.model.VerifyEmailChangeBody // ++ THÊM ++
import com.example.vivu_application.data.network.RetrofitClient // ++ THÊM ++
import kotlinx.coroutines.launch // ++ THÊM ++
import com.example.vivu_application.R
import com.example.vivu_application.data.local.TokenManager
import com.example.vivu_application.view.onboarding.OnboardingUtils


@Composable
fun ProfileOTPEmail(
    navController: NavHostController,
    newEmail: String, // ++ NHẬN EMAIL MỚI TỪ NAVIGATION ++
    onboardingUtils: OnboardingUtils
) {
    var otpCode by remember { mutableStateOf("") } // Đổi tên biến
    // var showError by remember { mutableStateOf("") } // Bỏ lỗi cũ
    var apiError by remember { mutableStateOf("") } // Lỗi API
    var isLoading by remember { mutableStateOf(false) } // State loading



    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val authApiService = RetrofitClient.authApiService
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    var showSuccessDialog by remember { mutableStateOf(false) }
    var isOtpVerified by remember { mutableStateOf(false) }



    // Hàm kiểm tra và gọi API
    fun verifyOtp() { // Đổi tên hàm cho rõ ràng
        if (otpCode.isBlank() || otpCode.length < 6) {
            apiError = "Please enter a valid 6-digit OTP code"
            return
        }
        // Không gọi API nếu đã verified rồi (tránh gọi lại)
        if (isOtpVerified) return

        apiError = ""
        isLoading = true
        focusManager.clearFocus()

        coroutineScope.launch {
            try {
                val requestBody = VerifyEmailChangeBody(newEmail = newEmail, otp = otpCode.trim())
                val response = authApiService.verifyEmailChange(requestBody)

                if (response.isSuccessful) {
                    isOtpVerified = true
                    apiError = ""
                    TokenManager.clearTokens()
                    onboardingUtils.setLogOut()
                    showSuccessDialog = true

                } else {
                    val errorCode = response.code()
                    val errorBodyString = response.errorBody()?.string() ?: "No error body"
                    apiError = "Invalid OTP" // Hiển thị chi tiết hơn cho bạn
                }
            } catch (e: Exception) {
                apiError = "Network error or server unavailable."

                isOtpVerified = false // Đảm bảo state là false nếu lỗi
            } finally {
                isLoading = false // Kết thúc loading của việc verify
            }
        }
    }

    fun handleConfirmationClick() {
        if (isLoading) return // Không làm gì nếu đang loading

        if (isOtpVerified) {
            // Nếu OTP đã được xác thực trước đó -> Thực hiện logout và mở dialog
            TokenManager.clearTokens()
            onboardingUtils.setLogOut()
            showSuccessDialog = true // Mở dialog để thông báo và điều hướng
        } else {
            // Nếu OTP chưa được xác thực -> Gọi hàm verifyOtp()
            verifyOtp() // Gọi API để xác thực OTP
        }
    }

    // --- Dialog thông báo thành công và yêu cầu đăng nhập lại ---
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { /* Không cho dismiss */ },
            title = { Text("Email Changed Successfully") },
            text = { Text("Your email has been updated. Please log in again with your new email address.") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        navController.navigate("login") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                ) {
                    Text("OK")
                }
            }
        )
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
            IconButton(onClick = { navController.popBackStack() },

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
            text = "A code has been sent to:\n$newEmail",
            fontSize = 20.sp,
            color = Color.DarkGray,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(30.dp))

        // --- Ô nhập OTP code ---
        OutlinedTextField(
            value = otpCode,
            onValueChange = { newValue ->
                if (newValue.length <= 6) {
                    otpCode = newValue.filter { it.isDigit() }
                }
                if (apiError.isNotEmpty()) apiError = "" // Xóa lỗi khi nhập
            },
            placeholder = { Text("Enter OTP", color = Color.Gray,textAlign = TextAlign.Center,  modifier = Modifier.fillMaxWidth()) },
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
                imeAction = ImeAction.None,
                keyboardType = KeyboardType.NumberPassword
            ),
            keyboardActions = KeyboardActions(), // Bỏ xử lý Enter,
            singleLine = true,
            textStyle = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center),
            enabled = !isLoading
        )

        // --- Hiển thị lỗi API ---
        if (apiError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = apiError, // Hiển thị lỗi từ state apiError
                fontSize = 14.sp,
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start).padding(start = 20.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f)) // Đẩy nút xuống

        // --- Nút Confirm hoặc Loading ---
       Row( /* ... giữ nguyên style Box ... */
           verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(bottom = 350.dp).offset(60.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            } else {
                Button(
                    onClick = { handleConfirmationClick() }, // Gọi hàm xác thực
                    /* ... giữ nguyên style Button ... */
                    modifier = Modifier.fillMaxWidth(0.6f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray, contentColor = Color.White),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(text = "Confirm", /* ... style chữ ... */) // Đổi chữ nút
                    // Bỏ icon nếu muốn
                }
            }
        }
    }
}