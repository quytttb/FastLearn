package com.app.fastlearn.ui.animations

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

/**
 * Hoạt hình cho hiệu ứng lật thẻ
 * @param isFlipped xác định thẻ đã được lật hay chưa
 * @return giá trị góc quay của thẻ
 */
@Composable
fun flipAnimation(isFlipped: Boolean): Float {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(500),
        label = "card_flip"
    )
    return rotation
}