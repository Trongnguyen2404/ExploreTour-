package com.example.vivu_application.view.profile.detail

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // ++ THÊM ++
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll // ++ THÊM ++
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign // ++ THÊM ++
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.vivu_application.data.model.UserProfile // Đảm bảo import đúng
import com.example.vivu_application.data.network.RetrofitClient
import com.example.vivu_application.navigation.BottomNavigationBar
import kotlinx.coroutines.launch
import com.example.vivu_application.R
import com.example.vivu_application.data.local.TokenManager

// --- Hàm tiện ích để che email ---
fun maskEmail(email: String?): String {
    if (email.isNullOrBlank() || !email.contains('@')) {
        return "--" // Trả về giá trị mặc định nếu email không hợp lệ
    }
    val parts = email.split('@')
    if (parts.size != 2) {
        return "--" // Xử lý trường hợp '@' không hợp lệ
    }
    val localPart = parts[0]
    val domainPart = parts[1]

    val maskedLocalPart = when {
        localPart.length <= 3 -> "***" // Che hết nếu quá ngắn
        else -> localPart.substring(0, 3) + "*".repeat(localPart.length - 3) // Che từ ký tự thứ 4
    }
    return "$maskedLocalPart@$domainPart"
}
// --- Kết thúc hàm tiện ích ---

@Composable
fun ProfilePage(navController: NavController) {

    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var apiError by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val authApiService = RetrofitClient.authApiService
    val scrollState = rememberScrollState() // Cho phép nội dung dài có thể cuộn

    fun fetchUserProfile() {
        isLoading = true
        apiError = null
        coroutineScope.launch {
            try {
                val response = authApiService.getUserProfile()

                if (response.isSuccessful) {
                    userProfile = response.body()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Failed to load profile."
                    if (response.code() == 401 || response.code() == 403) {
                        apiError = "Session expired. Please log in again." // Thông báo rõ ràng hơntint = Color(0xFF00BCD4)

                        // ++ XÓA TOKEN VÀ ĐIỀU HƯỚNG VỀ LOGIN ++
                        coroutineScope.launch { // Chạy trên coroutine để không block UI
                            TokenManager.clearTokens()
                            // Giả sử bạn có OnboardingUtils để cập nhật trạng thái đăng nhập chung
                            // onboardingUtils.setLogOut() // Bạn cần truyền OnboardingUtils vào ProfilePage nếu dùng
                            navController.navigate("login") {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    } else {
                        apiError = "Error loading profile: $errorBody"
                    }
                }
            } catch (e: Exception) {
                apiError = "Network error. Please check connection."
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        fetchUserProfile()
    }
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        //.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets.systemBars, // Không chịu ảnh hưởng bàn phím

        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1E8D9))
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ){
                if (!imeVisible) {
                    BottomNavigationBar(navController,)
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize() // Chiếm toàn bộ không gian trong Scaffold
                //.padding(innerPadding) // Áp dụng padding của Scaffold
                .padding(bottom = innerPadding.calculateBottomPadding()) // Chỉ áp dụng padding dưới cùng từ innerPadding
                .verticalScroll(scrollState), // Cho phép cuộn
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Phần Header với ảnh và nút ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp) // Giảm chiều cao một chút
                // Không cần padding ở đây nữa vì Scaffold đã xử lý
            ) {
                // Ảnh nền (Tùy chọn) - Có thể bỏ nếu muốn đơn giản hơn
                Image(
                    painter = painterResource(id = R.drawable.backg_top),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = (LocalConfiguration.current.screenHeightDp * 0.3f).dp), // 30% chiều cao màn hình
                    contentScale = ContentScale.Crop
                )

                // Nút Setting
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp, end = 16.dp) // Điều chỉnh padding nút setting
                ) {
                    IconButton(onClick = { navController.navigate("settingScreen") }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier.size(32.dp), // Kích thước icon setting
                            tint = Color.Black // Màu icon
                        )
                    }
                }

                // Ảnh hồ sơ
                ProfileImage(
                    // Truyền URL ảnh từ API HOẶC null để dùng ảnh mặc định
                    imageUrl = userProfile?.profilePictureUrl,
                    modifier = Modifier
                        .align(Alignment.BottomCenter) // Đặt ảnh ở dưới cùng của Box
                        //.offset(y = 40.dp) // Đẩy ảnh xuống một chút để đè lên phần dưới
                        //.padding(bottom = 10.dp)
                )
            }

            // --- Phần thông tin ---
           // Spacer(modifier = Modifier.height(50.dp)) // Khoảng cách sau ảnh profile

            // --- Hiển thị Loading, Error hoặc Nội dung Profile ---
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.padding(vertical = 32.dp))
                }
                apiError != null -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                        Text(
                            text = apiError ?: "An error occurred",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { fetchUserProfile() }) {
                            Text("Retry")
                        }
                    }
                }
                userProfile != null -> {
                    // Đã có dữ liệu profile
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp), // Padding ngang cho khối thông tin
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // ++ Hiển thị tên (hoặc username nếu tên trống) ++
                        Text(
                            text = if (!userProfile?.username.isNullOrBlank()) userProfile?.username!! else userProfile?.username ?: "User",
                            fontSize = 22.sp, // Cỡ chữ tên
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp) // Khoảng cách dưới tên
                        )

                        // ++ Hiển thị các dòng thông tin ++
                        ProfileInfoRow(
                            icon = R.drawable.profile_icon,
                            label = "NAME",
                            value = userProfile?.name ?: "Not Set" // Hiển thị "Not Set" nếu null/trống
                        )
                        Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(horizontal = 8.dp)) // Ngăn cách

                        ProfileInfoRow(
                            icon = R.drawable.date_icon,
                            label = "DATE OF BIRTH",
                            value = userProfile?.dateOfBirth ?: "Not Set"
                        )
                        Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(horizontal = 8.dp))

                        ProfileInfoRow(
                            icon = R.drawable.phone_icon,
                            label = "MOBILE",
                            value = userProfile?.mobile ?: "Not Set"
                        )
                        Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(horizontal = 8.dp))

                        ProfileInfoRow(
                            icon = R.drawable.mail_icon,
                            label = "EMAIL",
                            value = maskEmail(userProfile?.email) // ++ Sử dụng hàm che email ++
                        )
                        Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(horizontal = 8.dp))

                        ProfileInfoRow(
                            icon = R.drawable.lock_icon,
                            label = "PASSWORD",
                            value = "************" // Luôn hiển thị dấu *
                        )

                        Spacer(modifier = Modifier.height(10.dp)) // Tăng khoảng cách trước nút Edit

                        // --- Nút Edit ---
                        Button(
                            onClick = { navController.navigate("profilePageDetailEdit") }, // Đảm bảo route này đúng
                            modifier = Modifier
                                .fillMaxWidth(0.5f) // Chiếm 50% chiều rộng
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.DarkGray, // Màu nút Edit
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                            shape = RoundedCornerShape(50) // Bo tròn
                        ) {
                            Text(
                                text = "Edit Profile",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.navigate_next), // Icon Edit
                                contentDescription = "Edit",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp)) // Khoảng cách dưới cùng
                    }
                }
                else -> {
                    // Trường hợp không loading, không lỗi, nhưng profile vẫn null
                    Text("No profile data available.", modifier = Modifier.padding(32.dp))
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow(icon: Int, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp), // Tăng padding dọc
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = Color.Gray // Màu icon
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) { // Cho phép text dài xuống dòng
            Text(
                text = label,
                fontSize = 12.sp, // Cỡ chữ label nhỏ hơn
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(2.dp)) // Khoảng cách nhỏ
            Text(
                text = value,
                fontSize = 16.sp, // Cỡ chữ giá trị
                fontWeight = FontWeight.Medium,
                color = Color.Black // Màu chữ giá trị
            )
        }
    }
}

