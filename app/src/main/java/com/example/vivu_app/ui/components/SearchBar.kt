package com.example.vivu_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.vivu_app.R

class SearchViewModel : ViewModel() {

    var searchText by mutableStateOf("")
        private set

    private val _originalList = listOf("An", "Bình", "Cường", "Dũng", "Huy", "Hạnh")
    var filteredList by mutableStateOf(_originalList)
        private set

    fun onSearchTextChange(newText: String) {
        searchText = newText
        filteredList = if (newText.isBlank()) {
            _originalList
        } else {
            _originalList.filter {
                it.contains(newText, ignoreCase = true)
            }
        }
    }
}
@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color.White, shape = RoundedCornerShape(50))
            .border(2.dp, Color.Black, shape = RoundedCornerShape(50)),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Start
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 50.dp, top = 8.dp, bottom = 8.dp)
        )

        if (searchText.isEmpty()) {
            Text(
                text = "Search...",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp)
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = "Search Icon",
            tint = Color.Unspecified,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(35.dp)
                .padding(end = 12.dp)
        )
    }
}
