package com.example.tomagua

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.tomagua.navigation.TomaAguaNavHost
import com.example.tomagua.ui.theme.TomaAguaTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            TomaAguaTheme {
                TomaAguaNavHost()
            }
        }
    }
}