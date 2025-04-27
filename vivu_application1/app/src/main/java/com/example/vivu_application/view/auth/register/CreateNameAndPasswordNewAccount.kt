package com.example.vivu_application.view.auth.register

import android.util.Log // ++ ADD Log
import android.widget.Toast // ++ ADD Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // ++ ADD for scrolling if needed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll // ++ ADD for scrolling
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext // ++ ADD LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign // ++ ADD TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vivu_application.data.model.CompleteRegistrationBody // ++ ADD data class
import com.example.vivu_application.data.network.RetrofitClient // ++ ADD retrofit
import com.example.vivu_application.view.onboarding.OnboardingUtils
import kotlinx.coroutines.launch // ++ ADD coroutine launch
import com.example.vivu_application.R

@Composable
fun CreateNameAndPasswordNewAccount(
    navController: NavController,
    onboardingUtils: OnboardingUtils,
    email: String, // ++ RECEIVE EMAIL ++
    otp: String    // ++ RECEIVE OTP ++
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repassword by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf("") } // For local validation errors
    var apiError by remember { mutableStateOf("") } // For API errors
    var isLoading by remember { mutableStateOf(false) } // Loading state
    var passwordVisible by remember { mutableStateOf(false) }
    var repasswordVisible by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val authApiService = RetrofitClient.authApiService
    val scrollState = rememberScrollState() // For making column scrollable

    fun validateAndProceed() {
        // --- Local Validation First ---
        validationError = when {
            username.isBlank() -> "Username cannot be empty"
            // Add more specific username validation if needed (e.g., regex for allowed chars)
            username.length < 6 -> "Username must be at least 6 characters" // Example length
            password.isBlank() -> "Password cannot be empty"
            password.length < 8 -> "Password must be at least 8 characters" // Example length
            // Add password complexity rules if needed (uppercase, number, symbol)
            repassword.isBlank() -> "Please confirm your password"
            password != repassword -> "Passwords do not match"
            else -> "" // No local validation errors
        }

        if (validationError.isEmpty()) {
            // --- Call API if Local Validation Passes ---
            apiError = "" // Clear previous API error
            isLoading = true
            focusManager.clearFocus()

            coroutineScope.launch {
                try {
                    // ++ ĐẢM BẢO SỬ DỤNG ĐÚNG email và otp ĐÃ NHẬN ++
                    val requestBody = CompleteRegistrationBody(
                        email = email,         // email từ tham số hàm
                        otp = otp,             // otp từ tham số hàm
                        username = username.trim(),
                        password = password,
                        repeatPassword = repassword
                    )

                    // Log request body trước khi gửi đi
                    Log.d("CreateNamePassword", "Sending complete registration request: $requestBody")

                    val response = authApiService.completeRegistration(requestBody)

                    // ... xử lý response thành công hoặc thất bại ...
                    if (response.isSuccessful) {
                        // --- Registration Successful ---
                        Log.i("RegisterComplete", "Registration successful API call for email: $email") // Log 1: Xác nhận vào đây
                        Toast.makeText(context, "Account created successfully!", Toast.LENGTH_LONG).show()
                        onboardingUtils.setAccountCreated(true) // Mark account as created

                        // ++ THÊM LOG NGAY TRƯỚC KHI NAVIGATE ++
                        Log.d("RegisterComplete", "Attempting navigation to 'login' route...")

                        // Navigate to login, clearing the registration stack
                        navController.navigate("login") {
                            // Đảm bảo "createAccount" là route bắt đầu luồng đăng ký này trong AppNavigation
                            popUpTo("createAccount") { inclusive = true }
                            launchSingleTop = true
                        }

                        // ++ THÊM LOG NGAY SAU KHI NAVIGATE (để xem lệnh có chạy qua không) ++
                        Log.d("RegisterComplete", "navController.navigate('login') called.")

                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Registration failed"
                        Log.e("CreateNamePassword", "Complete registration failed: ${response.code()} - $errorBody")
                        apiError = "Error: $errorBody" // Hiển thị lỗi API
                        // ... Toast ...
                    }
                } catch (e: Exception) {
                    // ... xử lý exception ...
                    Log.e("CreateNamePassword", "Complete registration exception: ${e.message}", e)
                    apiError = "Network error: ${e.message}"
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
            .padding(horizontal = 32.dp)
            .verticalScroll(scrollState), // Make content scrollable
        verticalArrangement = Arrangement.Top, // Adjust if needed with scroll
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.group_1),
            contentDescription = "Registration Illustration",
            modifier = Modifier.size(200.dp).padding(top = 20.dp) // Add padding if needed
        )

        Spacer(modifier = Modifier.height(30.dp)) // Adjust spacing

        Text(
            text = "Complete Registration", // Update Title
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "Create your username and password", // Update description
            fontSize = 18.sp, // Slightly smaller
            // fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(25.dp))

        // --- Username Field ---
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                if (validationError.isNotEmpty() || apiError.isNotEmpty()) { // Clear errors on input
                    validationError = ""
                    apiError = ""
                }
            },
            label = { Text("Username") }, // Use Label
            placeholder = { Text("Enter your username", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors( /* ... your colors ... */ ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next, // Go to password
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            singleLine = true,
            isError = validationError.contains("Username", ignoreCase = true) || apiError.contains("Username", ignoreCase = true), // Show error state
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- Password Field ---
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (validationError.isNotEmpty() || apiError.isNotEmpty()) {
                    validationError = ""
                    apiError = ""
                }
            },
            label = { Text("Password") },
            placeholder = { Text("Enter your password", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors( /* ... your colors ... */ ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Password
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }, enabled = !isLoading) {
                    Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                }
            },
            singleLine = true,
            isError = validationError.contains("Password", ignoreCase = true) || validationError.contains("match", ignoreCase = true) || apiError.contains("Password", ignoreCase = true),
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- Re-enter Password Field ---
        OutlinedTextField(
            value = repassword,
            onValueChange = {
                repassword = it
                if (validationError.isNotEmpty() || apiError.isNotEmpty()) {
                    validationError = ""
                    apiError = ""
                }
            },
            label = { Text("Confirm Password") },
            placeholder = { Text("Re-enter your password", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors( /* ... your colors ... */ ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done, // Done action here
                keyboardType = KeyboardType.Password
            ),
            keyboardActions = KeyboardActions(
                onDone = { if (!isLoading) validateAndProceed() } // Call validation/API on Done
            ),
            visualTransformation = if (repasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (repasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { repasswordVisible = !repasswordVisible }, enabled = !isLoading) {
                    Icon(imageVector = image, contentDescription = if (repasswordVisible) "Hide password" else "Show password")
                }
            },
            singleLine = true,
            isError = validationError.contains("Password", ignoreCase = true) || validationError.contains("match", ignoreCase = true), // Only check match/password errors here
            enabled = !isLoading
        )

        // --- Display Validation and API Errors ---
        if (validationError.isNotEmpty()) {
            Text(
                text = validationError,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp).align(Alignment.Start)
            )
        }
        if (apiError.isNotEmpty()) {
            Text(
                text = apiError, // Display API specific errors
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp).align(Alignment.Start)
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        // --- Loading Indicator or Button ---
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(vertical = 16.dp))
        } else {
            Button(
                onClick = { validateAndProceed() },
                modifier = Modifier.fillMaxWidth().height(50.dp), // Standard height
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFA1C9F1),
                    contentColor = Color.Black
                ),
                elevation = ButtonDefaults.buttonElevation( defaultElevation = 8.dp ),
                enabled = !isLoading // Disable button when loading
            ) {
                Text(
                    text = "Confirm",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold) // Adjust text style
                )
            }
        }
        Spacer(modifier = Modifier.height(40.dp)) // Space at the bottom
    }
}