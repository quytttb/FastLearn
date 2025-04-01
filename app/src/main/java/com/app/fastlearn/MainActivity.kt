package com.app.fastlearn

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.fastlearn.ui.navigation.BottomNavigationBar
import com.app.fastlearn.ui.navigation.Destinations
import com.app.fastlearn.ui.navigation.FastLearnNavGraph
import com.app.fastlearn.ui.navigation.NavigationActions
import com.app.fastlearn.ui.theme.FastLearnTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FastLearnTheme {
                // Khởi tạo NavHostController
                val navController = rememberNavController()

                // Khởi tạo NavigationActions
                val navigationActions = remember(navController) {
                    NavigationActions(navController)
                }

                // Trạng thái của BottomBar, đặt trạng thái thành false, nếu route trang hiện tại là "capture" hoặc "imagePreview"
                val bottomBarState = rememberSaveable { mutableStateOf(true) }

                // Đăng ký để lấy navBackStackEntry, để lấy route hiện tại
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Đặt trạng thái của BottomBar - Hiển thị bottom bar khi ở màn hình chính
                LaunchedEffect(currentRoute) {
                    bottomBarState.value = when (currentRoute) {
                        Destinations.DOCUMENTS_ROUTE,
                        Destinations.FLASHCARDS_ROUTE,
                        Destinations.STUDY_ROUTE -> true

                        else -> false
                    }

                    Log.d("MainActivity", "BottomBarState $currentRoute: ${bottomBarState.value}")
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavigationBar(
                            navController = navController,
                            bottomBarState = bottomBarState,
                            navigationActions = navigationActions
                        )
                    }
                ) { innerPadding ->
                    val modifier = when (currentRoute) {
                        Destinations.CAPTURE_ROUTE,
                        Destinations.IMAGE_PREVIEW_ROUTE -> Modifier.fillMaxSize()

                        else -> Modifier.padding(innerPadding)
                    }

                    FastLearnApp(
                        modifier = modifier,
                        navController = navController,
                    )
                }
            }
        }
    }
}

@Composable
fun FastLearnApp(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    FastLearnNavGraph(modifier = modifier, navController = navController)
}