@Composable
fun ProfileImage(imageUrl: String?, modifier: Modifier = Modifier) {
    // --- Logic chọn ảnh (tạm thời tắt) ---
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
        // TODO: Gọi API upload ảnh
    }
    // --- Kết thúc logic chọn ảnh ---

    Card(
        shape = CircleShape,
        modifier = modifier
            .size(100.dp) // Kích thước ảnh đại diện
            .clip(CircleShape),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Thêm đổ bóng nhẹ
        // .clickable { launcher.launch("image/*") } // Bật lại nếu muốn đổi ảnh
    ) {
        // Ưu tiên ảnh mới chọn -> ảnh từ API -> ảnh mặc định
        val painter = when {
            imageUri != null -> rememberAsyncImagePainter(imageUri)
            !imageUrl.isNullOrBlank() -> rememberAsyncImagePainter(
                model = imageUrl,
                // Có thể thêm placeholder và error drawable cho Coil
                placeholder = painterResource(id = R.drawable.nha_trang), // Ảnh chờ load
                error = painterResource(id = R.drawable.emilia_clarke) // Ảnh lỗi load
            )
            else -> painterResource(R.drawable.avatar) // Ảnh mặc định của app
        }
        Image(
            painter = painter,
            contentDescription = "Profile Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Đảm bảo ảnh đầy và cắt phù hợp
        )
    }
}