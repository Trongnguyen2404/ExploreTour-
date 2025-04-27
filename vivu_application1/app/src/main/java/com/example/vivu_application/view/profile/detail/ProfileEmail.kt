package com.example.vivu_application.view.profile.detail // Giữ nguyên package hoặc đổi nếu muốn

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
// ... các import khác giữ nguyên ...
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.vivu_application.data.model.VerifyPasswordRequestBody // Đảm bảo đúng data class
import com.example.vivu_application.data.network.RetrofitClient
import kotlinx.coroutines.launch
import java.io.IOException
import com.example.vivu_application.R

@Composable
// ++ Đổi tên Composable cho rõ ràng ++
fun VerifyCurrentPasswordScreen(navController: NavHostController) { // Đổi tên Composable
    var currentPassword by remember { mutableStateOf("") } // Đổi tên biến cho rõ ràng
    var apiError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    // Giả sử RetrofitClient đã cấu hình sẵn Interceptor để thêm token
    val authApiService = RetrofitClient.authApiService
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Hàm kiểm tra và gọi API xác thực mật khẩu hiện tại
    fun verifyPasswordAndProceed() {
        if (currentPassword.isBlank()) {
            apiError = "Current password cannot be empty"
            return
        }

        apiError = ""
        isLoading = true
        focusManager.clearFocus()


        coroutineScope.launch {
            try {
                val requestBody = VerifyPasswordRequestBody(currentPassword = currentPassword)
                Log.d("VerifyCurrentPassword", "Verifying current password...")

                // Gọi API xác thực mật khẩu hiện tại
                // DÙNG ENDPOINT MÀ BẠN CUNG CẤP: verifyPasswordForEmailChange
                // LƯU Ý: API này PHẢI được backend xử lý để chỉ kiểm tra mật khẩu dựa trên token
                // và không thực hiện bất kỳ hành động đổi email nào. Nếu không, bạn cần endpoint khác.
                val response = authApiService.verifyPasswordForEmailChange(requestBody)

                if (response.isSuccessful) {
                    // Mật khẩu chính xác
                    Log.i("VerifyCurrentPassword", "Password verified successfully.")
                    Toast.makeText(context, "Password verified", Toast.LENGTH_SHORT).show()

                    // Lấy mật khẩu người dùng vừa nhập thành công
                    val verifiedPassword = currentPassword

                    // ++ SỬA ĐIỀU HƯỚNG ++
                    // Điều hướng đến màn hình nhập email MỚI, truyền mật khẩu đã xác thực
                    // (Lưu ý vấn đề bảo mật khi truyền PW qua route)
                    navController.navigate("profileEnterNewEmail/$verifiedPassword") { // Gọi route mới và truyền PW
                        launchSingleTop = true
                        // Cân nhắc popUpTo nếu cần xóa màn hình này khỏi backstack
                        // popUpTo("profilePageDetail") // Ví dụ: quay lại trang detail sau khi hoàn tất
                    }

                } else {
                    // Lỗi từ API (sai mật khẩu, lỗi server,...)
                    val errorBody = response.errorBody()?.string()
                    Log.e("VerifyCurrentPassword", "Password verification failed: ${response.code()} - $errorBody")

                    // Phân tích lỗi cụ thể hơn
                    when (response.code()) {
                        400, 401, 403 -> { // Các mã lỗi thường gặp cho sai MK hoặc token không hợp lệ
                            // Kiểm tra nội dung errorBody nếu có để phân biệt rõ hơn
                            if (errorBody != null && errorBody.contains("password", ignoreCase = true)) {
                                apiError = "Incorrect current password."
                            } else {
                                apiError = "Authentication failed. Please try again." // Lỗi chung hơn
                            }
                        }
                        else -> {
                            apiError = "Verification failed: ${errorBody ?: "Unknown server error."}"
                        }
                    }
                    Toast.makeText(context, apiError, Toast.LENGTH_LONG).show()
                }
            } catch (e: IOException) {
                // Lỗi mạng
                Log.e("VerifyCurrentPassword", "Network error: ${e.message}", e)
                apiError = "Network error. Please check your connection."
                Toast.makeText(context, apiError, Toast.LENGTH_LONG).show()
            }
            catch (e: Exception) {
                // Lỗi khác (Serialization, etc.)
                Log.e("VerifyCurrentPassword", "Password verification exception: ${e.message}", e)
                apiError = "An unexpected error occurred."
                Toast.makeText(context, apiError, Toast.LENGTH_LONG).show()
            } finally {
                isLoading = false
            }
        }
    }


    // --- UI Column giữ nguyên cấu trúc, chỉ sửa Text và onClick ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFececec))
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /// Nút quay lại (đặt lên đầu)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
            // .padding(top = 16.dp) // Bỏ padding top 50dp ở IconButton
        ) {
            IconButton(onClick = { if (!isLoading) navController.popBackStack() }) { // Vô hiệu hóa khi đang tải
                Icon(
                    painter = painterResource(id = R.drawable.back_ic), // Đảm bảo drawable tồn tại
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.weight(1f)) // Đẩy nút về bên trái
        }

        Spacer(modifier = Modifier.height(60.dp))

        // Tiêu đề
        Text(
                // text = "Enter your current password to change email", // Văn bản gốc
                text = "Enter your current password to change password", // ++ SỬA ++
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), // Padding ngang cho text
        textAlign = TextAlign.Center // Căn giữa tiêu đề
        )
        // --- Spacer ---
        Spacer(modifier = Modifier.height(40.dp))

        // Ô nhập Password hiện tại
        OutlinedTextField(
            value = currentPassword, // Sử dụng biến đã đổi tên
            onValueChange = {
                currentPassword = it
                if (apiError.isNotEmpty()) apiError = "" // Xóa lỗi khi người dùng nhập
            },
            label = { Text("Current Password") },
            placeholder = { Text("Enter your current password", color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp), // Padding ngang cho TextField
            shape = RoundedCornerShape(12.dp), // Bo góc
            colors = TextFieldDefaults.colors( // Màu sắc
                focusedIndicatorColor = Color.DarkGray, // Đổi màu focus/unfocus nếu muốn
                unfocusedIndicatorColor = Color.Gray,
                focusedContainerColor = Color.White, // Nền trắng khi focus
                unfocusedContainerColor = Color.White, // Nền trắng khi unfocus
                errorContainerColor = Color(0xFFFFF0F0) // Nền hơi đỏ khi lỗi
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done, // Nút "Done" trên bàn phím
                keyboardType = KeyboardType.Password // Bàn phím kiểu mật khẩu
            ),
            keyboardActions = KeyboardActions(
                onDone = { if (!isLoading) verifyPasswordAndProceed() } // Gọi hàm xác thực khi nhấn Done
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), // Ẩn/hiện mật khẩu
            trailingIcon = { // Icon con mắt
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Hide password" else "Show password"
                IconButton(onClick = { passwordVisible = !passwordVisible }, enabled = !isLoading) {
                    Icon(imageVector = image, description)
                }
            },
            singleLine = true, // Chỉ cho phép nhập 1 dòng
            textStyle = TextStyle(fontSize = 18.sp), // Cỡ chữ nhập liệu
            isError = apiError.isNotEmpty(), // Hiển thị trạng thái lỗi
            enabled = !isLoading // Vô hiệu hóa TextField khi đang tải
        )

        // Hiển thị lỗi API
        if (apiError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = apiError,
                color = MaterialTheme.colorScheme.error, // Màu lỗi chuẩn
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth() // Chiếm hết chiều rộng để padding hoạt động đúng
                    .padding(horizontal = 20.dp) // Thêm padding ngang cho text lỗi
                // .align(Alignment.Start) // Không cần nếu đã fillMaxWidth
            )
        }

        Spacer(modifier = Modifier.weight(1f)) // Đẩy nút Next xuống dưới cùng

        // Nút Next hoặc Loading Indicator
        Box(
            contentAlignment = Alignment.Center, // Căn giữa nội dung (Button hoặc Indicator)
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 16.dp, end = 16.dp) // Padding quanh Box
                .height(48.dp) // Đặt chiều cao cố định cho Box để Indicator không nhảy layout
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp)) // Kích thước Indicator
            } else {
                Button(
                    onClick = { verifyPasswordAndProceed() }, // Gọi hàm xác thực
                    modifier = Modifier
                        .fillMaxWidth(0.7f) // Chiếm 70% chiều rộng Box
                        .height(48.dp), // Chiều cao Button
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.DarkGray, // Màu nền Button
                        contentColor = Color.White, // Màu chữ/icon Button
                        disabledContainerColor = Color.Gray, // Màu nền khi bị vô hiệu hóa
                        disabledContentColor = Color.LightGray // Màu chữ/icon khi bị vô hiệu hóa
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp), // Đổ bóng nhẹ
                    enabled = !isLoading && currentPassword.isNotBlank(), // Vô hiệu hóa khi đang tải hoặc chưa nhập gì
                    shape = RoundedCornerShape(50) // Bo tròn mạnh
                ) {
                    Text(
                        text = "Next",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp)) // Khoảng cách giữa chữ và icon
                    Icon(
                        painter = painterResource(id = R.drawable.navigate_next), // Đảm bảo drawable tồn tại
                        contentDescription = "Next",
                        modifier = Modifier.size(20.dp) // Kích thước icon nhỏ hơn
                    )
                }
            }
        }
    }
}