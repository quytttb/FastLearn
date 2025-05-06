package com.app.fastlearn.ui.navigation

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.app.fastlearn.R
import com.app.fastlearn.ui.navigation.DestinationsArgs.DOCUMENT_ID
import com.app.fastlearn.ui.navigation.DestinationsArgs.IMAGE_URI

/**
 * Định nghĩa [Screens]
 */

private object Screens {

    const val DOCUMENTS = "documents"
    const val FLASHCARDS = "flashcards"
    const val STUDY = "study"
    const val OCR = "ocr"
    const val PROFILE = "profile"
    const val DOCUMENT_DETAIL = "documentDetail"
    const val CREATE = "create"
    const val OPTION = "option"
    const val STATISTICS = "statistics"
}

/**
 * Các argument được dùng trong các route
 */

object DestinationsArgs {
    const val IMAGE_URI = "imageUri"
    const val DOCUMENT_ID = "documentId"
}

/**
 * Định nghĩa [Destinations]
 */
object Destinations {
    const val DOCUMENTS_ROUTE = Screens.DOCUMENTS
    const val FLASHCARDS_ROUTE = "${Screens.FLASHCARDS}/{${DOCUMENT_ID}}"
    const val OCR_ROUTE = "${Screens.OCR}/{${IMAGE_URI}}"
    const val DOCUMENT_DETAIL_ROUTE = "${Screens.DOCUMENT_DETAIL}/{${DOCUMENT_ID}}"
    const val STUDY_ROUTE = "${Screens.STUDY}/{${DOCUMENT_ID}}"
    const val PROFILE_ROUTE = Screens.PROFILE
    const val CREATE_ROUTE = Screens.CREATE
    const val OPTION_ROUTE = "${Screens.OPTION}/{${DOCUMENT_ID}}"
    const val STATISTICS = Screens.STATISTICS
}

/**
 * Định nghĩa các bottom screen [Navigation]
 */
sealed class Navigation(val route: String, val labelResId: Int, val icon: ImageVector) {
    object Documents : Navigation(
        Destinations.DOCUMENTS_ROUTE,
        R.string.documents_screen_title,
        Icons.Filled.Description
    )

    object Create : Navigation(
        Destinations.CREATE_ROUTE,
        R.string.create_screen_title,
        Icons.Outlined.AddCircleOutline
    )

    object Statistics : Navigation(
        Destinations.STATISTICS,
        R.string.statistics_title,
        Icons.Filled.Analytics
    )

    // Items sẽ được hiển thị trên BottomNavigationBar
    companion object {
        val bottomNavItems = listOf(Documents, Create, Statistics)
    }
}

/**
 * Các Navigation trong ứng dụng
 */
class NavigationActions(private val navController: NavHostController) {

    private fun navigateWithDefaultOptions(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToDocuments() {
        navigateWithDefaultOptions(Destinations.DOCUMENTS_ROUTE)
    }

    fun navigatePopBackStackToDocuments() {
        // Pop back stack tới Documents Screen và xóa tất cả các màn hình phía trên nó
        navController.navigate(Destinations.DOCUMENTS_ROUTE) {
            popUpTo(Destinations.DOCUMENTS_ROUTE) {
                inclusive = false // Giữ Documents Screen
            }
            launchSingleTop = true
        }
    }

    fun navigateToCreate() {
        navigateWithDefaultOptions(Destinations.CREATE_ROUTE)
    }

    fun navigateToDocumentDetail(documentId: String) {
        val route = "${Screens.DOCUMENT_DETAIL}/$documentId"
        navController.navigate(route) {
            launchSingleTop = true
        }
    }

    fun navigateToOption(documentId: String) {
        val route = "${Screens.OPTION}/$documentId"
        navController.navigate(route) {
            launchSingleTop = true
        }
    }

    fun navigateToStatistics() {
        navigateWithDefaultOptions(Destinations.STATISTICS)
    }

    fun navigateToFlashcards(documentId: String) {
        val route = "${Screens.FLASHCARDS}/$documentId"
        navController.navigate(route) {
            launchSingleTop = true
        }
    }

    fun navigateToStudy(documentId: String) {
        val route = "${Screens.STUDY}/$documentId"
        navController.navigate(route) {
            launchSingleTop = true
        }
    }

    fun navigateToOCR(imageUri: String) {
        val encodedUri = Uri.encode(imageUri)
        val route = "${Screens.OCR}/$encodedUri"
        navController.navigate(route) {
            // Không sử dụng popUpTo đến startDestination cho OCR
            launchSingleTop = true
        }
    }

    fun navigateConfirmFromOCR() {
        navController.navigate(Destinations.DOCUMENTS_ROUTE) {
            // Loại bỏ tất cả các màn hình phía trên DocumentsScreen
            popUpTo(Destinations.DOCUMENTS_ROUTE) {
                inclusive = true // Loại bỏ cả màn hình Documents hiện tại
            }
            launchSingleTop = true // Mở một instance mới của DocumentsScreen
        }
    }

    fun navigateToProfile() {
        navigateWithDefaultOptions(Destinations.PROFILE_ROUTE)
    }

    fun navigateBack() {
        navController.popBackStack()
    }
}