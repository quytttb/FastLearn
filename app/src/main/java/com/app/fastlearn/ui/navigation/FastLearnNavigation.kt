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

/**
 * Định nghĩa [Screens]
 */

private object Screens {
    const val DOCUMENTS = "documents"
    const val CAPTURE = "capture"
    const val FLASHCARDS = "flashcards"
    const val STUDY = "study"
    const val IMAGE_PREVIEW = "imagePreview"
    const val OCR = "ocr"

    const val CACHED_IMAGES = "cachedImages"
}

/**
 * Các argument được dùng trong các route
 */

object DestinationsArgs {
    const val IMAGE_NAME = "imageName"
    const val RECOGNIZED_TEXT_ID = "recognizedTextId"
}

/**
 * Định nghĩa [Destinations]
 */
object Destinations {
    const val DOCUMENTS_ROUTE = Screens.DOCUMENTS
    const val CAPTURE_ROUTE = Screens.CAPTURE
    const val FLASHCARDS_ROUTE = Screens.FLASHCARDS
    const val STUDY_ROUTE = Screens.STUDY
    const val IMAGE_PREVIEW_ROUTE = "${Screens.IMAGE_PREVIEW}/{$IMAGE_NAME}"
    const val OCR_ROUTE = "${Screens.OCR}/{$RECOGNIZED_TEXT_ID}"

    const val CACHED_IMAGES_ROUTE = Screens.CACHED_IMAGES
}

/**
 * Định nghĩa các bottom screen [Navigation]
 */
sealed class Navigation(val route: String, val label: String, val icon: ImageVector) {
    object Documents : Navigation(Destinations.DOCUMENTS_ROUTE, "Documents", Icons.Filled.Description )
    object Flashcards : Navigation(Destinations.FLASHCARDS_ROUTE, "Flashcards", Icons.Filled.CreditCard)
    object Study : Navigation(Destinations.STUDY_ROUTE, "Study", Icons.Filled.School)

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

    fun navigateToCapture() {
        navigateWithDefaultOptions(Destinations.CAPTURE_ROUTE)
    }

    fun navigateToFlashcards() {
        navigateWithDefaultOptions(Destinations.FLASHCARDS_ROUTE)
    }

    fun navigateToStudy() {
        navigateWithDefaultOptions(Destinations.STUDY_ROUTE)
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

    fun navigateBack() {
        navController.popBackStack()
    }



    fun navigateToCachedImages() {
        navigateWithDefaultOptions(Destinations.CACHED_IMAGES_ROUTE)
    }
}