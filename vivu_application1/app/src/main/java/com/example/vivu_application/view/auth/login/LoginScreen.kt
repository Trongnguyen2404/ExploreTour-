package com.example.vivu_application.view.auth.login
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vivu_application.view.onboarding.OnboardingUtils
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

import androidx.compose.material3.CircularProgressIndicator // ++ THÊM ++
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope // ++ THÊM ++
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext // ++ THÊM ++
import com.example.vivu_application.data.model.LoginRequestBody // ++ THÊM ++
import com.example.vivu_application.data.network.RetrofitClient // ++ THÊM ++
import kotlinx.coroutines.launch // ++ THÊM ++
import android.util.Log // ++ THÊM ++
import android.widget.Toast // ++ THÊM ++
import com.example.vivu_application.data.local.TokenManager
import com.example.vivu_application.R
// ++ GIẢ SỬ DATA CLASS RESPONSE TRÔNG NHƯ THẾ NÀY ++
// Tạo file LoginResponseBody.kt trong data/model
// data class LoginResponseBody(
//    val token: String?, // Hoặc một tên khác như accessToken
//    val username: String?,
//    // Thêm các trường khác nếu API trả về
// )

@Composable
fun LoginScreen(
    navController: NavController,
    onboardingUtils: OnboardingUtils
): Unit {

    var emailOrUsername by remember { mutableStateOf("") } // Đổi tên biến cho rõ ràng
    var password by remember { mutableStateOf("") }
    var apiError by remember { mutableStateOf("") } // Lỗi từ API
    var isLoading by remember { mutableStateOf(false) } // Trạng thái loading
    var passwordVisible by remember { mutableStateOf(false) }

    // Xóa dữ liệu giả
    // val fakeemail = "123@gmail.com"
    // val fakepassword = "12345678"

    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val authApiService = RetrofitClient.authApiService

    // Bỏ các hàm validate cũ (hoặc chỉ giữ lại kiểm tra rỗng)
    // fun validateEmail(email: String): Boolean { ... }
    // fun validatePassword(password: String): Boolean { ... }

    fun loginUser() {
        apiError = "" // Xóa lỗi cũ
        if (emailOrUsername.isBlank() || password.isBlank()) {
            apiError = "Email/Username and Password cannot be empty"
            return
        }

        isLoading = true
        focusManager.clearFocus()

        coroutineScope.launch {
            try {
                val requestBody = LoginRequestBody(
                    emailOrUsername = emailOrUsername.trim(),
                    password = password
                )
                Log.d("LoginScreen", "Attempting login with: $requestBody")
                // ++ GỌI API LOGIN THỰC TẾ ++
                // Giả sử endpoint là "login" và trả về LoginResponseBody
                val response = authApiService.login(requestBody) // Cần thêm hàm login() vào AuthApiService

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    Log.i("LoginScreen", "Login successful. Response Body: $loginResponse")

                    // ++ LẤY CẢ HAI TOKEN ++
                    val accessToken = loginResponse?.accessToken
                    val refreshToken = loginResponse?.refreshToken


                    // ++ KIỂM TRA CẢ HAI TOKEN ++
                    if (accessToken != null && accessToken.isNotBlank() && refreshToken != null && refreshToken.isNotBlank()) {
                        // --- LƯU TOKEN AN TOÀN QUA TOKEN MANAGER ---
                        TokenManager.saveTokens(accessToken, refreshToken) // Lưu cả hai
                        Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()

                        // Đánh dấu đã đăng nhập
                        onboardingUtils.setLogIn(true)

                        // Chuyển hướng về Home
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        // Lỗi: API thành công nhưng không trả về đủ token
                        Log.e("LoginScreen", "Login successful but tokens are missing in response body. Access: ${accessToken != null}, Refresh: ${refreshToken != null}")
                        apiError = "Login failed: Invalid response from server (missing tokens)."
                        Toast.makeText(context, "Login failed: Invalid server response.", Toast.LENGTH_LONG).show()
                    }

                } else {
                    // Lỗi từ API
                    val errorBody = response.errorBody()?.string() ?: "Login failed. Please try again."
                    Log.e("LoginScreen", "Login failed: ${response.code()} - $errorBody")
                    apiError = "Login failed: $errorBody" // Hiển thị lỗi từ server
                    Toast.makeText(context, "Error: $errorBody", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                // Lỗi mạng hoặc lỗi khác
                Log.e("LoginScreen", "Login exception: ${e.message}", e)
                apiError = "Network error or server unavailable.${e.message}"
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
            painter = painterResource(id = R.drawable.group_1), // Thay bằng logo/hình ảnh của bạn
            contentDescription = "Login image",
            modifier = Modifier.size(150.dp).padding(top = 40.dp) // Điều chỉnh kích thước và padding
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Sign in",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // --- Email/Username Input ---
        OutlinedTextField(
            value = emailOrUsername,
            onValueChange = {
                emailOrUsername = it
                if (apiError.isNotEmpty()) apiError = "" // Xóa lỗi khi nhập
            },
            label = { Text("Email or Username") },
            placeholder = { Text("Enter email or username", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Gray, unfocusedIndicatorColor = Color.Gray,
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                errorContainerColor = Color(0xFFFFF0F0)
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            singleLine = true,
            textStyle = TextStyle(fontSize = 18.sp), // Cỡ chữ vừa phải
            isError = apiError.isNotEmpty(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(20.dp)) // Giảm khoảng cách nếu muốn

        // --- Password Input ---
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (apiError.isNotEmpty()) apiError = "" // Xóa lỗi khi nhập
            },
            label = { Text("Password") },
            placeholder = { Text("Enter password", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Gray, unfocusedIndicatorColor = Color.Gray,
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                errorContainerColor = Color(0xFFFFF0F0)
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
            keyboardActions = KeyboardActions(onDone = { if (!isLoading) loginUser() }),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }, enabled = !isLoading) {
                    Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                }
            },
            singleLine = true,
            textStyle = TextStyle(fontSize = 18.sp),
            isError = apiError.isNotEmpty(),
            enabled = !isLoading
        )

        // --- Hiển thị lỗi API ---
        if (apiError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = apiError,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start).padding(start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- Forgot Password ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Forgot password?",
                modifier = Modifier
                    .clickable(enabled = !isLoading) { navController.navigate("forgotPassword") }
                    .padding(vertical = 4.dp),
                color = if (!isLoading) Color.Blue else Color.Gray,
                fontSize = 14.sp, // Cỡ chữ nhỏ hơn
                textDecoration = TextDecoration.Underline
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        // --- Login Button or Loading ---
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().height(50.dp)) { // Đặt chiều cao cho Box
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp)) // Kích thước phù hợp
            } else {
                Button(
                    onClick = { loginUser() },
                    modifier = Modifier.fillMaxWidth(), // Button chiếm hết chiều rộng Box
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFA1C9F1),
                        contentColor = Color.Black
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(50) // Bo tròn mạnh hơn
                ) {
                    Text(
                        text = "Sign in",
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(30.dp))

        // --- Create Account Link ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("", fontSize = 16.sp) // Thêm text thường
            Text(
                text = "Create account", // Chỉ phần link
                fontSize = 16.sp,
                modifier = Modifier
                    .clickable(enabled = !isLoading) { navController.navigate("createAccount") }
                    .padding(vertical = 8.dp),
                color = if (!isLoading) Color.Blue else Color.Gray,
                fontWeight = FontWeight.Bold, // In đậm link
                textDecoration = TextDecoration.Underline
            )
        }

        Spacer(modifier = Modifier.height(20.dp)) // Khoảng trống cuối
    }
}

// ++ Cần thêm LoginRequestBody.kt trong data/model ++
// data class LoginRequestBody(
//     val emailOrUsername: String, // Hoặc tách thành email/username nếu API yêu cầu
//     val password: String
// )

// ++ Cần thêm hàm login() vào AuthApiService.kt ++
// interface AuthApiService {
//     // ... các hàm khác ...
//
//     @POST("api/v1/auth/login") // Hoặc đường dẫn API login của bạn
//     suspend fun login(@Body requestBody: LoginRequestBody): Response<LoginResponseBody> // Hoặc Response<Unit> nếu không trả về body
// }