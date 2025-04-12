package com.app.fastlearn.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import com.app.fastlearn.ui.screens.documents.DocumentDetailScreen
import com.app.fastlearn.ui.screens.documents.OCRScreen
import com.app.fastlearn.ui.screens.study.StudyListScreen

@Composable
fun FastLearnNavGraph(
    modifier: Modifier = Modifier,
    bottomInnerPadding: PaddingValues = PaddingValues(),
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
                modifier = Modifier.padding(bottom = bottomInnerPadding.calculateBottomPadding()),
                onOpenCamera = { navActions.navigateToCapture() },
                onImportFile = { /*Todo: Xử lý nhập file văn bản*/ },
                onDocumentClick = { documentId ->
                    navActions.navigateToDocumentDetail(documentId)
                },
            )
        }

        composable(Destinations.CAPTURE_ROUTE) {
            CaptureScreen(
                modifier = Modifier.padding(bottom = bottomInnerPadding.calculateBottomPadding()),
                onImageCaptured = { imageName ->
                    navActions.navigateToImagePreview(imageName)
                }
            )
        }

        composable(Destinations.FLASHCARDS_ROUTE) {
            FlashcardsScreen(
                modifier = Modifier.padding(bottom = bottomInnerPadding.calculateBottomPadding()),
            )
        }

        // New StudyListScreen composable
        composable(Destinations.STUDY_LIST_ROUTE) {
            StudyListScreen(
                modifier = Modifier.padding(bottom = bottomInnerPadding.calculateBottomPadding()),
                onDocumentSelected = { documentId ->
                    navActions.navigateToStudy(documentId)
                },
                onNavigateToDocuments = {
                    navActions.navigateToDocuments()
                }
            )
        }

        composable(
            Destinations.IMAGE_PREVIEW_ROUTE,
            arguments = listOf(
                navArgument(DestinationsArgs.IMAGE_NAME) { type = NavType.StringType }
            )
        ) { entry ->
            ImagePreviewScreen(
                modifier = Modifier.padding(bottom = bottomInnerPadding.calculateBottomPadding()),
                imageName = entry.arguments?.getString(DestinationsArgs.IMAGE_NAME)!!,
                onConfirmNavigate = { recognizedText ->
                    navActions.navigateToOCR(recognizedText)
                },
                onDiscardNavigate = { navActions.navigateBack() }
            )
        }

        composable(
            route = Destinations.OCR_ROUTE,
            arguments = listOf(
                navArgument(DestinationsArgs.RECOGNIZED_TEXT_ID) { type = NavType.StringType }
            )
        ) { entry ->
            OCRScreen(
                modifier = Modifier.padding(bottom = bottomInnerPadding.calculateBottomPadding()),
                onConfirmNavigate = { navActions.navigateComfirmFromOCR() },
                onDiscardNavigate = { navActions.navigateDiscardFromOCR() },
            )
        }

        composable(
            route = "${Destinations.DOCUMENT_DETAIL_ROUTE}/{documentId}",
            arguments = listOf(navArgument(DestinationsArgs.DOCUMENT_ID) {
                type = NavType.StringType
            })
        ) { entry ->
            DocumentDetailScreen(
                documentId = entry.arguments?.getString(DestinationsArgs.DOCUMENT_ID)!!,
                modifier = Modifier.statusBarsPadding(),
                onNavigateBack = { navActions.navigateBack() }
            )
        }

        composable(
            route = "${Destinations.STUDY_ROUTE}/{documentId}",
            arguments = listOf(navArgument(DestinationsArgs.DOCUMENT_ID) {
                type = NavType.StringType
            })
        ) { entry ->
            StudyScreen(
                //documentId = entry.arguments?.getString(DestinationsArgs.DOCUMENT_ID),
                modifier = Modifier.padding(bottom = bottomInnerPadding.calculateBottomPadding()),
                onSendProgress = { documentId ->
                    //Todo: Xử lý gửi tiến trình sang profile
                },
                onNavigateBack = { navActions.navigateBack() },
            )
        }
    }
}