package com.example.tomagua.data.alarm

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.RequiresPermission
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver: BroadcastReceiver() {

    @Inject lateinit var notificationHelper: NotificationHelper
    @Inject lateinit var alarmManager:  AlarmManager

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        val configId = intent.getLongExtra(EXTRA_CONFIG_ID, -1L)
        if (configId == -1L) return

        val slotIndex = intent.getIntExtra(EXTRA_SLOT_INDEX, 0)
        val hour = intent.getIntExtra(EXTRA_HOUR, 8)
        val minute = intent.getIntExtra(EXTRA_MINUTE, 0)
        val message = intent.getStringExtra(EXTRA_MESSAGE).orEmpty()
        val waterQuantityMl = intent.getIntExtra(EXTRA_WATER_QUANTITY, 250)
        val soundUri = intent.getStringExtra(EXTRA_SOUND_URI)

        val notificationId = (configId * 100 + slotIndex).toInt()
        notificationHelper.showReminderNotification(notificationId, message, waterQuantityMl, soundUri)

        // Auto-reagendamento: como setExactAndAllowWhileIdle() não repete sozinho,
        // recriamos o mesmo horário pro dia seguinte assim que este disparo acontece.
        rescheduleNextDay(context, configId, slotIndex, hour, minute, message, waterQuantityMl, soundUri)
    }

    private fun rescheduleNextDay(
        context: Context,
        configId: Long,
        slotIndex: Int,
        hour: Int,
        minute: Int,
        message: String,
        waterQuantityMl: Int,
        soundUri: String?
    ) {
        val nextTrigger = LocalDateTime.now()
            .plusDays(1)
            .withHour(hour).withMinute(minute).withSecond(0).withNano(0)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val nextIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmManagerReminderScheduler.ACTION_REMINDER
            data = Uri.parse("tomaagua://reminder/$configId/$slotIndex")
            putExtra(EXTRA_CONFIG_ID, configId)
            putExtra(EXTRA_SLOT_INDEX, slotIndex)
            putExtra(EXTRA_HOUR, hour)
            putExtra(EXTRA_MINUTE, minute)
            putExtra(EXTRA_MESSAGE, message)
            putExtra(EXTRA_WATER_QUANTITY, waterQuantityMl)
            putExtra(EXTRA_SOUND_URI, soundUri)
        }
        val requestCode = (configId * 100 + slotIndex).toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTrigger, pendingIntent)
    }

    companion object {
        const val EXTRA_CONFIG_ID = "extra_config_id"
        const val EXTRA_SLOT_INDEX = "extra_slot_index"
        const val EXTRA_HOUR = "extra_hour"
        const val EXTRA_MINUTE = "extra_minute"
        const val EXTRA_MESSAGE = "extra_message"
        const val EXTRA_WATER_QUANTITY = "extra_water_quantity"
        const val EXTRA_SOUND_URI = "extra_sound_uri"
    }
}