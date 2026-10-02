package com.example.tomagua.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.os.Build

fun AlarmManager.canScheduleExact(): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.S || canScheduleExactAlarms()

/** Alarme exato quando permitido; senão degrada para um alarme aproximado, sem crashar. */
fun AlarmManager.setReminderAlarm(triggerAtMillis: Long, pendingIntent: PendingIntent){
    if(canScheduleExact()){
        setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
    } else {
        setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
    }
}