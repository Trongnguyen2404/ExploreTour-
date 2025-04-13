package com.example.loginpage.view.profile.detail

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.loginpage.R
import com.example.loginpage.ui.theme.YourAppTheme

@Composable
fun ProfilePageDetailEdit(navController: NavHostController) {
    YourAppTheme {
        val viewModel: ProfileViewModel = viewModel()
        val name by viewModel.name.collectAsState()
        val dateOfBirth by viewModel.dateOfBirth.collectAsState()
        val mobile by viewModel.mobile.collectAsState()
        val email = "123*********"
        val password = "********"

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
                        tint = Color.Blue
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            ProfileImages(
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "Do you want to change the data?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))

            EditProfileInfoItem(
                label = "Name:",
                value = name,
                onValueChange = { viewModel.updateName(it) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            EditProfileInfoItem(
                label = "Date of birth:",
                value = dateOfBirth,
                onValueChange = { viewModel.updateDateOfBirth(it) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            EditProfileInfoItem(
                label = "Mobile:",
                value = mobile,
                onValueChange = { viewModel.updateMobile(it) },
                keyboardType = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(20.dp))

            ProfileInfoItems(
                label = "Email:",
                value = email,
                isNavigatable = true,
                onClick = { navController.navigate("profilePageEmail") }
            )

            Spacer(modifier = Modifier.height(20.dp))

            ProfileInfoItems(
                label = "Password:",
                value = password,
                isNavigatable = true,
                onClick = { navController.navigate("profilePagePassword") }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFA1C9F1),
                    contentColor = Color.Black
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 5.dp,
                    pressedElevation = 5.dp,
                    disabledElevation = 0.dp
                )
            ) {
                Text(
                    text = "Save",
                    style = TextStyle(fontSize = 20.sp)
                )
            }
        }
    }
}

// Composable để hiển thị và chỉnh sửa thông tin
@Composable
fun EditProfileInfoItem(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(vertical = 8.dp)
            .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(20.dp))
            .background(Color.White, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = Color.Black // Đảm bảo màu chữ là đen
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = ImeAction.Next
            ),
            colors = TextFieldDefaults.colors(
                cursorColor = Color.Black, // Màu con trỏ
                focusedContainerColor = Color.White, // Màu nền khi focus
                unfocusedContainerColor = Color.White, // Màu nền khi không focus
                focusedIndicatorColor = Color.Black, // Màu viền khi focus
                unfocusedIndicatorColor = Color.Gray // Màu viền khi không focus
            )
        )
    }
}

// Composable để hiển thị thông tin tĩnh (dùng lại từ ProfilePageDetail)
@Composable
fun ProfileInfoItems(
    label: String,
    value: String,
    isNavigatable: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(vertical = 8.dp)
            .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(20.dp))
            .background(Color.White, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable(enabled = isNavigatable) { onClick?.invoke() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        if (isNavigatable) {
            Icon(
                painter = painterResource(id = R.drawable.navigate_next),
                contentDescription = "Navigate",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}



@Composable
fun ProfileImages(modifier: Modifier = Modifier) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Card(
            shape = CircleShape,
            modifier = Modifier
                .size(130.dp)
                .clip(CircleShape)
        ) {
            Image(
                painter = if (imageUri == null) painterResource(R.drawable.emilia_clarke)
                else rememberAsyncImagePainter(imageUri),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Change the picture",
            fontSize = 16.sp,
            textDecoration = TextDecoration.Underline,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.clickable { launcher.launch("image/*") }
        )
    }
}
