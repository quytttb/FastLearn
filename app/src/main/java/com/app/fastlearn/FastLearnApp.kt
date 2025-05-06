package com.app.fastlearn

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.fastlearn.ui.navigation.BottomNavigationBar
import com.app.fastlearn.ui.navigation.Destinations
import com.app.fastlearn.ui.navigation.FastLearnNavGraph
import com.app.fastlearn.ui.navigation.NavigationActions

//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FastLearnApp(
    modifier: Modifier = Modifier,
) {
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
            Destinations.CREATE_ROUTE,
            Destinations.STATISTICS -> true
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
        FastLearnNavGraph(
            modifier = modifier,
            navController = navController,
            bottomInnerPadding = innerPadding
        )
    }
}