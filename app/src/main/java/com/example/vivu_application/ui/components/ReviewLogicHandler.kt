package com.example.vivu_application.ui.components

import androidx.compose.runtime.Composable
import com.example.vivu_application.data.model.Review
import com.example.vivu_application.viewmodel.ReviewUiState


@Composable
fun ReviewLogicHandler(
    reviewState: ReviewUiState,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    content: @Composable (
        reviews: List<Review>,
        isLoading: Boolean,
        isLastPage: Boolean,
        error: String?,
        onLoadMore: () -> Unit,
        onRetry: () -> Unit
    ) -> Unit
) {
    content(
        reviewState.reviews,
        reviewState.isLoading,
        reviewState.isLastPage,
        reviewState.error,
        onLoadMore,
        onRetry
    )
}
