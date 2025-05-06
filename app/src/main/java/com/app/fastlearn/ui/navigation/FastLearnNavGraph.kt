package com.app.fastlearn.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
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
import com.app.fastlearn.ui.screens.create.CreateScreen
import com.app.fastlearn.ui.screens.documents.DocumentDetailScreen
import com.app.fastlearn.ui.screens.documents.DocumentsScreen
import com.app.fastlearn.ui.screens.ocr.OCRScreen
import com.app.fastlearn.ui.screens.flashcards.FlashcardsScreen
import com.app.fastlearn.ui.screens.option.OptionScreen
import com.app.fastlearn.ui.screens.profile.ProfileScreen
import com.app.fastlearn.ui.screens.statistics.StatisticsScreen
import com.app.fastlearn.ui.screens.study.StudyScreen

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
//    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = currentNavBackStackEntry?.destination?.route ?: startDestination

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Destinations.DOCUMENTS_ROUTE) {
            DocumentsScreen(
                modifier = Modifier.padding(bottom = bottomInnerPadding.calculateBottomPadding()),
                onProfileClick = { navActions.navigateToProfile() },
                onDocumentClick = { documentId ->
                    navActions.navigateToOption(documentId)
                },
            )
        }

        composable(Destinations.CREATE_ROUTE) {
            CreateScreen(
                modifier = Modifier.padding(bottom = bottomInnerPadding.calculateBottomPadding()),
                onOCRClick = { imageUri ->
                    navActions.navigateToOCR(imageUri)
                },
                onFileClick = {
                    // Handle file import
                }
            )
        }

        composable(Destinations.STATISTICS) {
            StatisticsScreen(
                modifier = Modifier.padding(bottom = bottomInnerPadding.calculateBottomPadding()),
            )
        }


        composable(
            route = Destinations.OPTION_ROUTE,
            arguments = listOf(navArgument(DestinationsArgs.DOCUMENT_ID) {
                type = NavType.StringType
            })
        ) { entry ->
            OptionScreen(
                documentId = entry.arguments?.getString(DestinationsArgs.DOCUMENT_ID)!!,
                modifier = Modifier.statusBarsPadding(),
                onNavigateBack = { navActions.navigateBack() },
                onViewDocument = { docId ->
                    navActions.navigateToDocumentDetail(docId)
                },
                onViewFlashcards = { docId ->
                    navActions.navigateToFlashcards(docId)
                    // Need to add logic to load specific document flashcards
                },
                onStudy = { docId ->
                    navActions.navigateToStudy(docId)
                }
            )
        }


        composable(
            route = Destinations.FLASHCARDS_ROUTE,
            arguments = listOf(navArgument(DestinationsArgs.DOCUMENT_ID) {
                type = NavType.StringType
            })
        ) { entry ->
            FlashcardsScreen(
                modifier = Modifier.padding(bottom = bottomInnerPadding.calculateBottomPadding()),
                onNavigateBack = { navActions.navigateBack() },
            )
        }



        composable(
            route = Destinations.OCR_ROUTE,
            arguments = listOf(
                navArgument(DestinationsArgs.IMAGE_URI) { type = NavType.StringType }
            )
        ) { entry ->
            OCRScreen(
                modifier = Modifier.padding(bottom = bottomInnerPadding.calculateBottomPadding()),
                imageUri = entry.arguments?.getString(DestinationsArgs.IMAGE_URI),
                onConfirmNavigate = { navActions.navigateConfirmFromOCR() },
                onDiscardNavigate = { navActions.navigateBack() },
            )
        }

        composable(
            route = Destinations.DOCUMENT_DETAIL_ROUTE,
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
            route = Destinations.STUDY_ROUTE,
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

        composable(Destinations.PROFILE_ROUTE) {
            ProfileScreen(
                modifier = Modifier.padding(bottom = bottomInnerPadding.calculateBottomPadding()),
                onNavigateToStatistics = { /* Will implement later */ },
                onNavigateToHistory = { /* Will implement later */ }
            )
        }
    }
}