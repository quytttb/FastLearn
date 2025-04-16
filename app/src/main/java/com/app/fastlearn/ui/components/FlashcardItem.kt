package com.app.fastlearn.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fastlearn.domain.model.Flashcard
import com.app.fastlearn.ui.animations.dragOpacityAnimation
import com.app.fastlearn.ui.animations.flipAnimation
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun FlashcardItem(
    flashcard: Flashcard?,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onSwipe: () -> Unit,
    dragSpeedMultiplier: Float = 0.5f // Tốc độ kéo thẻ, giá trị càng cao thì tốc độ kéo càng nhanh
) {
    val scope = rememberCoroutineScope()

    // Trạng thái kết hợp để theo dõi vị trí kéo
    var offset by remember { mutableStateOf(Pair(0f, 0f)) }
    var isDragging by remember { mutableStateOf(false) }

    // Áp dụng hoạt hình cho độ mờ và góc quay
    val rotation = flipAnimation(isFlipped)
    val opacity = dragOpacityAnimation(isDragging)

    // Kiểm tra xem có phải là cử chỉ kéo hay không
    // Nếu độ dịch chuyển lớn hơn 100dp thì coi như là cử chỉ kéo
    fun isSwipeGesture() = abs(offset.first) > 100 || abs(offset.second) > 100

    // Xử lí hoàn thành cử chỉ kéo
    fun completeSwipe() {
        if (isSwipeGesture()) {
            scope.launch {
                onSwipe()
                offset = Pair(0f, 0f)
                isDragging = false
            }
        } else {
            offset = Pair(0f, 0f)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.5f)
            .offset(x = offset.first.dp, y = offset.second.dp)
            .graphicsLayer {
                alpha = opacity
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .pointerInput(isFlipped) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = {
                        completeSwipe()
                        isDragging = false
                    },
                    onDragCancel = {
                        offset = Pair(0f, 0f)
                        isDragging = false
                    },
                    onDrag = { change, dragAmount ->
                        // Thay đổi vị trí kéo
                        change.consume()
                        val (x, y) = offset

                        // Áp dụng hệ số nhân để tùy chỉnh tốc độ kéo
                        val adjustedDragX = dragAmount.x * dragSpeedMultiplier
                        val adjustedDragY = dragAmount.y * dragSpeedMultiplier

                        // Cập nhật vị trí kéo dựa trên hướng lật
                        val newX = if (isFlipped) {
                            x - adjustedDragX
                        } else {
                            x + adjustedDragX
                        }
                        offset = Pair(newX, y + adjustedDragY)
                    }
                )
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        onClick = { onFlip() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            // Front side (Question)
            CardContent(
                isVisible = !isFlipped,
                rotation = rotation,
                text = flashcard?.question ?: "",
                isFrontSide = true
            )

            // Back side (Answer)
            CardContent(
                isVisible = isFlipped,
                rotation = 0f, // No need for additional rotation here
                text = flashcard?.answer ?: "",
                isFrontSide = false
            )
        }
    }
}

@Composable
private fun CardContent(
    isVisible: Boolean,
    rotation: Float,
    text: String,
    isFrontSide: Boolean
) {
    AnimatedVisibility(visible = isVisible) {
        val contentModifier = if (isFrontSide) {
            Modifier.graphicsLayer { rotationY = rotation }
        } else {
            Modifier.graphicsLayer {
                rotationY = rotation
                rotationZ = 180f
                rotationX = 180f
            }
        }

        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .then(contentModifier),
            fontWeight = if (isFrontSide) FontWeight.Bold else FontWeight.Normal,
            fontSize = if (isFrontSide) 22.sp else 18.sp,
            textAlign = TextAlign.Center
        )
    }
}