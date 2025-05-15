package com.example.vivu_application.view.profile.detail



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
import com.example.vivu_application.data.model.RequestEmailChangeBody // ++ THÊM ++
import com.example.vivu_application.data.network.RetrofitClient // ++ THÊM ++
import kotlinx.coroutines.launch // ++ THÊM ++
import com.example.vivu_application.R
import com.example.vivu_application.data.model.RequestOtpBody

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


    fun requestChangeAndProceed() {
        // --- Local Validation ---
        validationError = when {
            newEmail.isBlank() -> "New email cannot be empty"
            !newEmail.lowercase().endsWith("@gmail.com") -> "Email must be a Gmail address (@gmail.com)"
            else -> ""
        }

        if (validationError.isEmpty()) {
            isLoading = true
            coroutineScope.launch {
                try {
                    // ---- SỬA Ở ĐÂY ----
                    // 1. Tạo đúng Request Body cho việc thay đổi email
                    val requestBody = RequestEmailChangeBody(
                        currentPassword = currentPassword, // currentPassword nhận từ navigation
                        newEmail = newEmail.trim()
                    )
                    // 2. Gọi đúng API endpoint để YÊU CẦU OTP cho việc thay đổi email
                    val response = authApiService.requestEmailChange(requestBody)
                    // ---- KẾT THÚC PHẦN SỬA ----

                    if (response.isSuccessful) {
                        val userEmail = newEmail.trim()
                        navController.navigate("profileOTPEmail/$userEmail")
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        // Cập nhật thông báo lỗi cho phù hợp với API requestEmailChange
                        // Ví dụ: server có thể trả về lỗi nếu currentPassword sai,
                        // hoặc newEmail trùng với email hiện tại của user.
                        apiError = "Cannot change email" // Hiển thị chi tiết hơn nếu có
                    }
                } catch (e: Exception) {
                    apiError = "Network error or server unavailable."
                } finally {
                    isLoading = false
                }
            }
        } else {
            isLoading = false // Dừng loading nếu validation thất bại
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
                modifier = Modifier.padding(top = 60.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back_ic),
                    contentDescription = "Back",
                    tint = Color(0xFF00BCD4)
                )
            }
        }

        Spacer(modifier = Modifier.height(60.dp))

        // Tiêu đề
        Text(
            text = "You must enter your new email",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(),

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
            placeholder = { Text("Enter the new email address", color = Color.Gray) },
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
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Email),
            keyboardActions = KeyboardActions(onDone = { if (!isLoading) requestChangeAndProceed() }),
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
                modifier = Modifier.align(Alignment.Start).padding(start = 20.dp)
                )
        }
        if (apiError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = apiError,
                fontSize = 14.sp,
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start).padding(start = 20.dp)
                )
        }

        Spacer(modifier = Modifier.weight(1f)) // Đẩy nút xuống

        // --- Nút Next hoặc Loading ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 350.dp)
                .offset(60.dp)
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
                    Text(text = "Next")
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