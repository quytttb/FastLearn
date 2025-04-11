package com.app.fastlearn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.app.fastlearn.ui.navigation.NavigationActions
import com.app.fastlearn.ui.theme.FastLearnTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FastLearnActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FastLearnTheme {

                FastLearnApp()
            }
        }
    }
}