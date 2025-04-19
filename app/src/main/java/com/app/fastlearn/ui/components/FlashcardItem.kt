package com.app.fastlearn.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.zIndex
import com.app.fastlearn.domain.model.Flashcard
import com.app.fastlearn.ui.animations.dragOpacityAnimation
import com.app.fastlearn.ui.animations.flipAnimation
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun FlashcardItem(
    flashcard: Flashcard?,
    nextFlashcard: Flashcard?,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onSwipe: () -> Unit,
    dragSpeedMultiplier: Float = 0.5f
) {
    val scope = rememberCoroutineScope()

    // Trạng thái kết hợp để theo dõi vị trí kéo
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    // Thêm trạng thái cho hiệu ứng mượt
    var swiped by remember { mutableStateOf(false) }

    // Thêm trạng thái theo dõi việc swipe thành công
    var isSuccessfulSwipe by remember { mutableStateOf(false) }

    // Sử dụng animatables cho hiệu ứng mượt
    val animatedOffsetX = remember { Animatable(0f) }
    val animatedOffsetY = remember { Animatable(0f) }

    // Độ mờ của thẻ kế tiếp (tăng dần khi current card biến mất)
    val nextCardOpacity = remember { Animatable(0f) }

    // Animatable cho hiệu ứng màu sắc chuyển đổi của thẻ kế tiếp
    val nextCardColorTransition = remember { Animatable(0f) }

    // Hiệu ứng cho current card
    val rotation = flipAnimation(isFlipped)
    val opacity = dragOpacityAnimation(isDragging)

    // Kiểm tra xem có phải là cử chỉ kéo hay không
    fun isSwipeGesture() = abs(offsetX) > 100 || abs(offsetY) > 100

    // Xử lí hoàn thành cử chỉ kéo với hiệu ứng mượt
    fun completeSwipe() {
        if (isSwipeGesture()) {
            // Xác định hướng vuốt để tạo hiệu ứng biến mất
            val directionX = if (offsetX > 0) 1.5f else -1.5f
            val directionY = if (offsetY > 0) 1f else -1f
            swiped = true
            isSuccessfulSwipe = true

            scope.launch {
                // Hiệu ứng thẻ kế tiếp hiện ra và chuyển đổi màu
                launch {
                    // Chạy đồng thời cả độ opacity và chuyển đổi màu
                    nextCardOpacity.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(300)
                    )
                    nextCardColorTransition.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(300)
                    )
                }

                // Thẻ hiện tại di chuyển theo hướng vuốt và biến mất
                val targetX = offsetX + 1000 * directionX
                val targetY = offsetY + 500 * directionY

                animatedOffsetX.animateTo(
                    targetValue = targetX,
                    animationSpec = tween(300, easing = LinearEasing)
                )

                // Sau khi hoàn thành hiệu ứng, đặt lại vị trí và gọi callback
                offsetX = 0f
                offsetY = 0f
                animatedOffsetX.snapTo(0f)
                animatedOffsetY.snapTo(0f)
                nextCardOpacity.snapTo(0f)
                nextCardColorTransition.snapTo(0f)
                swiped = false
                isSuccessfulSwipe = false
                onSwipe()
            }
        } else {
            // Nếu không đủ xa, thẻ sẽ trở về vị trí ban đầu với hiệu ứng tuyến tính
            scope.launch {
                // Sử dụng hiệu ứng tuyến tính cho cả hai trục
                animatedOffsetX.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(150, easing = LinearEasing)
                )
                animatedOffsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(150, easing = LinearEasing)
                )
                offsetX = 0f
                offsetY = 0f
                nextCardOpacity.snapTo(0f)
                nextCardColorTransition.snapTo(0f)
            }
        }
    }

    // Cập nhật animatable khi offsetX, offsetY thay đổi
    LaunchedEffect(offsetX, offsetY) {
        if (isDragging && !swiped) {
            animatedOffsetX.snapTo(offsetX)
            animatedOffsetY.snapTo(offsetY)

            // Cập nhật độ mờ của thẻ kế tiếp dựa trên mức độ kéo
            val dragProgress = (abs(offsetX) + abs(offsetY)) / 200f
            nextCardOpacity.snapTo(dragProgress.coerceIn(0f, 0.8f))

            // Chỉ cập nhật transition màu sắc nếu đang tiến tới một swipe thành công
            if (isSwipeGesture()) {
                nextCardColorTransition.snapTo(dragProgress.coerceIn(0f, 0.8f))
            }
        }
    }

    // Stack effect - hiển thị các thẻ chồng lên nhau
    Box(modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1.5f)) {

        // Next card (behind current card)
        if (nextFlashcard != null) {
            // Tính toán màu chuyển đổi dựa trên giá trị animation
            val progress = nextCardColorTransition.value

            // Màu gốc và màu đích cho container
            val startContainerColor = MaterialTheme.colorScheme.primaryContainer
            val targetContainerColor = MaterialTheme.colorScheme.primary

            // Màu gốc và màu đích cho nội dung (text)
            val startContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            val targetContentColor = MaterialTheme.colorScheme.onPrimary

            // Tính toán màu hiện tại dựa trên giá trị progress
            val currentContainerColor = lerp(startContainerColor, targetContainerColor, progress)
            val currentContentColor = lerp(startContentColor, targetContentColor, progress)

            FlashcardContent(
                flashcard = nextFlashcard,
                isFlipped = false,
                offsetX = 0f,
                offsetY = 0f,
                opacity = nextCardOpacity.value,
                rotation = 0f,
                elevation = 3.dp,
                containerColor = currentContainerColor,
                contentColor = currentContentColor,
                onClick = {},
                onDrag = { _, _ -> },
                onDragStart = {},
                onDragEnd = {},
                onDragCancel = {},
                modifier = Modifier.zIndex(0f)
            )
        }

        // Current card (on top)
        if (flashcard != null) {
            FlashcardContent(
                flashcard = flashcard,
                isFlipped = isFlipped,
                offsetX = animatedOffsetX.value,
                offsetY = animatedOffsetY.value,
                opacity = opacity,
                rotation = rotation,
                elevation = 6.dp,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = { if (!isDragging) onFlip() },
                onDrag = { change, dragAmount ->
                    change.consume()
                    val adjustedDragX = dragAmount.x * dragSpeedMultiplier
                    val adjustedDragY = dragAmount.y * dragSpeedMultiplier

                    val newX = if (isFlipped) {
                        offsetX - adjustedDragX
                    } else {
                        offsetX + adjustedDragX
                    }
                    offsetX = newX
                    offsetY += adjustedDragY
                },
                onDragStart = { isDragging = true },
                onDragEnd = {
                    completeSwipe()
                    isDragging = false
                },
                onDragCancel = {
                    scope.launch {
                        // Sử dụng hiệu ứng tuyến tính khi hủy kéo
                        animatedOffsetX.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(150, easing = LinearEasing)
                        )
                        animatedOffsetY.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(150, easing = LinearEasing)
                        )
                        offsetX = 0f
                        offsetY = 0f
                        nextCardColorTransition.snapTo(0f)
                    }
                    isDragging = false
                },
                modifier = Modifier.zIndex(1f)
            )
        }
    }
}

