package com.app.fastlearn.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Hàm composable hiển thị lớp phủ loading với văn bản tùy chọn.
 * Có thể được sử dụng như lớp phủ toàn màn hình hoặc hộp thoại.
 *
 * @param isLoading Boolean xác định có hiển thị lớp phủ loading hay không
 * @param message Thông báo tùy chọn hiển thị bên dưới chỉ báo loading
 * @param useDialog Sử dụng Dialog thay vì lớp phủ Box
 */
@Composable
fun LoadingOverlay(
    isLoading: Boolean,
    message: String? = null,
    useDialog: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (!isLoading) return

    val content = @Composable {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 4.dp
                    )

                    if (!message.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }

    if (useDialog) {
        Dialog(
            onDismissRequest = { /* Non-cancelable */ },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            content()
        }
    } else {
        content()
    }
}

/**
 * Phiên bản đơn giản hơn của LoadingOverlay chỉ hiển thị vòng quay loading toàn màn hình
 * mà không có nền thẻ.
 */
@Composable
fun SimpleLoadingOverlay(
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isLoading) return

    // Calculate the alpha animation for a subtle fade effect
    val alphaAnim by animateFloatAsState(
        targetValue = if (isLoading) 1f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .alpha(alphaAnim)
            .background(Color.Black.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(40.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}