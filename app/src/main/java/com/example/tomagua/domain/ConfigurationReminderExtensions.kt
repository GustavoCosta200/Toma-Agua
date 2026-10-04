package com.example.tomagua.domain

import com.example.tomagua.data.alarm.AlarmTimeCalculator
import com.example.tomagua.data.local.entity.ConfigurationReminder
import java.time.LocalTime

/** Horários em que esta configuração dispara no dia (mesma regra do agendador). */
fun ConfigurationReminder.dailyTimes(): List<LocalTime> =
    if (hourInterval <= 0) emptyList()
    else AlarmTimeCalculator.calculateDailyTimes(startHour, endHour, hourInterval)

/** Quantos lembretes disparam por dia. */
fun ConfigurationReminder.dailyTriggers(): Int = dailyTimes().size

/** Total de ml que esta configuração representa em um dia. */
fun ConfigurationReminder.dailyMl(): Int = mlQuantity * dailyTriggers()

/** Quantos já dispararam até agora (para um "X de Y notificações hoje"). */
fun ConfigurationReminder.triggersSoFar(now: LocalTime = LocalTime.now()): Int =
    dailyTimes().count { !it.isAfter(now) }