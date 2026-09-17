package com.example.tomagua.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.tomagua.data.local.entity.ConfigurationReminder
import com.example.tomagua.domain.schedule.ReminderScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

class AlarmManagerReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager
) : ReminderScheduler{

    override fun schedule(configuration: ConfigurationReminder) {
        cancel(configuration.id)
        val times = AlarmTimeCalculator.calculateDailyTimes(
            startTime = configuration.startHour,
            endTime = configuration.endHour,
            intervalHours = configuration.hourInterval
        )
        times.forEachIndexed { index, time -> scheduleSlot(configuration, index, time) }
    }

    override fun cancel(configurationId: Long) {
        // Não guardamos quantos slots existiam antes, então tentamos cancelar uma
        // faixa generosa de índices (24 cobre qualquer intervalo >= 1h por dia).
        for (index in 0 until MAX_SLOTS_PER_DAY){
            val pendingIntent = buildPendingIntent(
                configurationId = configurationId,
                slotIndex = index,
                flags = PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            pendingIntent?.let{
                alarmManager.cancel { it }
                it.cancel()
            }
        }
    }

    override fun scheduleTest(configuration: ConfigurationReminder) {
        val testTime = LocalTime.now().plusMinutes(1)
        val triggerAtMillis = System.currentTimeMillis() + 60_000L
        val pendingIntent = buildPendingIntent(
            configurationId = configuration.id,
            slotIndex = TEST_SLOT_INDEX,
            flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            configuration = configuration,
            hour = testTime.hour,
            minute = testTime.minute
        )!!
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent)
    }

    private fun scheduleSlot(configuration: ConfigurationReminder, index: Int, time: LocalTime){
        val pendingIntent = buildPendingIntent(
            configurationId = configuration.id,
            slotIndex = index,
            flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            configuration = configuration,
            hour = time.hour,
            minute = time.minute
        )!!
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            nextTriggerMillis(time),
            pendingIntent
        )
    }

    private fun nextTriggerMillis(time: LocalTime, now: LocalDateTime = LocalDateTime.now()): Long {
        var target = LocalDateTime.of(now.toLocalDate(), time)
        if (!target.isAfter(now)) target = target.plusDays(1)
        return target.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    private fun buildPendingIntent(
        configurationId: Long,
        slotIndex: Int,
        flags: Int,
        configuration: ConfigurationReminder? = null,
        hour: Int = 0,
        minute: Int = 0
    ): PendingIntent?{
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_REMINDER
            // O "data" (URI) é o que o AlarmManager usa pra identificar/casar o PendingIntent
            // na hora de cancelar — extras não entram nessa comparação.
            data = Uri.parse("tomaagua://reminder/$configurationId/$slotIndex")
            if (configuration != null){
                putExtra(AlarmReceiver.EXTRA_CONFIG_ID, configurationId)
                putExtra(AlarmReceiver.EXTRA_SLOT_INDEX, slotIndex)
                putExtra(AlarmReceiver.EXTRA_HOUR, hour)
                putExtra(AlarmReceiver.EXTRA_MINUTE, minute)
                putExtra(AlarmReceiver.EXTRA_MESSAGE, configuration.message)
                putExtra(AlarmReceiver.EXTRA_WATER_QUANTITY, configuration.mlQuantity)
                putExtra(AlarmReceiver.EXTRA_SOUND_URI, configuration.soundUri)
            }
        }
        val requestCode = (configurationId * 100 + slotIndex).toInt()
        return PendingIntent.getBroadcast(context, requestCode, intent, flags)
    }

    companion object {
        private const val MAX_SLOTS_PER_DAY = 24
        private const val TEST_SLOT_INDEX = 999
        const val ACTION_REMINDER = "com.example.tomagua.ACTION_REMINDER"
    }
}