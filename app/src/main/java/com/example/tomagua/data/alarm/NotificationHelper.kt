package com.example.tomagua.data.alarm

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.jar.Manifest
import javax.inject.Inject

class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun channelIdFor(soundUri: String?): String{
        val suffix = soundUri?.hashCode()?.toString() ?: "default"
        return "reminder_channel_$suffix"
    }

    private fun ensureChannel(soundUri: String?){
        val channelId = channelIdFor(soundUri)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // createNotificationChannel é idempotente, mas o som só pode ser definido na
        // CRIAÇÃO do canal — não dá pra alterar depois. Por isso um canal por som.
        if (manager.getNotificationChannel(channelId) != null) return

        val soundToUse: Uri = soundUri?.let { Uri.parse(it) }
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val channel = NotificationChannel(
            channelId,
            "Lembretes de Água",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificações para Lembrar de Beber Água"
            setSound(soundToUse, audioAttributes)
            enableVibration(true)
        }
        manager.createNotificationChannel(channel)
    }

    @RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS)
    fun showReminderNotification(
        notificationId: Int,
        message: String,
        waterQuantityMl: Int,
        soundUri: String?
    ){
        ensureChannel(soundUri)
        val channelId = channelIdFor(soundUri)
        val content = message.ifBlank { "Hora de beber água! Beba $waterQuantityMl ml." }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_dialog_info) // Trocar pelo Ícone do App
            .setContentTitle("Toma Água")
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // A partir do Android 13 (API 33), postar notificação exige POST_NOTIFICATIONS
        // concedida em runtime

        val canNotify = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED

        if (canNotify){
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        }
    }
}