package com.example.vivu_app.ui

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.example.vivu_app.R
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun CloudAnimationScreen(modifier: Modifier = Modifier) {

    var showCloud1 by remember { mutableStateOf(false) }
    var showCloud2 by remember { mutableStateOf(false) }
    var showCloud3 by remember { mutableStateOf(false) }

    // ⏳ Delay cho từng tầng mây xuất hiện
    LaunchedEffect(Unit) {
        delay(200)  // Mây 1 xuất hiện
        showCloud1 = true
        delay(400)  // Mây 2 xuất hiện
        showCloud2 = true
        delay(500)  // Mây 3 xuất hiện
        showCloud3 = true
    }

    // 📌 Hiệu ứng trượt xuống nhẹ
    val cloud1Offset by animateDpAsState(
        targetValue = if (showCloud1) -80.dp else (-500).dp,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing), label = ""
    )

    val cloud2Offset by animateDpAsState(
        targetValue = if (showCloud2) -55.dp else (-500).dp,
        animationSpec = tween(durationMillis = 1400, easing = FastOutSlowInEasing), label = ""
    )

    val cloud3Offset by animateDpAsState(
        targetValue = if (showCloud3) -25.dp else (-500).dp,
        animationSpec = tween(durationMillis = 1600, easing = FastOutSlowInEasing), label = ""
    )


    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(600.dp) // 🔥 Chiều cao lớn hơn để mây không bị cắt
    ) {
        // 🌥️ Tầng mây 1 (TO HƠN & LÊN CAO)
        AnimatedVisibility(visible = showCloud1) {
            Image(
                painter = painterResource(id = R.drawable.cloud_layer_3),
                contentDescription = "Cloud Layer 1",
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(1.2f) // 🔥 KÉO DÀI 200% MÀN HÌNH
                    .height(200.dp)
                    .offset(y = cloud3Offset)
            )
        }
        // ☁️ Tầng mây 2 (TO HƠN & LÊN CAO)
        AnimatedVisibility(visible = showCloud2) {
            Image(
                painter = painterResource(id = R.drawable.cloud_layer_2),
                contentDescription = "Cloud Layer 2",
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(1.2f) // 🔥 KÉO DÀI 200% MÀN HÌNH
                    .height(200.dp)
                    .offset(y = cloud2Offset, x = -15.dp)
            )
        }
        // 🌩️ Tầng mây 3 (TO HƠN & LÊN CAO)
        AnimatedVisibility(visible = showCloud3) {
            Image(
                painter = painterResource(id = R.drawable.cloud_layer_1),
                contentDescription = "Cloud Layer 3",
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(1.2f) // 🔥 KÉO DÀI 200% MÀN HÌNH
                    .height(200.dp)
                    .offset(y = cloud1Offset)
            )
        }
    }
}
