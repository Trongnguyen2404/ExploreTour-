package com.example.vivu_app.ui.components

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
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

    // Delay cho từng tầng mây xuất hiện
    LaunchedEffect(Unit) {
        delay(100)
        showCloud1 = true
        delay(300)
        showCloud2 = true
        delay(500)
        showCloud3 = true
    }

    // Animation cho từng tầng mây
    val cloud1TranslationY by animateFloatAsState(
        targetValue = if (showCloud1) -200f else -500f,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing), label = ""
    )
    val cloud2TranslationY by animateFloatAsState(
        targetValue = if (showCloud2) -110f else -500f,
        animationSpec = tween(durationMillis = 1400, easing = FastOutSlowInEasing), label = ""
    )
    val cloud3TranslationY by animateFloatAsState(
        targetValue = if (showCloud3) -30f else -500f,
        animationSpec = tween(durationMillis = 1600, easing = FastOutSlowInEasing), label = ""
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
//            .height(200.dp)
            .background(Color.Transparent) //  Nền trong suốt
    ) {
        // Layer 3 - Mây sâu nhất
        AnimatedVisibility(visible = showCloud1) {
            Image(
                painter = painterResource(id = R.drawable.cloud_layer_3),
                contentDescription = "Cloud Layer 3",
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(1.2f)
                    .height(200.dp)
                    .graphicsLayer {
                        translationY = cloud3TranslationY
                    },
                contentScale = ContentScale.FillBounds,
            )
        }

        // Layer 2 - Mây giữa
        AnimatedVisibility(visible = showCloud2) {
            Image(
                painter = painterResource(id = R.drawable.cloud_layer_2),
                contentDescription = "Cloud Layer 2",
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(1.2f)
                    .height(200.dp)
                    .graphicsLayer {
                        translationY = cloud2TranslationY
                        translationX = -20.dp.toPx()
                    },
                contentScale = ContentScale.FillBounds,
            )
        }

        // Layer 1 - Mây gần nhất
        AnimatedVisibility(visible = showCloud3) {
            Image(
                painter = painterResource(id = R.drawable.cloud_layer_1),
                contentDescription = "Cloud Layer 1",
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(1.2f)
                    .height(200.dp)
                    .graphicsLayer {
                        translationY = cloud1TranslationY
                    },
//                contentScale = ContentScale.FillBounds,
            )
        }
    }
}
