package com.example.vivu_application.view.profile.detail

import android.app.DatePickerDialog // Import DatePickerDialog
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.vivu_application.data.model.UpdateProfileRequestBody // ++ THÊM ++
import com.example.vivu_application.data.network.RetrofitClient // ++ THÊM ++
import kotlinx.coroutines.launch // ++ THÊM ++
import java.text.SimpleDateFormat // ++ THÊM ++
import java.util.* // ++ THÊM ++
import com.example.vivu_application.R
fun convertToApiDate(displayDate: String): String? {

    return try {
        val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = displayFormat.parse(displayDate)
        if (date != null) apiFormat.format(date) else null
    } catch (e: Exception) {
        Log.e("DateConversion", "Error parsing display date: $displayDate", e)
        null // Trả về null nếu parse lỗi
    }
}

fun convertToDisplayDate(apiDate: String?): String {
    // Chuyển từ yyyy-MM-dd sang dd/MM/yyyy
    if (apiDate.isNullOrBlank()) return ""
    return try {
        val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = apiFormat.parse(apiDate)
        if (date != null) displayFormat.format(date) else ""
    } catch (e: Exception) {
        Log.e("DateConversion", "Error parsing API date: $apiDate", e)
        "" // Trả về chuỗi rỗng nếu parse lỗi
    }
}


