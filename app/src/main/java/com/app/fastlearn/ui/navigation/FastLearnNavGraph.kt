package com.app.fastlearn.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.fastlearn.ui.screens.capture.CaptureScreen
import com.app.fastlearn.ui.screens.capture.ImagePreviewScreen
import com.app.fastlearn.ui.screens.documents.DocumentsScreen
import com.app.fastlearn.ui.screens.flashcards.FlashcardsScreen
import com.app.fastlearn.ui.screens.study.StudyScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import com.app.fastlearn.ui.screens.capture.CachedImagesScreen
import com.app.fastlearn.ui.screens.documents.OCRScreen

@Composable
fun FastLearnNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Destinations.DOCUMENTS_ROUTE,
    navActions: NavigationActions = remember(navController) {
        NavigationActions(navController)
    }
) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: startDestination

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Destinations.DOCUMENTS_ROUTE) {
            DocumentsScreen(
                onOpenCamera = { navActions.navigateToCapture() },
                onImportFile = { navActions.navigateToCachedImages() }
            )
        }
        composable(Destinations.CAPTURE_ROUTE) {
            CaptureScreen(
                onImageCaptured = { imageName ->
                    navActions.navigateToImagePreview(imageName)
                }
            )
        }
        composable(Destinations.FLASHCARDS_ROUTE) {
            FlashcardsScreen()
        }
        composable(Destinations.STUDY_ROUTE) {
            StudyScreen()
        }

        composable(
            Destinations.IMAGE_PREVIEW_ROUTE,
            arguments = listOf(
                navArgument(DestinationsArgs.IMAGE_NAME) { type = NavType.StringType }
            )
        ) { entry ->
            ImagePreviewScreen(
                imageName = entry.arguments?.getString(DestinationsArgs.IMAGE_NAME)!!,
                onConfirmNavigate = { /*TODO: xử lý xác nhận ảnh*/ },
                onDiscardNavigate = { navActions.navigateBack() }
            )
        }
        composable(Destinations.OCR_ROUTE) {
            OCRScreen()
        }

        composable(Destinations.CACHED_IMAGES_ROUTE) {
            CachedImagesScreen()
        }
    }
}