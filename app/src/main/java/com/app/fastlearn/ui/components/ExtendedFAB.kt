import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.app.fastlearn.R
import com.app.fastlearn.ui.animations.fabAnimations

// Nút FAB mở rộng
@Composable
fun ExtendedFAB(
    onCameraClick: () -> Unit,
    onFileClick: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val (enterAnimation, exitAnimation) = fabAnimations()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Các nút FAB mở rộng
        AnimatedVisibility(
            visible = isExpanded,
            enter = enterAnimation,
            exit = exitAnimation
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Chụp ảnh
                ExtendedFloatingActionButton(
                    onClick = onCameraClick,
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                    icon = {
                        Icon(
                            Icons.Filled.CameraAlt,
                            contentDescription = stringResource(id = R.string.camera)
                        )
                    },
                    text = { Text(stringResource(id = R.string.camera)) },
                )

                // Chọn file từ thư viện
                ExtendedFloatingActionButton(
                    onClick = onFileClick,
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                    icon = {
                        Icon(
                            Icons.Filled.FileOpen,
                            contentDescription = stringResource(id = R.string.file)
                        )
                    },
                    text = { Text(stringResource(id = R.string.file)) },
                )
            }
        }

        // Spacer để tạo khoảng cách giữa các nút FAB
        Spacer(modifier = Modifier.height(8.dp))

        // Nút FAB chính
        FloatingActionButton(
            onClick = { isExpanded = !isExpanded },
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Filled.Close else Icons.Filled.Add,
                contentDescription = stringResource(id = R.string.add_document),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
