package com.example.tomagua.data.alarm

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.tomagua.domain.repository.ConfigurationReminderRepository
import com.example.tomagua.domain.schedule.ReminderScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Reagenda todos os lembretes quando o sistema perde os alarmes:
 * reboot, mudança de fuso ou permissão de alarme exato concedida de novo.
 */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver(){

    @Inject lateinit var repository: ConfigurationReminderRepository
    @Inject lateinit var scheduler: ReminderScheduler

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action){
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIMEZONE_CHANGED,
            AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED -> Unit
            else -> return
        }

        //goAsync mantém o Receiver vivo enquanto lemos o banco fora da main thread
        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                repository.findAll().forEach { scheduler.schedule(it) }
            } finally {
                pendingResult.finish()
            }
        }
    }
}