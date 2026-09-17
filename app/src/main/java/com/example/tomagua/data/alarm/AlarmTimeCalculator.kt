package com.example.tomagua.data.alarm

import java.time.LocalTime

object AlarmTimeCalculator {

    /* Calcula os alarmes do dia em que um lembrete deve disparar
    * dado um intervalo e a janela de [startTime, endTime]*/

    fun calculateDailyTimes(
        startTime: LocalTime,
        endTime: LocalTime,
        intervalHours: Int
    ): List<LocalTime> {
        require(intervalHours > 0) {"IntervalHours deve ser maior que 0"}

        val times = mutableListOf<LocalTime>()
        var current = startTime

        while(!current.isAfter(endTime)){
            times.add(current)
            val next = current.plusHours(intervalHours.toLong())
            // LocalTime "vira" a meia noite. Se o próximo horário não for estritamente depois do atual,
            // significa que passou da meia noite, para porque isso ultrapassa o endHour do mesmo dia.
            if(!next.isAfter(current)) break
            current = next
        }
        return times
    }
}