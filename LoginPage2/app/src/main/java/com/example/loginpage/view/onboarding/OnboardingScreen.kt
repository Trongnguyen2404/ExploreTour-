package com.example.loginpage.view.onboarding


import android.media.MediaTimestamp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    navController: NavController,
    onFinished: () -> Unit) {

    val pages: List<OnboardingModel> = listOf(
        OnboardingModel.FirstPages,
        OnboardingModel.SecondPages,
        OnboardingModel.ThirdPages,
        OnboardingModel.FourthPages
    )

    val pagerState: PagerState = rememberPagerState(initialPage = 0) { pages.size }

    val buttonState = remember {
        derivedStateOf {
            when (pagerState.currentPage) {
                0 -> listOf("", "Next")
                1 -> listOf("Back", "Next")
                2 -> listOf("Back", "Next")
                3 -> listOf("Back", "")
                else -> listOf("", "")
            }
        }
    }

    val scope = rememberCoroutineScope()
    val showSkipDialog = remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 75.dp), // Thêm padding tổng thể
                horizontalAlignment = Alignment.End // Căn nút Skip sang phải
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp), // Thêm padding phía trên hàng nút điều hướng
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Nút Back
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (buttonState.value[0].isNotEmpty()) {
                            ButtonUI(
                                text = buttonState.value[0],
                                backgroundColor = Color.Transparent,
                                textColor = Color.Gray
                            ) {
                                scope.launch {
                                    if (pagerState.currentPage > 0) {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                }
                            }
                        }
                    }

                    // Indicator
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        IndicatorUI(pageSize = pages.size, currentPage = pagerState.currentPage)
                    }

                    // Nút Next
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        if (buttonState.value[1].isNotEmpty()) {
                            ButtonUI(
                                text = buttonState.value[1],
                                backgroundColor = MaterialTheme.colorScheme.primary,
                                textColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                scope.launch {
                                    if (pagerState.currentPage < pages.size - 1) {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // AlertDialog hiển thị khi showSkipDialog là true
            if (showSkipDialog.value) {
                AlertDialog(
                    onDismissRequest = {showSkipDialog.value = false },
                    title = { Text(" Confirm ignore") },
                    text = {Text("Do you want to skip the introduction",
                        fontSize = 16.sp)},
                    confirmButton = {
                        TextButton(onClick =  {
                            showSkipDialog.value = false
                            scope.launch {
                                pagerState.animateScrollToPage(pages.size - 1) // chuyen den trang cuoi
                            }
                        }, colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray,
                            contentColor = Color.Black
                        )

                        )     {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSkipDialog.value = false }) {
                            Text("No")
                        }
                    }
                )
            }

        },

        content = { paddingValues ->
            Column(Modifier.padding(paddingValues)) {
                HorizontalPager(state = pagerState) { index ->
                    val layoutType = when (index) {
                        0 -> "title_and_description_below"
                        3 -> "title_image_button_description_button"
                        else -> "title_above_description_below"
                    }
                    val (imageWidth, imageHeight) = when (index) {
                        0 -> Pair(320.dp, 330.dp)
                        1 -> Pair(420.dp, 300.dp)
                        2 -> Pair(420.dp, 300.dp)
                        3 -> Pair(320.dp, 320.dp)
                        else -> Pair(420.dp, 320.dp)
                    }
                    OnboardingGraphUI(
                        onboardingModel = pages[index],
                        layoutType = layoutType,
                        imageWidth = imageWidth,
                        imageHeight = imageHeight,
                        navController = navController
                    )

                }
            }
        }
    )
    // Nút Skip đặt trên cùng của Box
    if (pagerState.currentPage < pages.size - 1) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 8.dp, top = 70.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            ButtonUI(
                text = "Skip >",
                fontSize = 20,
                backgroundColor = Color.Transparent,
                textColor = Color.Black,
                onClick = { showSkipDialog.value = true
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen(navController = NavController(LocalContext.current)) {}
}