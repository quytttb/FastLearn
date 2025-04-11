package com.app.fastlearn.ui.animations

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

/**
 * Hoạt hình để thay đổi độ mờ trong quá trình kéo thẻ
 * @param isDragging Xác định xem thẻ có đang được kéo hay không
 * @return Giá trị alpha (độ mờ) của thẻ
 */
@Composable
fun dragOpacityAnimation(isDragging: Boolean): Float {
    val alpha by animateFloatAsState(
        targetValue = if (isDragging) 0.5f else 1f,
        animationSpec = tween(200),
        label = "card_opacity"
    )
    return alpha
}