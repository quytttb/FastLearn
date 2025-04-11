package com.app.fastlearn.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.app.fastlearn.ui.navigation.DestinationsArgs.IMAGE_NAME
import com.app.fastlearn.ui.navigation.DestinationsArgs.RECOGNIZED_TEXT_ID
import com.app.fastlearn.R

/**
 * Định nghĩa [Screens]
 */

private object Screens {

    const val DOCUMENTS = "documents"
    const val CAPTURE = "capture"
    const val FLASHCARDS = "flashcards"
    const val STUDY = "study"
    const val STUDY_LIST = "studyList"
    const val IMAGE_PREVIEW = "imagePreview"
    const val OCR = "ocr"

    const val DOCUMENT_DETAIL = "documentDetail"
}

/**
 * Các argument được dùng trong các route
 */

object DestinationsArgs {
    const val IMAGE_NAME = "imageName"
    const val RECOGNIZED_TEXT_ID = "recognizedTextId"
    const val DOCUMENT_ID = "documentId"
}

/**
 * Định nghĩa [Destinations]
 */
object Destinations {
    const val DOCUMENTS_ROUTE = Screens.DOCUMENTS
    const val CAPTURE_ROUTE = Screens.CAPTURE
    const val FLASHCARDS_ROUTE = Screens.FLASHCARDS
    const val STUDY_LIST_ROUTE = Screens.STUDY_LIST
    const val STUDY_ROUTE = "${Screens.STUDY}/{${DestinationsArgs.DOCUMENT_ID}}"
    const val IMAGE_PREVIEW_ROUTE = "${Screens.IMAGE_PREVIEW}/{$IMAGE_NAME}"
    const val OCR_ROUTE = "${Screens.OCR}/{$RECOGNIZED_TEXT_ID}"
    const val DOCUMENT_DETAIL_ROUTE = "${Screens.DOCUMENT_DETAIL}/{$IMAGE_NAME}"

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

    object Flashcards : Navigation(
        Destinations.FLASHCARDS_ROUTE,
        R.string.flashcards_screen_title,
        Icons.Filled.CreditCard
    )

    object Study :
        Navigation(
            Destinations.STUDY_LIST_ROUTE,
            R.string.study_screen_title,
            Icons.Filled.School
        )

    // Items sẽ được hiển thị trên BottomNavigationBar
    companion object {
        val bottomNavItems = listOf(Documents, Flashcards, Study)
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

fun navigateToDocumentDetail(documentId: String) {
    val route = "${Destinations.DOCUMENT_DETAIL_ROUTE}/$documentId"
    navigateWithDefaultOptions(route)
}

    fun navigateToCapture() {
        navigateWithDefaultOptions(Destinations.CAPTURE_ROUTE)
    }

    fun navigateToFlashcards() {
        navigateWithDefaultOptions(Destinations.FLASHCARDS_ROUTE)
    }

    fun navigateToStudyList() {
        navigateWithDefaultOptions(Destinations.STUDY_LIST_ROUTE)
    }

    fun navigateToStudy(documentId: String) {
        val route = "${Screens.STUDY}/$documentId"
        navController.navigate(route) {
            launchSingleTop = true
        }
    }

    fun navigateToImagePreview(imageName: String) {
        val route = "${Screens.IMAGE_PREVIEW}/$imageName"
        navController.navigate(route) {
            // Không sử dụng popUpTo đến startDestination cho ImagePreview
            // vì chúng ta muốn quay lại CaptureScreen khi nhấn back
            launchSingleTop = true
        }
    }

    fun navigateToOCR(recognizedTextId: String) {
        val route = "${Screens.OCR}/$recognizedTextId"
        navController.navigate(route) {
            // Không sử dụng popUpTo đến startDestination cho OCR
            // vì chúng ta muốn quay lại ImagePreview khi nhấn back
            launchSingleTop = true
        }
    }

    fun navigateComfirmFromOCR() {
        navController.navigate(Destinations.DOCUMENTS_ROUTE) {
            // Loại bỏ tất cả các màn hình đến màn tài liệu
            popUpTo(Destinations.DOCUMENTS_ROUTE) {
                inclusive = false // Không bao gồm màn hình tài liệu
            }
            launchSingleTop = true
        }
    }

    fun navigateDiscardFromOCR() {
        navController.navigate(Destinations.CAPTURE_ROUTE) {
            // Loại bỏ tất cả các màn hình đến màn hình Chụp ảnh
            popUpTo(Destinations.CAPTURE_ROUTE) {
                inclusive = false // Không bao gồm màn hình chụp ảnh
            }
            launchSingleTop = true
        }
    }


    fun navigateBack() {
        navController.popBackStack()
    }
}