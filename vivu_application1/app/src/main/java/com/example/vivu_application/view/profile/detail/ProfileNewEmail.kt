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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.vivu_application.data.model.RequestEmailChangeBody // ++ THÊM ++
import com.example.vivu_application.data.network.RetrofitClient // ++ THÊM ++
import kotlinx.coroutines.launch // ++ THÊM ++
import com.example.vivu_application.R
@Composable
fun ProfileNewEmail(
    navController: NavHostController,
    currentPassword: String // ++ NHẬN MẬT KHẨU TỪ NAVIGATION ++
) {
    var newEmail by remember { mutableStateOf("") } // Đổi tên biến email thành newEmail
    var validationError by remember { mutableStateOf("") } // Lỗi validation local
    var apiError by remember { mutableStateOf("") } // Lỗi từ API
    var isLoading by remember { mutableStateOf(false) } // State loading

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val authApiService = RetrofitClient.authApiService
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Log.d("ProfileNewEmail", "Screen loaded with currentPassword available.")

    fun requestChangeAndProceed() {
        // --- Local Validation ---
        validationError = when {
            newEmail.isBlank() -> "New email cannot be empty"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches() -> "Invalid email format"
            // TODO: Thêm kiểm tra xem email mới có trùng email cũ không (cần lấy email cũ)
            // Nếu cần kiểm tra trùng email cũ, bạn cần truyền cả email cũ từ màn hình trước nữa.
            else -> "" // Hợp lệ local
        }

        if (validationError.isEmpty()) {
            // --- Call API ---
            apiError = ""
            isLoading = true
            focusManager.clearFocus()

            coroutineScope.launch {
                try {
                    val requestBody = RequestEmailChangeBody(
                        currentPassword = currentPassword, // Dùng mật khẩu đã nhận
                        newEmail = newEmail.trim()
                    )
                    Log.d("ProfileNewEmail", "Requesting email change: $requestBody")
                    // API này cũng cần được xác thực
                    val response = authApiService.requestEmailChange(requestBody)

                    if (response.isSuccessful) {
                        // Yêu cầu thành công, OTP đã được gửi đến email MỚI
                        Log.i("ProfileNewEmail", "Email change request successful for new email: ${newEmail.trim()}")
                        Toast.makeText(context, "Verification code sent to ${newEmail.trim()}", Toast.LENGTH_LONG).show()

                        // Điều hướng đến màn hình nhập OTP cho email mới
                        val newEmailAddress = newEmail.trim()
                        navController.navigate("profileOTPEmail/$newEmailAddress") {
                            // Tùy chọn popUpTo
                        }

                    } else {
                        // Lỗi từ API (mật khẩu sai lại?, email mới không hợp lệ phía server?,...)
                        val errorBody = response.errorBody()?.string() ?: "Failed to request email change."
                        Log.e("ProfileNewEmail", "Email change request failed: ${response.code()} - $errorBody")
                        apiError = "Error: $errorBody"
                        Toast.makeText(context, "Error: $errorBody", Toast.LENGTH_LONG).show()
                    }

                } catch (e: Exception) {
                    // Lỗi mạng hoặc lỗi khác
                    Log.e("ProfileNewEmail", "Email change request exception: ${e.message}", e)
                    apiError = "Network error or server unavailable."
                    Toast.makeText(context, "Network Error: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    isLoading = false
                }
            }
        } else {
            isLoading = false
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
        ) {
            IconButton(onClick = { navController.popBackStack() },
                modifier = Modifier.padding(top = 50.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back_ic),
                    contentDescription = "",
                    tint = Color.Blue
                )
            }
        }

        Spacer(modifier = Modifier.height(60.dp))

        // Tiêu đề
        Text(
            text = "You must enter your new email",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(30.dp))

        // --- Ô nhập Email mới ---
        OutlinedTextField(
            value = newEmail,
            onValueChange = {
                newEmail = it
                if (validationError.isNotEmpty() || apiError.isNotEmpty()) {
                    validationError = ""
                    apiError = ""
                }
            },
            label = { Text("New Email") }, // Đổi Label
            placeholder = { Text("Enter the new email address", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors( /* ... your colors ... */ errorContainerColor = Color(0xFFFFF0F0)),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Email),
            keyboardActions = KeyboardActions(onDone = { if (!isLoading) requestChangeAndProceed() }),
            singleLine = true,
            textStyle = TextStyle(fontSize = 18.sp),
            isError = validationError.isNotEmpty() || apiError.isNotEmpty(), // Hiển thị lỗi
            enabled = !isLoading
        )

        // --- Hiển thị lỗi validation và API ---
        if (validationError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = validationError, /* ... style lỗi ... */ )
        }
        if (apiError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = apiError, /* ... style lỗi ... */ )
        }

        Spacer(modifier = Modifier.weight(1f)) // Đẩy nút xuống

        // --- Nút Next hoặc Loading ---
        Box( /* ... giữ nguyên như ProfileEmail, chỉ đổi onClick ... */
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            } else {
                Button(
                    onClick = { requestChangeAndProceed() }, // Gọi hàm mới
                    /* ... style nút giữ nguyên ... */
                    modifier = Modifier.fillMaxWidth(0.6f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray, contentColor = Color.White),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(text = "Next", /* ... style chữ ... */)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.navigate_next),
                        contentDescription = "Arrow right",
                        modifier = Modifier
                            .size(30.dp))
                }
            }
        }
    }
}