@Composable
fun ProfilePageDetailEdit(navController: NavHostController) {

    // --- State cho dữ liệu nhập liệu ---
    var name by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") } // Lưu ở định dạng dd/MM/yyyy
    var mobile by remember { mutableStateOf("") }
    var currentProfilePictureUrl by remember { mutableStateOf<String?>(null) } // Lưu URL ảnh hiện tại từ API
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) } // Lưu Uri ảnh mới chọn

    // --- State cho API call và UI ---
    var isLoadingData by remember { mutableStateOf(true) } // Loading dữ liệu ban đầu
    var isSaving by remember { mutableStateOf(false) } // Loading khi lưu
    var apiError by remember { mutableStateOf<String?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) } // Dialog xác nhận lưu

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authApiService = RetrofitClient.authApiService
    val scrollState = rememberScrollState()

    // --- Hàm tải dữ liệu profile ban đầu ---
    fun fetchInitialProfile() {
        isLoadingData = true
        apiError = null
        coroutineScope.launch {
            try {
                Log.d("ProfileEdit", "Fetching initial profile...")
                val response = authApiService.getUserProfile()
                if (response.isSuccessful) {
                    val profile = response.body()
                    profile?.let {
                        name = it.name ?: ""
                        // Chuyển đổi ngày từ API (yyyy-MM-dd) sang định dạng hiển thị (dd/MM/yyyy)
                        dateOfBirth = convertToDisplayDate(it.dateOfBirth)
                        mobile = it.mobile ?: ""
                        currentProfilePictureUrl = it.profilePictureUrl // Lưu URL ảnh hiện tại
                        Log.i("ProfileEdit", "Initial profile loaded: $it")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Failed to load profile data."
                    Log.e("ProfileEdit", "Fetch initial profile failed: ${response.code()} - $errorBody")
                    apiError = "Could not load current profile data."
                    Toast.makeText(context, apiError, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("ProfileEdit", "Exception fetching initial profile: ${e.message}", e)
                apiError = "Network error loading data."
                Toast.makeText(context, apiError, Toast.LENGTH_LONG).show()
            } finally {
                isLoadingData = false
            }
        }
    }

    // --- Gọi tải dữ liệu ban đầu ---
    LaunchedEffect(Unit) {
        fetchInitialProfile()
    }

    // --- Hàm xử lý cập nhật profile ---
    fun updateProfile() {
        // TODO: Thêm validation nếu cần

        isSaving = true
        apiError = null

        val apiDob = convertToApiDate(dateOfBirth)

        // --- Xử lý URL ảnh ---
        // Biến này sẽ chứa URL cuối cùng để gửi đi API update profile
        var finalImageUrl: String? = currentProfilePictureUrl // Mặc định là URL hiện tại

        if (selectedImageUri != null) {
            // Người dùng đã chọn ảnh mới
            Log.w("ProfileEdit", "New image selected (Uri: $selectedImageUri). Upload logic needed!")
            // !!-- PHẦN NÀY CẦN LOGIC UPLOAD --!!
            // 1. Gọi hàm uploadImage(selectedImageUri) -> Hàm này gọi API upload riêng
            // 2. Nhận newUrl từ kết quả upload
            // 3. Cập nhật finalImageUrl = newUrl
            // Hiện tại, chúng ta chưa có URL mới nên không thay đổi finalImageUrl
            // Hoặc bạn có thể chọn gửi null nếu chưa muốn cập nhật URL ảnh
            // finalImageUrl = null; // Ví dụ nếu muốn xóa ảnh hoặc chờ upload
            Toast.makeText(context, "Image upload not implemented yet", Toast.LENGTH_SHORT).show()
            // Không nên tiếp tục lưu nếu upload chưa xong, tạm thời chỉ log
            // return // Có thể dừng ở đây nếu việc upload là bắt buộc
        }
        // Nếu người dùng không chọn ảnh mới, finalImageUrl sẽ giữ nguyên giá trị currentProfilePictureUrl

        val requestBody = UpdateProfileRequestBody(
            fullName = name.trim().takeIf { it.isNotEmpty() },
            dateOfBirth = apiDob,
            phoneNumber = mobile.trim().takeIf { it.isNotEmpty() },
            profilePictureUrl = finalImageUrl // Gửi URL đã xử lý
        )

        Log.d("ProfileEdit", "Updating profile with request: $requestBody")

        coroutineScope.launch {
            try {
                // Gọi API cập nhật profile (PUT hoặc PATCH)
                val response = authApiService.updateUserProfile(requestBody)

                if (response.isSuccessful) {
                    Log.i("ProfileEdit", "Profile updated successfully. Response: ${response.body()}")
                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    // TODO: Cập nhật lại cache hoặc state ở màn hình ProfilePage nếu cần
                    navController.popBackStack() // Quay lại màn hình trước
                } else {
                    // Lỗi từ API
                    val errorBody = response.errorBody()?.string() ?: "Failed to update profile."
                    Log.e("ProfileEdit", "Update profile failed: ${response.code()} - $errorBody")
                    apiError = "Error updating profile: $errorBody"
                    Toast.makeText(context, apiError, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                // Lỗi mạng hoặc khác
                Log.e("ProfileEdit", "Exception updating profile: ${e.message}", e)
                apiError = "Network error during update."
                Toast.makeText(context, apiError, Toast.LENGTH_LONG).show()
            } finally {
                isSaving = false // Kết thúc trạng thái lưu
                showConfirmDialog = false // Đóng dialog sau khi xử lý xong
            }
        }
    }

    // --- Date Picker Dialog ---
    val calendar = Calendar.getInstance()
    // Set năm hiện tại từ calendar nếu dateOfBirth rỗng hoặc lỗi parse
    val initialYear = remember(dateOfBirth) {
        try { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateOfBirth)?.let { calendar.time = it; calendar.get(Calendar.YEAR) } ?: calendar.get(Calendar.YEAR) } catch (e: Exception) { calendar.get(Calendar.YEAR) }
    }
    val initialMonth = remember(dateOfBirth) {
        try { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateOfBirth)?.let { calendar.time = it; calendar.get(Calendar.MONTH) } ?: calendar.get(Calendar.MONTH) } catch (e: Exception) { calendar.get(Calendar.MONTH) }
    }
    val initialDay = remember(dateOfBirth) {
        try { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateOfBirth)?.let { calendar.time = it; calendar.get(Calendar.DAY_OF_MONTH) } ?: calendar.get(Calendar.DAY_OF_MONTH) } catch (e: Exception) { calendar.get(Calendar.DAY_OF_MONTH) }
    }

    val datePickerDialog = remember { // Tạo dialog chỉ một lần
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                // Format ngày theo dd/MM/yyyy
                dateOfBirth = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
            },
            initialYear, initialMonth, initialDay
        ).apply {
            // Set giới hạn ngày (ví dụ)
            val minCal = Calendar.getInstance().apply { set(1900, 1, 1) }
            val maxCal = Calendar.getInstance().apply { set(2015, 12, 31) }
            datePicker.minDate = minCal.timeInMillis
            datePicker.maxDate = maxCal.timeInMillis
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3E3E3))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(top = 30.dp)
                    .size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back_ic),
                    contentDescription = "Back",
                    tint = Color(0xFF00BCD4)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        ProfileImagesEdit(
            modifier = Modifier.fillMaxWidth(),
            initialImageUrl = currentProfilePictureUrl, // Truyền URL ảnh hiện tại
            selectedImageUri = selectedImageUri, // Truyền Uri ảnh mới chọn
            onImageSelected = { uri -> selectedImageUri = uri } // Cập nhật Uri khi chọn ảnh
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "Edit Your Profile", // Đổi tiêu đề
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (isLoadingData) {
            CircularProgressIndicator(modifier = Modifier.padding(vertical = 32.dp))
        } else {

            // Trường Name
            OutlinedTextField( // Sử dụng OutlinedTextField cho đồng bộ
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                enabled = !isSaving, // Vô hiệu hóa khi đang lưu
                shape = RoundedCornerShape(8.dp) // Bo góc nhẹ
            )

            // Trường Date of Birth
            OutlinedTextField(
                value = dateOfBirth,
                onValueChange = { }, // ReadOnly
                label = { Text("Date of Birth (dd/MM/yyyy)") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }, enabled = !isSaving) {
                        Icon(Icons.Filled.DateRange, contentDescription = "Select Date")
                    }
                },
                enabled = !isSaving,
                shape = RoundedCornerShape(8.dp)
            )

            // Trường Mobile
            OutlinedTextField(
                value = mobile,
                onValueChange = { mobile = it },
                label = { Text("Mobile Phone") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Phone),
                enabled = !isSaving,
                shape = RoundedCornerShape(8.dp)
            )

            // --- Hiển thị Email và Password (không cho sửa trực tiếp ở đây) ---
            Spacer(modifier = Modifier.height(16.dp))
            ProfileInfoItemsReadOnly(label = "Email:", value = "Tap to change", onClick = { navController.navigate("profilePageEmail") }) // Route đổi email
            ProfileInfoItemsReadOnly(label = "Password:", value = "Tap to change", onClick = { navController.navigate("profileChangePassword") }) // Route đổi password

            // --- Hiển thị lỗi API ---
            if (apiError != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = apiError ?: "An error occurred",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // --- Nút Save ---
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { showConfirmDialog = true }, // Mở dialog xác nhận
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isSaving, // Vô hiệu hóa khi đang lưu
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                shape = RoundedCornerShape(50)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Save Changes", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp)) // Khoảng cách dưới cùng
        } // Kết thúc else của isLoadingData

        // --- Dialog Xác nhận Lưu ---
        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { if (!isSaving) showConfirmDialog = false }, // Cho phép đóng nếu không đang lưu
                title = { Text("Confirm Changes", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                text = { Text("Save the updated profile information?", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                confirmButton = {
                    Button(
                        onClick = {
                            if (!isSaving) {
                                updateProfile() // Gọi hàm cập nhật API
                            }
                        },
                        enabled = !isSaving, // Chỉ bật khi không đang lưu
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                    ) {
                        Text("Yes, Save")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { if (!isSaving) showConfirmDialog = false },
                        enabled = !isSaving
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}


@Composable
fun ProfileInfoItemsReadOnly(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(8.dp)) // Viền mảnh hơn
            .background(Color.White.copy(alpha = 0.8f), shape = RoundedCornerShape(8.dp)) // Nền hơi mờ
            .clickable(onClick = onClick) // Click để điều hướng
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween // Đẩy icon sang phải
    ) {
        Column {
            Text(text = label, fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
        Icon(
            painter = painterResource(id = R.drawable.navigate_next), // Icon điều hướng
            contentDescription = "Navigate",
            modifier = Modifier.size(24.dp),
            tint = Color.Gray
        )
    }
    Spacer(modifier = Modifier.height(16.dp)) // Khoảng cách giữa các item
}


@Composable
fun ProfileImagesEdit(
    modifier: Modifier = Modifier,
    initialImageUrl: String?, // URL ảnh hiện tại từ API
    selectedImageUri: Uri?, // Uri ảnh mới chọn
    onImageSelected: (Uri?) -> Unit // Callback khi chọn ảnh mới
) {
    var isLaunching by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        isLaunching = false
        if (uri != null) {
            Log.d("ProfileImagesEdit", "New image selected: $uri")
            onImageSelected(uri) // Gọi callback để cập nhật state ở Composable cha
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Card(
            shape = CircleShape,
            modifier = Modifier.size(130.dp).clip(CircleShape),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            // Ưu tiên ảnh mới chọn (selectedImageUri), sau đó đến ảnh từ API (initialImageUrl)
            val painter = when {
                selectedImageUri != null -> rememberAsyncImagePainter(selectedImageUri)
                !initialImageUrl.isNullOrBlank() -> rememberAsyncImagePainter(
                    model = initialImageUrl,
                    placeholder = painterResource(id = R.drawable.nha_trang), // Thay bằng placeholder của bạn
                    error = painterResource(id = R.drawable.emilia_clarke) // Thay bằng avatar mặc định
                )
                else -> painterResource(R.drawable.avatar) // Ảnh mặc định
            }
            Image(
                painter = painter,
                contentDescription = "Profile Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Change Picture", // Đổi chữ
            fontSize = 14.sp, // Cỡ chữ nhỏ hơn
            textDecoration = TextDecoration.Underline,
            fontWeight = FontWeight.Medium,
            color = Color.Blue, // Màu link
            modifier = Modifier.clickable {
                if (!isLaunching) {
                    isLaunching = true
                    launcher.launch("image/*") // Mở trình chọn ảnh
                }
            }.padding(8.dp) // Thêm padding để dễ nhấn
        )
    }
}