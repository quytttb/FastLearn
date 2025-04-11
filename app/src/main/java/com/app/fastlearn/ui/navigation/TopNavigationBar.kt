package com.app.fastlearn.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.res.stringResource
import com.app.fastlearn.R
import com.app.fastlearn.ui.components.TopAppBar

@Composable
fun TopNavigationBar(
    navController: NavController,
    navigationActions: NavigationActions
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isTopLevelDestination = Navigation.Companion.bottomNavItems.any { it.route == currentRoute }
    val title = when (currentRoute) {
        Destinations.DOCUMENTS_ROUTE -> stringResource(id = R.string.documents_screen_title)
        Destinations.FLASHCARDS_ROUTE -> stringResource(id = R.string.flashcards_screen_title)
        Destinations.STUDY_ROUTE -> stringResource(id = R.string.study_screen_title)
        Destinations.CAPTURE_ROUTE -> stringResource(id = R.string.capture_screen_title)
        else -> if (currentRoute?.startsWith(Destinations.IMAGE_PREVIEW_ROUTE) == true) {
            stringResource(id = R.string.image_preview_screen_title)
        } else if (currentRoute?.startsWith(Destinations.OCR_ROUTE) == true) {
            stringResource(id = R.string.ocr_screen_title)
        } else {
            stringResource(id = R.string.app_name)
        }
    }

    TopAppBar(
        title = title,
        showBackButton = !isTopLevelDestination,
        onBackClick = { navigationActions.navigateBack() }
    )
}
