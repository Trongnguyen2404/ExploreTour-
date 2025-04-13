package com.example.loginpage.view.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun OnboardingGraphUI(
    onboardingModel: OnboardingModel,
    layoutType: String = "title_above_description_below",
    imageWidth: Dp = 300.dp,
    imageHeight: Dp = 200.dp,
    navController: NavController? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(600.dp) // Giảm chiều cao để tránh bị cắt
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFDCEEFF),
                        Color(0xFFEBF5FF),
                        Color.White
                    )
                )
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (layoutType) {
            "title_above_description_below" -> {
                Spacer(modifier = Modifier.size(50.dp))

                Text(
                    text = onboardingModel.title,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.fillMaxWidth().size(20.dp))

                Image(
                    painter = painterResource(id = onboardingModel.image),
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = imageWidth, height = imageHeight)
                        .padding(50.dp, 0.dp),
                    alignment = Alignment.Center
                )

                Spacer(modifier = Modifier.fillMaxWidth().size(20.dp))

                if (onboardingModel.description.isNotEmpty()) {
                    Text(
                        text = onboardingModel.description,
                        modifier = Modifier.fillMaxWidth().padding(15.dp, 0.dp),
                        fontSize = 20.sp,
                        lineHeight = 28.sp,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))
            }

            "title_and_description_below" -> {
                Spacer(modifier = Modifier.size(50.dp))

                Image(
                    painter = painterResource(id = onboardingModel.image),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .size(width = imageWidth, height = imageHeight),
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.fillMaxWidth().size(20.dp))

                Text(
                    text = onboardingModel.title,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.fillMaxWidth().size(10.dp))

                if (onboardingModel.description.isNotEmpty()) {
                    Text(
                        text = onboardingModel.description,
                        modifier = Modifier.fillMaxWidth().padding(15.dp, 0.dp),
                        fontSize = 20.sp,
                        lineHeight = 28.sp,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                } else {
                    Spacer(modifier = Modifier.size(30.dp))
                }
                Spacer(modifier = Modifier.size(10.dp))
            }

            "title_image_button_description_button" -> {
                Spacer(modifier = Modifier.size(20.dp))

                // Tiêu đề
                Text(
                    text = onboardingModel.title,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.size(15.dp))

                // Hình ảnh
                Image(
                    painter = painterResource(id = onboardingModel.image),
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = imageWidth, height = imageHeight)
                        .padding(horizontal = 50.dp),
                    alignment = Alignment.Center
                )

                Spacer(modifier = Modifier.size(15.dp))

                // Sign in
                if (onboardingModel.buttonText1.isNotEmpty()) {
                    Button(
                        onClick = {
                            navController?.navigate("login")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 50.dp)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4A90E2) // Màu xanh cho nút "Sign in"
                        )
                    ) {
                        Text(
                            text = onboardingModel.buttonText1,
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                    }
                } else {
                    Text(
                        text = "Nút thứ nhất chưa có văn bản",
                        color = Color.Red,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))

                // Mô tả
                if (onboardingModel.description.isNotEmpty()) {
                    Text(
                        text = onboardingModel.description,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                        fontSize = 18.sp,
                        lineHeight = 26.sp,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))

                //Sign up
                if (onboardingModel.buttonText2.isNotEmpty()) {
                    Button(
                        onClick = {
                            navController?.navigate("createAccount")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 50.dp)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFCCCCCC) // Màu xám cho nút "Sign up"
                        )
                    ) {
                        Text(
                            text = onboardingModel.buttonText2,
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                    }
                } else {
                    Text(
                        text = "Nút thứ hai chưa có văn bản",
                        color = Color.Red,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingGraphUIPrevirew1() {
    OnboardingGraphUI(
        OnboardingModel.FirstPages,
        layoutType = "title_above_description_below"
    )
}

@Preview(showBackground = true)
@Composable
fun OnboardingGraphUIPrevirew2() {
    OnboardingGraphUI(
        OnboardingModel.SecondPages,
        layoutType = "title_and_description_below"
    )
}

@Preview(showBackground = true)
@Composable
fun OnboardingGraphUIPrevirew3() {
    OnboardingGraphUI(
        OnboardingModel.ThirdPages,
        layoutType = "title_and_description_below"
    )
}

@Preview(showBackground = true)
@Composable
fun OnboardingGraphUIPrevirew4() {
    OnboardingGraphUI(
        OnboardingModel.FourthPages,
        layoutType = "title_image_button_description_button",
        imageWidth = 300.dp,
        imageHeight = 200.dp
    )
}