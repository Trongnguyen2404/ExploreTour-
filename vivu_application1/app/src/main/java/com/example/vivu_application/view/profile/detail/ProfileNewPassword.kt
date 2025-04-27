package com.example.vivu_application.view.profile.detail // Giữ package hoặc đổi nếu muốn

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable // Giữ lại import này
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
import androidx.compose.ui.focus.FocusDirection
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
import androidx.compose.ui.text.style.TextDecoration // Giữ lại import này
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.vivu_application.data.model.ChangePasswordRequestBody // Đảm bảo import đúng
import com.example.vivu_application.data.network.RetrofitClient
import kotlinx.coroutines.launch
import com.example.vivu_application.R

@Composable
// ++ Đổi tên Composable cho rõ chức năng ++
fun ProfileNewPassword(navController: NavHostController) {
    // Bỏ tham số email, verificationData vì không cần thiết ở màn hình này

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var repeatNewPassword by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf("") } // Lỗi validation cục bộ
    var apiError by remember { mutableStateOf("") }        // Lỗi trả về từ API
    var isLoading by remember { mutableStateOf(false) }    // Trạng thái loading

    val focusManager = LocalFocusManager.current
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var repeatNewPasswordVisible by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val authApiService = RetrofitClient.authApiService
    val scrollState = rememberScrollState()

    // Hàm xử lý thay đổi mật khẩu
    fun changePasswordAndProceed() {
        // --- Kiểm tra validation phía Client trước ---
        validationError = when {
            currentPassword.isBlank() -> "Current password cannot be empty"
            newPassword.isBlank() -> "New password cannot be empty"
            newPassword.length < 8 -> "New password must be at least 8 characters" // Hoặc theo rule của bạn
            repeatNewPassword.isBlank() -> "Please confirm your new password"
            newPassword != repeatNewPassword -> "New passwords do not match"
            currentPassword == newPassword -> "New password cannot be the same as the current password"
            else -> "" // Không có lỗi validation
        }

        // Nếu validation phía client OK -> Gọi API
        if (validationError.isEmpty()) {
            apiError = "" // Xóa lỗi API cũ
            isLoading = true
            focusManager.clearFocus()

            coroutineScope.launch {
                try {
                    val requestBody = ChangePasswordRequestBody(
                        currentPassword = currentPassword,
                        newPassword = newPassword,
                        repeatNewPassword = repeatNewPassword // Đảm bảo key này đúng trong data class
                    )
                    Log.d("ChangePassword", "Attempting to change password. Request: $requestBody")
                    // Gọi API đổi mật khẩu - Yêu cầu phải có Authorization header (từ Interceptor)
                    val response = authApiService.changePassword(requestBody)

                    if (response.isSuccessful) {
                        // Thành công
                        Log.i("ChangePassword", "Password changed successfully.")
                        Toast.makeText(context, "Password updated successfully!", Toast.LENGTH_LONG).show()
                        // Quay lại màn hình trước đó (Profile Detail, Settings,...)
                        navController.popBackStack()

                    } else {
                        // Lỗi từ API
                        val errorBody = response.errorBody()?.string() ?: "Failed to change password."
                        Log.e("ChangePassword", "Change password failed: ${response.code()} - $errorBody")
                        // Cố gắng đưa ra thông báo lỗi cụ thể hơn
                        if (response.code() == 400 || response.code() == 401 || response.code() == 403) {
                            // Thường các mã này chỉ ra mật khẩu hiện tại không đúng
                            apiError = "Incorrect current password."
                        } else {
                            // Các lỗi khác từ server
                            apiError = "Error changing password: $errorBody"
                        }
                        Toast.makeText(context, apiError, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    // Lỗi mạng hoặc không xác định
                    Log.e("ChangePassword", "Change password exception: ${e.message}", e)
                    apiError = "Network error or server unavailable."
                    Toast.makeText(context, apiError, Toast.LENGTH_LONG).show()
                } finally {
                    isLoading = false // Kết thúc loading
                }
            }
        } else {
            // Nếu validation thất bại, đảm bảo không loading
            isLoading = false
        }
    }

    // --- Giao diện UI ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFececec))
            .padding(16.dp)
            .verticalScroll(scrollState), // Cho phép cuộn
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Nút quay lại ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() },
                modifier = Modifier.padding(top = 16.dp) // Giảm padding top
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back_ic),
                    contentDescription = "Back",
                    tint = Color.Black // Đổi màu nếu muốn
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp)) // Giảm khoảng cách

        // --- Tiêu đề ---
        Text(
            text = "Change Password", // Tiêu đề rõ ràng
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(30.dp))

        // --- Ô nhập Current Password ---
        OutlinedTextField(
            value = currentPassword,
            onValueChange = {
                currentPassword = it
                // Xóa lỗi khi người dùng bắt đầu nhập lại
                if (validationError.isNotEmpty() || apiError.isNotEmpty()) { validationError = ""; apiError = "" }
            },
            label = { Text("Current Password") },
            placeholder = { Text("Enter your current password", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors( /* ... */ errorContainerColor = Color(0xFFFFF0F0)),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Password),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (currentPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }, enabled = !isLoading) {
                    Icon(imageVector = image, contentDescription = "Toggle current password visibility")
                }
            },
            singleLine = true,
            textStyle = TextStyle(fontSize = 18.sp),
            // Lỗi khi validation cần hoặc API báo sai MK hiện tại
            isError = validationError.contains("current", ignoreCase = true) || apiError.contains("current", ignoreCase = true),
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- Ô nhập New Password ---
        OutlinedTextField(
            value = newPassword,
            onValueChange = {
                newPassword = it
                if (validationError.isNotEmpty() || apiError.isNotEmpty()) { validationError = ""; apiError = "" }
            },
            label = { Text("New Password") },
            placeholder = { Text("Enter new password (min 8 chars)", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors( /* ... */ errorContainerColor = Color(0xFFFFF0F0)),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Password),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (newPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { newPasswordVisible = !newPasswordVisible }, enabled = !isLoading) {
                    Icon(imageVector = image, contentDescription = "Toggle new password visibility")
                }
            },
            singleLine = true,
            textStyle = TextStyle(fontSize = 18.sp),
            // Lỗi khi validation cần (trừ lỗi current)
            isError = validationError.isNotEmpty() && !validationError.contains("current", ignoreCase = true),
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- Ô nhập Repeat New Password ---
        OutlinedTextField(
            value = repeatNewPassword,
            onValueChange = {
                repeatNewPassword = it
                // Chỉ xóa lỗi khớp khi nhập lại
                if (validationError.contains("match", ignoreCase = true)) { validationError = "" }
            },
            label = { Text("Confirm New Password") },
            placeholder = { Text("Re-enter new password", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors( /* ... */ errorContainerColor = Color(0xFFFFF0F0)),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
            keyboardActions = KeyboardActions(onDone = { if (!isLoading) changePasswordAndProceed() }), // Gọi hàm đổi MK
            visualTransformation = if (repeatNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (repeatNewPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { repeatNewPasswordVisible = !repeatNewPasswordVisible }, enabled = !isLoading) {
                    Icon(imageVector = image, contentDescription = "Toggle repeat new password visibility")
                }
            },
            singleLine = true,
            textStyle = TextStyle(fontSize = 18.sp),
            // Chỉ hiển thị lỗi nếu không khớp
            isError = validationError.contains("match", ignoreCase = true),
            enabled = !isLoading
        )

        // --- Hiển thị lỗi validation và API ---
        Column(modifier = Modifier.padding(start = 20.dp, top = 8.dp).fillMaxWidth()) {
            if (validationError.isNotEmpty()) {
                Text(text = validationError, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp)) // Khoảng cách nhỏ giữa các lỗi
            }
            if (apiError.isNotEmpty()) {
                Text(text = apiError, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
            }
        }


        // --- Link Forgot Password ---
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Forgot password?",
                // **QUAN TRỌNG:** Route này phải dẫn đến màn hình nhập email quên mật khẩu
                // Ví dụ: "profileForgotPasswordEmail" hoặc tên route bạn đã định nghĩa
                modifier = Modifier
                    .clickable(enabled = !isLoading) { navController.navigate("profilePagePassword") } // ++ Đảm bảo route đúng ++
                    .padding(vertical = 4.dp),
                color = if (!isLoading) Color.Blue else Color.Gray,
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline
            )
        }

        Spacer(modifier = Modifier.weight(1f)) // Đẩy nút xuống

        // --- Nút Confirm hoặc Loading ---
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            } else {
                Button(
                    onClick = { changePasswordAndProceed() }, // Gọi hàm đổi MK
                    /* ... giữ nguyên style Button ... */
                    modifier = Modifier.fillMaxWidth(0.6f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray, contentColor = Color.White),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                    enabled = !isLoading, // Nút luôn bật trừ khi loading
                    shape = RoundedCornerShape(50)
                ) {
                    Text(text = "Confirm Change", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}