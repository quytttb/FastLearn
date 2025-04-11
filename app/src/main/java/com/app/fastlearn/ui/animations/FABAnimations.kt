package com.app.fastlearn.ui.animations

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable

/**
 * Hoạt hình cho việc hiện và ẩn các FAB con
 * @param duration Thời gian hoạt hình (mặc định là 300ms)
 * @return Một cặp EnterTransition (hiện) và ExitTransition (ẩn)
 */
@Composable
fun fabAnimations(duration: Int = 300): Pair<EnterTransition, ExitTransition> {
    val enter = slideInVertically(
        initialOffsetY = { it },
        animationSpec = tween(duration)
    ) + fadeIn(animationSpec = tween(duration))

    val exit = slideOutVertically(
        targetOffsetY = { it },
        animationSpec = tween(duration)
    ) + fadeOut(animationSpec = tween(duration))

    return Pair(enter, exit)
}