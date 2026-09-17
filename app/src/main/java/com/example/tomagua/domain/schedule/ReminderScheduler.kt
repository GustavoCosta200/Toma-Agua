package com.example.tomagua.domain.schedule

import com.example.tomagua.data.local.entity.ConfigurationReminder

interface ReminderScheduler {
    // Cancela os alarmes antigos dessa configuração e agenda novos horários no dia
    fun schedule(configuration: ConfigurationReminder)

    // Cancela todos os alarmes agendados para esta configuração
    fun cancel(configurationId: Long)

    // Agenda um único alarme de teste, 1 minuto no futuro, para validar o fluxo de funcionamento
    fun scheduleTest(configuration: ConfigurationReminder)
}