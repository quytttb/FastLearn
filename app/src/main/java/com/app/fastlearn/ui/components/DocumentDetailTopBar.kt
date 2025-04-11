package com.app.fastlearn.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import com.app.fastlearn.R
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentDetailTopBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior,
    onBackClick: () -> Unit,
    onCreateFlashcardsClick: () -> Unit
) {
    // Calculate the status bar color based on scroll state
    val statusBarColor = MaterialTheme.colorScheme.surface.copy(
        alpha = scrollBehavior.state.overlappedFraction
    )

    // Apply status bar color using accompanist-systemuicontroller
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = MaterialTheme.colorScheme.surface.luminance() > 0.5f

    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = useDarkIcons
        )
    }

    LargeTopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.navigate_back)
                )
            }
        },
        actions = {
            IconButton(onClick = onCreateFlashcardsClick) {
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = stringResource(R.string.create_flashcards)
                )
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}