package com.example.tomagua.ui.configuration

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tomagua.data.local.entity.ConfigurationReminder
import com.example.tomagua.domain.repository.ConfigurationReminderRepository
import com.example.tomagua.domain.schedule.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

data class ConfigurationReminderUiState(
    val intervalHours: Int = 1,
    val startTime: LocalTime = LocalTime.of(8, 0),
    val endTime: LocalTime = LocalTime.of(22, 0),
    val waterQuantityMl: Int = 250,
    val soundUri: String? = null,
    val message: String = "",
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveCompleted: Boolean = false
) {
    val isValid: Boolean
        get() = startTime.isBefore(endTime) && waterQuantityMl > 0 && intervalHours > 0
}

@HiltViewModel
class ConfigurationReminderEditorViewModel @Inject constructor(
    private val configurationReminderRepository: ConfigurationReminderRepository,
    private val reminderScheduler: ReminderScheduler,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    private val profileId: Long = checkNotNull(savedStateHandle["profileId"])
    // Id do lembrete já existente neste perfil (null = ainda não existe, vai inserir)
    private var existingId: Long? = null
    private val _uiState = MutableStateFlow(ConfigurationReminderUiState())
    val uiState: StateFlow<ConfigurationReminderUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Leitura única: só queremos preencher o formulário, não observar mudanças
            configurationReminderRepository.watchByProfile(profileId).first()
                .firstOrNull()
                ?.let { existing ->
                    existingId = existing.id
                    _uiState.update {
                        it.copy(
                            intervalHours = existing.hourInterval,
                            startTime = existing.startHour,
                            endTime = existing.endHour,
                            waterQuantityMl = existing.mlQuantity,
                            soundUri = existing.soundUri,
                            message = existing.message,
                            isEditing = true
                        )
                    }
                }
        }
    }

    fun onIntervalChanged(hours: Int) = _uiState.update { it.copy(intervalHours = hours) }
    fun onStartTimeChanged(time: LocalTime) = _uiState.update { it.copy(startTime = time) }
    fun onEndTimeChanged(time: LocalTime) = _uiState.update { it.copy(endTime = time) }
    fun onWaterQuantityChanged(ml: Int) = _uiState.update { it.copy(waterQuantityMl = ml) }
    fun onSoundSelected(uri: String) = _uiState.update { it.copy(soundUri = uri) }
    fun onMessageChanged(text: String) = _uiState.update { it.copy(message = text) }

    fun save() {
        if (!_uiState.value.isValid) {
            _uiState.update { it.copy(error = "Horário final deve ser depois do inicial") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            try {
                val s = _uiState.value
                val currentId = existingId
                val configuration = ConfigurationReminder(
                    id = currentId?:0,
                    profileId = profileId,
                    hourInterval = s.intervalHours,
                    startHour = s.startTime,
                    endHour = s.endTime,
                    mlQuantity = s.waterQuantityMl,
                    soundUri = s.soundUri,
                    message = s.message
                )

                val savedId = if (currentId != null){
                    configurationReminderRepository.update(configuration)
                    currentId
                } else {
                    configurationReminderRepository.insert(configuration)
                }
                // schedule() já faz cancel() antes, então os alarmes antigos são substituídos
                reminderScheduler.schedule(configuration.copy(id = savedId))
                _uiState.update { it.copy(isSaving = false, saveCompleted = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }

    // Temporário, só pra validar o fluxo antes de agendar a lista inteira (Etapa 7, passo de teste)
    fun scheduleTestReminder(profileId: Long) {
        val s = _uiState.value
        reminderScheduler.scheduleTest(
            ConfigurationReminder(
                profileId = profileId,
                hourInterval = s.intervalHours,
                startHour = s.startTime,
                endHour = s.endTime,
                mlQuantity = s.waterQuantityMl,
                soundUri = s.soundUri,
                message = s.message.ifBlank { "Teste de notificação" }
            )
        )
    }
}