// Hàm để tính toán chuyển đổi giữa hai màu
private fun lerp(start: Color, end: Color, fraction: Float): Color {
    return Color(
        red = lerp(start.red, end.red, fraction),
        green = lerp(start.green, end.green, fraction),
        blue = lerp(start.blue, end.blue, fraction),
        alpha = lerp(start.alpha, end.alpha, fraction)
    )
}

// Hàm hỗ trợ nội suy tuyến tính (linear interpolation)
private fun lerp(start: Float, end: Float, fraction: Float): Float {
    return start + (end - start) * fraction
}

@Composable
private fun FlashcardContent(
    flashcard: Flashcard,
    isFlipped: Boolean,
    offsetX: Float,
    offsetY: Float,
    opacity: Float,
    rotation: Float,
    elevation: Dp,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.5f)
            .offset(x = offsetX.dp, y = offsetY.dp)
            .graphicsLayer {
                alpha = opacity
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .pointerInput(isFlipped) {
                detectDragGestures(
                    onDragStart = { onDragStart() },
                    onDragEnd = { onDragEnd() },
                    onDragCancel = { onDragCancel() },
                    onDrag = onDrag
                )
            }
            .then(modifier),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        onClick = onClick
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
                text = flashcard.question,
                isFrontSide = true
            )

            // Back side (Answer)
            CardContent(
                isVisible = isFlipped,
                rotation = 0f,
                text = flashcard.answer,
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