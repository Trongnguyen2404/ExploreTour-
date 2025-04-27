
package com.example.vivu_application.view.auth.login

import android.util.Log // ++ THÊM ++
import android.widget.Toast // ++ THÊM ++
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.ui.platform.LocalContext // ++ THÊM ++
import androidx.compose.ui.platform.LocalFocusManager // ++ THÊM ++
import androidx.compose.ui.text.style.TextAlign // ++ THÊM ++
import com.example.vivu_application.data.model.VerifyOtpBody // ++ THÊM ++
import com.example.vivu_application.data.network.RetrofitClient // ++ THÊM ++
import kotlinx.coroutines.launch // ++ THÊM ++
import com.example.vivu_application.R
@Composable
fun PasswordRecoveryOTP(
    navController: NavHostController,
    email: String // ++ NHẬN EMAIL TỪ NAVIGATION ++
) {
    var otpCode by remember { mutableStateOf("") } // Đổi tên biến cho rõ ràng
    // var showError by remember { mutableStateOf(false) } // Bỏ lỗi cũ
    var apiError by remember { mutableStateOf("") } // State lỗi API
    var isLoading by remember { mutableStateOf(false) } // State loading

    // val fakeotp = "1111" // Xóa OTP giả

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val authApiService = RetrofitClient.authApiService
    val focusManager = LocalFocusManager.current

    fun verifyAndProceed() {
        if (otpCode.isBlank() || otpCode.length < 6) { // Ví dụ kiểm tra độ dài OTP
            apiError = "Please enter a valid 6-digit OTP code"
            return
        }

        apiError = ""
        isLoading = true
        focusManager.clearFocus()

        coroutineScope.launch {
            try {
                val requestBody = VerifyOtpBody(
                    email = email, // Dùng email nhận được
                    otp = otpCode.trim()
                )
                Log.d("PasswordRecoveryOTP", "Verifying forgot password OTP: $requestBody")
                val response = authApiService.verifyPasswordResetOtp(requestBody)

                if (response.isSuccessful) {
                    // Xác thực OTP thành công
                    Log.i("PasswordRecoveryOTP", "Forgot password OTP verified for: $email")
                    Toast.makeText(context, "OTP Verified successfully!", Toast.LENGTH_SHORT).show()

                    // !! QUAN TRỌNG: Chuyển hướng và truyền dữ liệu !!
                    // Màn hình NewPasswordScreen CẦN email và otp (hoặc token) để hoạt động.
                    // Chúng ta cần truyền chúng đi. Ví dụ truyền cả email và otp:
                    val verifiedOtp = otpCode.trim()
                    navController.navigate("newPasswordScreen/$email/$verifiedOtp") {
                        // Tùy chọn popUpTo
                        // Ví dụ: Xóa màn hình nhập email và OTP khỏi stack
                        popUpTo("forgotPassword") { inclusive = false }
                    }
                    // Nếu API verifyPasswordResetOtp trả về token, bạn sẽ lấy token đó
                    // và truyền nó thay vì otp:
                    // val verificationToken = response.body()?.verificationToken
                    // if (verificationToken != null) {
                    //     navController.navigate("newPasswordScreen/$email/$verificationToken") { ... }
                    // } else { // Xử lý lỗi thiếu token }

                } else {
                    // Lỗi xác thực OTP từ API
                    val errorBody = response.errorBody()?.string() ?: "Invalid or expired OTP."
                    Log.e("PasswordRecoveryOTP", "OTP verification failed: ${response.code()} - $errorBody")
                    apiError = "Verification failed: $errorBody"
                    Toast.makeText(context, "Error: $errorBody", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                // Lỗi mạng hoặc lỗi khác
                Log.e("PasswordRecoveryOTP", "OTP verification exception: ${e.message}", e)
                apiError = "Network error or server unavailable."
                Toast.makeText(context, "Network Error: ${e.message}", Toast.LENGTH_LONG).show()
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
            modifier = Modifier.size(200.dp)
        )


        Spacer(modifier = Modifier.height(40.dp))


        // Hiển thị email đang xác thực
        Text(
            text = "Enter the recovery code sent to:\n$email",
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // --- OTP Input ---
        OutlinedTextField(
            value = otpCode,
            onValueChange = { newValue ->
                // Chỉ cho phép nhập số và giới hạn độ dài (ví dụ 6)
                if (newValue.length <= 6) {
                    otpCode = newValue.filter { it.isDigit() }
                }
                if (apiError.isNotEmpty()) apiError = "" // Xóa lỗi khi nhập
            },
            label = { Text("Recovery Code") }, // Đổi Label
            placeholder = { Text("Enter 6-digit code", color = Color.Gray) }, // Gợi ý rõ hơn
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Gray, unfocusedIndicatorColor = Color.Gray,
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                errorContainerColor = Color(0xFFFFF0F0)
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.NumberPassword // Ẩn số nếu muốn, hoặc Number
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (!isLoading) {
                        verifyAndProceed()
                    }
                }
            ),
            singleLine = true,
            textStyle = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center), // Căn giữa OTP
            isError = apiError.isNotEmpty(),
            enabled = !isLoading
        )

        // --- Hiển thị lỗi API ---
        if (apiError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = apiError, // Hiển thị lỗi từ API
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.error,
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
                    onClick = { verifyAndProceed() }, // Gọi hàm xác thực
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
        Spacer(modifier = Modifier.height(20.dp)) // Khoảng trống cuối
    }
}