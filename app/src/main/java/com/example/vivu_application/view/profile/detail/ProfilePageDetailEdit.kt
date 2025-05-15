
package com.example.vivu_application.view.profile.detail

import android.app.DatePickerDialog
import android.net.Uri // Giữ lại import này vì Coil có thể cần cho các trường hợp khác, nhưng không dùng trực tiếp cho logic chọn file nữa
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalConfiguration
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
import com.example.vivu_application.R
import com.example.vivu_application.data.model.UpdateProfileRequestBody
import com.example.vivu_application.data.network.RetrofitClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Các hàm convertToApiDate và convertToDisplayDate giữ nguyên
fun convertToApiDate(displayDate: String): String? {
    return try {
        val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = displayFormat.parse(displayDate)
        if (date != null) apiFormat.format(date) else null
    } catch (e: Exception) {
        null
    }
}

fun convertToDisplayDate(apiDate: String?): String {
    if (apiDate.isNullOrBlank()) return ""
    return try {
        val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = apiFormat.parse(apiDate)
        if (date != null) displayFormat.format(date) else ""
    } catch (e: Exception) {
        ""
    }
}

@Composable
fun ProfilePageDetailEdit(navController: NavHostController) {

    // --- State cho dữ liệu nhập liệu ---
    var name by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") } // Lưu ở định dạng dd/MM/yyyy
    var mobile by remember { mutableStateOf("") }
    var currentProfilePictureUrl by remember { mutableStateOf<String?>(null) } // Lưu URL ảnh hiện tại từ API

    // ++ THAY ĐỔI: Bỏ selectedImageUri, thêm state cho URL input và dialog ++
    var newImageUrlInput by remember { mutableStateOf("") } // State cho URL người dùng nhập vào dialog
    var tempDisplayImageUrl by remember { mutableStateOf<String?>(null) } // URL để hiển thị tạm thời sau khi nhập từ dialog
    var showEnterUrlDialog by remember { mutableStateOf(false) } // State để hiển thị dialog nhập URL

    // --- State cho API call và UI ---
    var isLoadingData by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var apiError by remember { mutableStateOf<String?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authApiService = RetrofitClient.authApiService
    // val scrollState = rememberScrollState() // Không thấy dùng scrollState trong Column chính, có thể bỏ nếu không cần thiết

    // --- Hàm tải dữ liệu profile ban đầu ---
    fun fetchInitialProfile() {
        isLoadingData = true
        apiError = null
        coroutineScope.launch {
            try {
                val response = authApiService.getUserProfile()
                if (response.isSuccessful) {
                    val profile = response.body()
                    profile?.let {
                        name = it.name ?: ""
                        dateOfBirth = convertToDisplayDate(it.dateOfBirth)
                        mobile = it.mobile ?: ""
                        currentProfilePictureUrl = it.profilePictureUrl
                        tempDisplayImageUrl = it.profilePictureUrl // Khởi tạo URL hiển thị

                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Failed to load profile data."
                    apiError = "Could not load current profile data."

                }
            } catch (e: Exception) {
                apiError = "Network error loading data."
            } finally {
                isLoadingData = false
            }
        }
    }

    LaunchedEffect(Unit) {
        fetchInitialProfile()
    }

    // --- ++ CẬP NHẬT HÀM NÀY ++ ---
    fun updateProfile() {
        isSaving = true
        apiError = null
        val apiDob = convertToApiDate(dateOfBirth)

        // Lấy URL ảnh để gửi đi:
        // Ưu tiên URL người dùng vừa nhập và xác nhận từ dialog (lưu trong tempDisplayImageUrl sau khi dialog OK)
        // Nếu không, dùng URL hiện tại từ server.
        // newImageUrlInput chỉ là buffer của TextField trong dialog.
        val finalImageUrlToSend = tempDisplayImageUrl // tempDisplayImageUrl đã được cập nhật khi người dùng nhấn OK trên dialog

        val requestBody = UpdateProfileRequestBody(
            fullName = name.trim().takeIf { it.isNotEmpty() },
            dateOfBirth = apiDob,
            phoneNumber = mobile.trim().takeIf { it.isNotEmpty() },
            profilePictureUrl = finalImageUrlToSend // Gửi URL đã được người dùng xác nhận (hoặc URL cũ)
        )

        coroutineScope.launch {
            try {
                val response = authApiService.updateUserProfile(requestBody)
                if (response.isSuccessful) {
                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    // Sau khi lưu thành công, API trả về profile đã cập nhật
                    // Cập nhật lại currentProfilePictureUrl từ response của server để đảm bảo nhất quán
                    // và cũng cập nhật tempDisplayImageUrl
                    response.body()?.profilePictureUrl?.let { updatedUrlFromServer ->
                        currentProfilePictureUrl = updatedUrlFromServer
                        tempDisplayImageUrl = updatedUrlFromServer
                    }
                    newImageUrlInput = "" // Xóa bộ đệm input URL trong dialog
                    navController.navigate("profilePage") {
                        popUpTo("profilePageDetailEdit") { inclusive = true } // Xóa màn hình edit khỏi backstack
                        launchSingleTop = true // Tránh tạo nhiều instance của profilePage
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Failed to update profile."
                    apiError = "Phone number must be 10 or 11 digits"
                }
            } catch (e: Exception) {
                apiError = "Network error during update."
            } finally {
                isSaving = false
                showConfirmDialog = false
            }
        }
    }

    // --- Date Picker Dialog (giữ nguyên) ---
    val calendar = Calendar.getInstance()
    val initialYear = remember(dateOfBirth) { try { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateOfBirth)?.let { calendar.time = it; calendar.get(Calendar.YEAR) } ?: calendar.get(Calendar.YEAR) } catch (e: Exception) { calendar.get(Calendar.YEAR) } }
    val initialMonth = remember(dateOfBirth) { try { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateOfBirth)?.let { calendar.time = it; calendar.get(Calendar.MONTH) } ?: calendar.get(Calendar.MONTH) } catch (e: Exception) { calendar.get(Calendar.MONTH) } }
    val initialDay = remember(dateOfBirth) { try { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateOfBirth)?.let { calendar.time = it; calendar.get(Calendar.DAY_OF_MONTH) } ?: calendar.get(Calendar.DAY_OF_MONTH) } catch (e: Exception) { calendar.get(Calendar.DAY_OF_MONTH) } }
    val datePickerDialog = remember {
        DatePickerDialog(
            context, { _, year, month, dayOfMonth -> dateOfBirth = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year) },
            initialYear, initialMonth, initialDay
        ).apply {
            val minCal = Calendar.getInstance().apply { set(1900, 0, 1) } // Tháng bắt đầu từ 0
            val maxCal = Calendar.getInstance()
            datePicker.minDate = minCal.timeInMillis
            datePicker.maxDate = maxCal.timeInMillis
        }
    }

    // --- UI Chính ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3E3E3))
            .padding(horizontal = 16.dp, vertical = 8.dp), // Giảm padding dọc
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(top = 30.dp).size(48.dp)
            ) {
                Icon(painter = painterResource(id = R.drawable.back_ic), contentDescription = "Back", tint = Color(0xFF00BCD4))
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ++ SỬ DỤNG COMPOSABLE MỚI ĐỂ HIỂN THỊ ẢNH VÀ MỞ DIALOG NHẬP URL ++
        EditableProfileImageWithUrlDialog(
            modifier = Modifier.fillMaxWidth(),
            displayImageUrl = tempDisplayImageUrl, // Hiển thị URL đã được người dùng xác nhận từ dialog (hoặc URL ban đầu)
            onEditClick = {
                // Khi nhấn "Change Picture", đặt giá trị của newImageUrlInput bằng URL đang hiển thị (nếu có)
                // để người dùng có thể chỉnh sửa nó trong dialog thay vì phải gõ lại từ đầu.
                newImageUrlInput = tempDisplayImageUrl ?: ""
                showEnterUrlDialog = true
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Edit Your Profile",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(15.dp))

        if (isLoadingData) {
            CircularProgressIndicator(modifier = Modifier.padding(vertical = 32.dp))
        } else {
            // Trường Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name", color = Color.Black) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Gray,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                enabled = !isSaving,
                shape = RoundedCornerShape(8.dp)
            )

            // Trường Date of Birth
            OutlinedTextField(
                value = dateOfBirth,
                onValueChange = { },
                label = { Text("Date of Birth (dd/MM/yyyy)", color = Color.Black) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Gray,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
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
                label = { Text("Mobile Phone", color = Color.Black) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Gray,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Phone),
                enabled = !isSaving,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            ProfileInfoItemsReadOnly(label = "Email:", value = "Tap to change", onClick = { navController.navigate("profilePageEmail") })
            ProfileInfoItemsReadOnly(label = "Password:", value = "Tap to change", onClick = { navController.navigate("profilePageNewPassword") })

            if (apiError != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = apiError ?: "An error occurred",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(1.dp))
            Button(
                onClick = { showConfirmDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp), // Giảm từ 50.dp để tiết kiệm không gian
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                shape = RoundedCornerShape(50)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Save Changes", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- ++ THÊM DIALOG NHẬP URL ++ ---
        if (showEnterUrlDialog) {
            AlertDialog(
                onDismissRequest = { showEnterUrlDialog = false },
                title = { Text("Enter New Image URL", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                text = {
                    OutlinedTextField(
                        value = newImageUrlInput, // Sử dụng state cho input
                        onValueChange = { newImageUrlInput = it },
                        label = { Text("Image URL") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("https://example.com/image.jpg") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Done)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Kiểm tra URL có hợp lệ không (ví dụ cơ bản)
                            if (newImageUrlInput.isNotBlank() && (newImageUrlInput.startsWith("http://") || newImageUrlInput.startsWith("https://"))) {
                                tempDisplayImageUrl = newImageUrlInput // Cập nhật URL để hiển thị ngay
                                Log.d("ProfileEdit", "New image URL entered and confirmed: $tempDisplayImageUrl")
                            } else if (newImageUrlInput.isBlank()) {
                                // Nếu người dùng xóa URL và nhấn OK, có thể muốn xóa ảnh (gửi null/empty) hoặc giữ lại ảnh cũ
                                // Ở đây, nếu để trống, ta có thể hiểu là giữ ảnh cũ (tempDisplayImageUrl không đổi)
                                // Hoặc nếu muốn xóa: tempDisplayImageUrl = null
                                Toast.makeText(context, "Image URL cleared, will use current or no image.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Invalid URL format.", Toast.LENGTH_SHORT).show()
                                return@Button // Không đóng dialog nếu URL không hợp lệ
                            }
                            showEnterUrlDialog = false
                        }
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showEnterUrlDialog = false }) { Text("Cancel") }
                }
            )
        }

        // --- Dialog Xác nhận Lưu (giữ nguyên) ---
        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { if (!isSaving) showConfirmDialog = false },
                title = { Text("Confirm Changes", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                text = { Text("Save the updated profile information?", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                confirmButton = {
                    Button(
                        onClick = { if (!isSaving) { updateProfile() } },
                        enabled = !isSaving,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                    ) { Text("Yes, Save") }
                },
                dismissButton = {
                    TextButton(onClick = { if (!isSaving) showConfirmDialog = false }, enabled = !isSaving) { Text("Cancel") }
                }
            )
        }
    }
}

// Composable này giữ nguyên
@Composable
fun ProfileInfoItemsReadOnly(label: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.8f), shape = RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = label, fontSize = 12.sp, color = Color.Black)
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
        Icon(
            painter = painterResource(id = R.drawable.navigate_next),
            contentDescription = "Navigate",
            modifier = Modifier.size(24.dp),
            tint = Color.Gray
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
}

// --- ++ COMPOSABLE MỚI (thay thế ProfileImagesEdit cũ) ++ ---
@Composable
fun EditableProfileImageWithUrlDialog(
    modifier: Modifier = Modifier,
    displayImageUrl: String?, // URL ảnh để hiển thị (từ server hoặc từ dialog sau khi OK)
    onEditClick: () -> Unit // Callback khi nhấn nút "Change Picture" để mở dialog
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Card(
            shape = CircleShape,
            modifier = Modifier.size(100.dp).clip(CircleShape),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            val painter = when {
                !displayImageUrl.isNullOrBlank() -> rememberAsyncImagePainter(
                    model = displayImageUrl,
                    placeholder = painterResource(id = R.drawable.nha_trang), // Placeholder của bạn
                    error = painterResource(id = R.drawable.emilia_clarke)   // Ảnh lỗi/mặc định
                )
                else -> painterResource(R.drawable.avatar) // Ảnh mặc định nếu không có URL
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
            text = "Change Picture by URL",
            fontSize = 14.sp,
            textDecoration = TextDecoration.Underline,
            fontWeight = FontWeight.Medium,
            color = Color.Blue,
            modifier = Modifier
                .clickable { onEditClick() }
                .padding(8.dp)
        )
    }
}