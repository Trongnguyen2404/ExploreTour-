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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.loginpage.R
import com.example.loginpage.navigation.BottomNavigationBar

@Composable
fun ProfilePageDetail(navController: NavHostController) {
    val viewModel: ProfileViewModel = viewModel()
    val name by viewModel.name.collectAsState()
    val dateOfBirth by viewModel.dateOfBirth.collectAsState()
    val mobile by viewModel.mobile.collectAsState()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE3E3E3))
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
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

            Spacer(modifier = Modifier.height(24.dp))

            ProfileImage(
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(30.dp))

            ProfileInfoItem(label = "Name:", value = name)
            Spacer(modifier = Modifier.height(20.dp))
            ProfileInfoItem(label = "Date of birth:", value = dateOfBirth)
            Spacer(modifier = Modifier.height(20.dp))
            ProfileInfoItem(label = "Mobile:", value = mobile)
            Spacer(modifier = Modifier.height(20.dp))

            ProfileInfoItem(
                label = "Email:",
                value = "123*********"
            )

            Spacer(modifier = Modifier.height(20.dp))

            ProfileInfoItem(
                label = "Password:",
                value = "********"
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    navController.navigate("profilePageDetailEdit")
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
                    text = "Edit Profile",
                    style = TextStyle(fontSize = 20.sp)
                )
            }
        }
    }
}

@Composable
fun ProfileInfoItem(label: String, value: String, isNavigatable: Boolean = false, onClick: (() -> Unit)? = null) {
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
    }
}

@Composable
fun ProfileImage(modifier: Modifier = Modifier) {
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
    }
}
