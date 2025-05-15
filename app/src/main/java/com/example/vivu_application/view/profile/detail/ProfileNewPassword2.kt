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
                    // Gọi API đặt lại mật khẩu cuối cùng của luồng quên mật khẩu
                    val response = authApiService.setNewPassword(requestBody)

                    if (response.isSuccessful) {
                        // Đặt lại thành công
                        //Toast.makeText(context, "Password reset successfully!", Toast.LENGTH_LONG).show()


                        navController.navigate("profilePageDetailEdit") {
                            launchSingleTop = true
                        }

                    } else {
                        // Lỗi từ API (OTP sai/hết hạn lại?, lỗi server...)
                        val errorBody = response.errorBody()?.string() ?: "Failed to set new password."
                        // Có thể API này cũng trả về lỗi nếu OTP không đúng dù đã qua màn hình trước?
                        if (errorBody.contains("OTP", ignoreCase = true)) {
                            apiError = "Invalid or expired OTP. Please try again."
                        } else {
                            apiError = " "
                        }

                    }

                } catch (e: Exception) {
                    // Lỗi mạng hoặc lỗi khác

                    apiError = "Network error or server unavailable."
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
            modifier = Modifier.fillMaxWidth().padding(top = 60.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back_ic),
                    contentDescription = "Back",
                    tint = Color(0xFF00BCD4)
                )
            }
        }

        Spacer(modifier = Modifier.height(60.dp))

        // --- Tiêu đề ---

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Enter and confirm the new password for\n$email", // Hiển thị email
            fontSize = 20.sp,
            color = Color.DarkGray,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(30.dp))

        // --- Ô nhập New Password ---
        OutlinedTextField(
            value = newPassword,
            onValueChange = {
                newPassword = it
                if (validationError.isNotEmpty() || apiError.isNotEmpty()) { validationError = ""; apiError = "" }
            },
            placeholder = { Text("Enter new password ", color = Color.Gray) },
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
            placeholder = { Text("Re-enter new password", color = Color.Gray) },
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(bottom = 350.dp).offset(60.dp)
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