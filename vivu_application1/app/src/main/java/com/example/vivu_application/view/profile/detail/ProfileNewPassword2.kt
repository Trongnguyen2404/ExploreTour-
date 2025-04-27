package com.example.vivu_application.view.profile.detail

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.vivu_application.data.model.SetNewPasswordBody // Import data class
import com.example.vivu_application.data.network.RetrofitClient // Import client
import kotlinx.coroutines.launch // Import coroutine
import com.example.vivu_application.R

@Composable
fun ProfileNewPassword2(
    navController: NavHostController,
    email: String, // Nhận email từ navigation
    otp: String    // Nhận OTP đã xác thực từ navigation
) {
    var newPassword by remember { mutableStateOf("") }
    var repeatNewPassword by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf("") } // Lỗi validation cục bộ
    var apiError by remember { mutableStateOf("") }        // Lỗi từ API
    var isLoading by remember { mutableStateOf(false) }    // Trạng thái loading

    val focusManager = LocalFocusManager.current
    var newPasswordVisible by remember { mutableStateOf(false) }
    var repeatNewPasswordVisible by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val authApiService = RetrofitClient.authApiService
    val scrollState = rememberScrollState()

    Log.d("SetNewPasswordAfterForgot", "Screen loaded for email: $email with OTP: $otp")

    // Hàm validate và gọi API đặt mật khẩu mới
    fun validateAndSetPassword() {
        // --- Local Validation ---
        validationError = when {
            newPassword.isBlank() -> "New password cannot be empty"
            newPassword.length < 8 -> "Password must be at least 8 characters" // Hoặc rule của bạn
            repeatNewPassword.isBlank() -> "Please confirm your new password"
            newPassword != repeatNewPassword -> "Passwords do not match"
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
                        email = email,           // Email từ argument
                        otp = otp,               // OTP từ argument
                        newPassword = newPassword,
                        repeatPassword = repeatNewPassword
                    )
                    Log.d("SetNewPasswordAfterForgot", "Setting new password request: $requestBody")
                    // Gọi API đặt lại mật khẩu cuối cùng của luồng quên mật khẩu
                    val response = authApiService.setNewPassword(requestBody)

                    if (response.isSuccessful) {
                        // Đặt lại thành công
                        Log.i("SetNewPasswordAfterForgot", "Password reset successful for: $email")
                        Toast.makeText(context, "Password reset successfully! Please log in.", Toast.LENGTH_LONG).show()


                        navController.navigate("profilePageDetailEdit") {
                            launchSingleTop = true
                        }

                    } else {
                        // Lỗi từ API (OTP sai/hết hạn lại?, lỗi server...)
                        val errorBody = response.errorBody()?.string() ?: "Failed to set new password."
                        Log.e("SetNewPasswordAfterForgot", "Set new password failed: ${response.code()} - $errorBody")
                        // Có thể API này cũng trả về lỗi nếu OTP không đúng dù đã qua màn hình trước?
                        if (errorBody.contains("OTP", ignoreCase = true)) {
                            apiError = "Invalid or expired OTP. Please try again."
                        } else {
                            apiError = "Error: $errorBody"
                        }
                        Toast.makeText(context, apiError, Toast.LENGTH_LONG).show()
                    }

                } catch (e: Exception) {
                    // Lỗi mạng hoặc lỗi khác
                    Log.e("SetNewPasswordAfterForgot", "Set new password exception: ${e.message}", e)
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

    // --- Giao diện UI ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFececec))
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Nút quay lại ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back_ic),
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(60.dp))

        // --- Tiêu đề ---
        Text(
            text = "Set Your New Password",
            fontSize = 24.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Enter and confirm the new password for\n$email", // Hiển thị email
            fontSize = 16.sp, color = Color.DarkGray,
            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(30.dp))

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
            isError = validationError.isNotEmpty(), // Hiển thị tất cả lỗi validation
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- Ô nhập Repeat New Password ---
        OutlinedTextField(
            value = repeatNewPassword,
            onValueChange = {
                repeatNewPassword = it
                if (validationError.contains("match", ignoreCase = true)) { validationError = "" }
            },
            label = { Text("Confirm New Password") },
            placeholder = { Text("Re-enter new password", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors( /* ... */ errorContainerColor = Color(0xFFFFF0F0)),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
            keyboardActions = KeyboardActions(onDone = { if (!isLoading) validateAndSetPassword() }), // Gọi hàm đặt MK
            visualTransformation = if (repeatNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (repeatNewPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { repeatNewPasswordVisible = !repeatNewPasswordVisible }, enabled = !isLoading) {
                    Icon(imageVector = image, contentDescription = "Toggle repeat new password visibility")
                }
            },
            singleLine = true,
            textStyle = TextStyle(fontSize = 18.sp),
            isError = validationError.contains("match", ignoreCase = true), // Chỉ hiển thị lỗi khớp
            enabled = !isLoading
        )

        // --- Hiển thị lỗi validation và API ---
        Column(modifier = Modifier.padding(start = 20.dp, top = 8.dp).fillMaxWidth()) {
            if (validationError.isNotEmpty()) {
                Text(text = validationError, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
            }
            if (apiError.isNotEmpty()) {
                Text(text = apiError, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.weight(1f)) // Đẩy nút xuống

        // --- Nút Complete Reset hoặc Loading ---
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            } else {
                Button(
                    onClick = { validateAndSetPassword() }, // Gọi hàm đặt MK
                    /* ... style Button ... */
                    modifier = Modifier.fillMaxWidth(0.6f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray, contentColor = Color.White),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                    // Kích hoạt nút khi cả hai trường không rỗng (hoặc thêm validation phức tạp hơn nếu muốn)
                    enabled = !isLoading && newPassword.isNotBlank() && repeatNewPassword.isNotBlank(),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(text = "Complete Reset", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}