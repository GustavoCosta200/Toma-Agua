package com.example.tomagua

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationManagerCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.tomagua.data.alarm.NotificationIntents
import com.example.tomagua.navigation.TomaAguaNavHost
import com.example.tomagua.ui.notification.ConsumptionRequest
import com.example.tomagua.ui.notification.RegisterConsumptionDialog
import com.example.tomagua.ui.theme.TomaAguaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var pendingRequest by mutableStateOf<ConsumptionRequest?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Só processa na criação "de verdade": numa recriação (rotação) o mesmo
        // intent voltaria e o diálogo reapareceria.
        if (savedInstanceState == null) handleNotificationIntent(intent)

        setContent {
            TomaAguaTheme {
                TomaAguaNavHost()
                pendingRequest?.let { request ->
                    RegisterConsumptionDialog(
                        request = request,
                        onDismiss = { pendingRequest = null }
                    )
                }
            }
        }
    }

    // Chamado com o app já aberto (launchMode = singleTop)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        val configId = intent?.getLongExtra(NotificationIntents.EXTRA_CONFIG_ID, -1L) ?: -1L
        if (configId == -1L) return

        // Redundante com setAutoCancel(true), mas garante que a notificação saia da barra.
        val notificationId = intent!!.getIntExtra(NotificationIntents.EXTRA_NOTIFICATION_ID, -1)
        if (notificationId != -1) NotificationManagerCompat.from(this).cancel(notificationId)

        pendingRequest = ConsumptionRequest(
            configId = configId,
            amountMl = intent.getIntExtra(NotificationIntents.EXTRA_WATER_QUANTITY, 0)
        )
    }
}