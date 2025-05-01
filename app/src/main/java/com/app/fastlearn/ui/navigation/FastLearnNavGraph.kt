package com.app.fastlearn.ui.navigation

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.fastlearn.ui.screens.capture.ImagePreviewScreen
import com.app.fastlearn.ui.screens.documents.DocumentsScreen
import com.app.fastlearn.ui.screens.flashcards.FlashcardsScreen
import com.app.fastlearn.ui.screens.study.StudyScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.fastlearn.ui.screens.documents.DocumentDetailScreen
import com.app.fastlearn.ui.screens.documents.DocumentsViewModel
import com.app.fastlearn.ui.screens.documents.OCRScreen
import com.app.fastlearn.ui.screens.profile.ProfileScreen
import com.app.fastlearn.ui.screens.study.StudyListScreen
import kotlin.toString

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
                onImagePreviewClick = { imageUri ->
                    navActions.navigateToImagePreview(imageUri)
                },
                onImportFile = { /*Todo: Xử lý nhập file văn bản*/ },
                onProfileClick = { navActions.navigateToProfile() },
                onDocumentClick = { documentId ->
                    navActions.navigateToDocumentDetail(documentId)
                },
            )
        }


        composable(Destinations.FLASHCARDS_ROUTE) {
            FlashcardsScreen(
                modifier = Modifier.padding(bottom = bottomInnerPadding.calculateBottomPadding()),
            )
        }

        composable(Destinations.STUDY_LIST_ROUTE) {
            StudyListScreen(
                modifier = Modifier.padding(bottom = bottomInnerPadding.calculateBottomPadding()),
                onDocumentSelected = { documentId ->
                    navActions.navigateToStudy(documentId)
                },
            )
        }

        composable(
            Destinations.IMAGE_PREVIEW_ROUTE,
            arguments = listOf(
                navArgument(DestinationsArgs.IMAGE_URI) { type = NavType.StringType }
            )
        ) { entry ->
            ImagePreviewScreen(
                modifier = Modifier.padding(bottom = bottomInnerPadding.calculateBottomPadding()),
                imageUri = entry.arguments?.getString(DestinationsArgs.IMAGE_URI)!!,
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
                onDiscardNavigate = { TODO("Gọi Intent mở Camera") },
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
                onNavigateBack = { navActions.navigateToDocuments() }
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
                onNavigateBack = { navActions.navigateToStudyList() },
            )
        }

        composable(Destinations.PROFILE_ROUTE) {
            ProfileScreen(
                modifier = Modifier.padding(bottom = bottomInnerPadding.calculateBottomPadding()),
                onNavigateToStatistics = { /* Will implement later */ },
                onNavigateToHistory = { /* Will implement later */ }
            )
        }
    }
}