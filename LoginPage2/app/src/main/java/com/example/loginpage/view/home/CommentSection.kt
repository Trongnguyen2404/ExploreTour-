package com.example.loginpage.view.home


import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Divider
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
import com.example.loginpage.R
import com.example.loginpage.controller.PostController
import com.example.loginpage.model.Comment


@Composable
fun CommentSection(postViewModel: PostController) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        // Tiêu đề phần bình luận
        Text(
            text = "COMMENT (${postViewModel.comments.size})",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Danh sách bình luận
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            postViewModel.comments.forEach { comment ->
                CommentItem(comment)
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ô nhập bình luận
        CommentInputSection(postViewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentInputSection(postController: PostController) {
    var newComment by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Rating stars
        StarRating(rating = rating, onRatingChanged = { rating = it })

        Text(
            text = "Chạm vào ngôi sao để xếp hạng",
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
                    .padding(bottom = 8.dp)
            ) {
                // Avatar bên trái
                Icon(
                    imageVector = Icons.Default.AccountCircle, // Hoặc Image(painterResource(id = R.drawable.avatar_user))
                    contentDescription = "Avatar",
                    modifier = Modifier.size(50.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // TextField + Send icon
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .width(400.dp)
                        .heightIn(min = 50.dp)
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = newComment,
                            onValueChange = { newComment = it },
                            placeholder = { Text("Nhập bình luận...") },
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.colors( // Use colors() instead of textFieldColors()
                                // Set container colors for different states if desired
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,


                                // Set indicator colors for different states
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),

//                            colors = TextFieldDefaults.textFieldColors(
//                                containerColor = Color.Transparent,
//                                unfocusedIndicatorColor = Color.Transparent,
//                                focusedIndicatorColor = Color.Transparent
//                            ),
                            maxLines = 3,
                            singleLine = false
                        )

                        IconButton(
                            onClick = {
                                if (newComment.isNotBlank()) {
                                    postController.addComment(
                                        Comment(
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
                                painter = painterResource(id = R.drawable.ic_send), // <-- Drawable từ res
                                contentDescription = "Gửi bình luận",
                                modifier = Modifier.size(30.dp),
                                tint = MaterialTheme.colorScheme.primary
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
            .width(450.dp)
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = RoundedCornerShape(40.dp) // Bo tròn viền ngoài
            )
            .padding(8.dp) // Padding giữa viền và nội dung
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            // Avatar bên trái
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Avatar",
                modifier = Modifier.size(50.dp).offset(y = 5.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(2.dp))

            // Nội dung bình luận bên phải trong nền sáng
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.width(270.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {

                    // Hàng đầu: tên + đánh giá sao sát nhau
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
                            iconSize = 12.dp,
                            spacing = 1.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Nội dung bình luận
                    Text(
                        text = comment.content,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun StarRating(
    rating: Int,
    editable: Boolean = true,
    onRatingChanged: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    iconSize: Dp = 40.dp,
    spacing: Dp = 2.dp
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "Star $i",
                tint = if (i <= rating) Color(0xFFFFD700) else Color.Gray,
                modifier = Modifier
                    .size(iconSize)
                    .then(
                        if (editable) Modifier.clickable { onRatingChanged(i) } else Modifier
                    )
            )
            if (i < 5) {
                Spacer(modifier = Modifier.width(spacing))
            }
        }
    }
}

