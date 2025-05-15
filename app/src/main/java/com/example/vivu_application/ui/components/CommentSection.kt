package com.example.vivu_application.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.vivu_application.R
import com.example.vivu_application.controller.PostController
import com.example.vivu_application.model.Comment
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.clip
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.UUID


@Composable
fun CommentSection(postId: Int, postController: PostController) {

    val post = postController.posts.collectAsState().value.find { it.id == postId } ?: return

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Box(

            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(300.dp)
                .height(1.dp)
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.height(16.dp))
        // Ô nhập bình luận
        CommentInputSection(postId = post.id, postController = viewModel())

        Spacer(modifier = Modifier.height(16.dp))

        // Tiêu đề phần bình luận
        Text(
            text = "COMMENT (${post.comments.size})",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurfaceVariant, // Màu từ theme, thường là xám
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(5.dp))

        // Danh sách bình luận
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            post.comments.forEachIndexed { index, comment ->
                CommentItem(comment)
                if (index < post.comments.lastIndex) {
                    Spacer(modifier = Modifier.height(6.dp)) // spacing nhẹ giữa comment
                }
            }

        }

        Spacer(modifier = Modifier.height(16.dp))


    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CommentInputSection(postId: Int, postController: PostController) {
    var newComment by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0) }

    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()



    // Auto scroll khi chọn sao
    LaunchedEffect(rating) {
        if (rating > 0) {
            coroutineScope.launch {
                bringIntoViewRequester.bringIntoView()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Star rating
        StarRating(rating = rating, onRatingChanged = { rating = it })

        Text(
            text = "chạm vào ngôi sao để xếp hạng",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )

        if (rating > 0) {
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .bringIntoViewRequester(bringIntoViewRequester)
            ) {
                //  Avatar lồi ra bên trái
                Image(
                    painter = painterResource(id = R.drawable.avatar), // Thay bằng hình đúng
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.LightGray, CircleShape)
                )

                Spacer(modifier = Modifier.width(4.dp))

                // 💬 Khung comment
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .width(320.dp)
                        .background(Color(0xFFE9E8E8), RoundedCornerShape(20.dp))
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = newComment,
                            onValueChange = { newComment = it },
                            placeholder = { Text("Nhập bình luận...") },
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            maxLines = 3,
                            singleLine = false
                        )

                        IconButton(
                            onClick = {
                                if (newComment.isNotBlank()) {
                                    postController.addComment(
                                        postId = postId, //  THÊM DÒNG NÀY
                                        comment = Comment(
                                            id = UUID.randomUUID().toString(),
                                            userName = "Khách",
                                            content = newComment,
                                            rating = rating
                                        )
                                    )
                                    newComment = ""
                                    rating = 0
                                }
                            },
                            enabled = newComment.isNotBlank()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_send), // Mũi tên màu xanh
                                contentDescription = "Gửi",
                                tint = Color(0xFF003DF5), // Màu xanh dương đậm
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun CommentItem(comment: Comment) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 6.dp)
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = RoundedCornerShape(30.dp) // Viền bo tròn ngoài
            )
            .padding(8.dp) // Padding giữa viền đen và nội dung bên trong
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            // Avatar bên trái
            Image(
                painter = painterResource(id = R.drawable.avatar), // Thay bằng hình
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.LightGray, CircleShape)
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Khung xám chứa nội dung bình luận
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFEAEAEA), // Màu xám nhạt
                modifier = Modifier
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    // Dòng tên + sao
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = comment.userName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        StarRating(
                            rating = comment.rating,
                            editable = false,
                            iconSize = 14.dp,
                            spacing = 1.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Nội dung bình luận
                    ExpandableText(text = comment.content)
                }
            }
        }
    }
}


