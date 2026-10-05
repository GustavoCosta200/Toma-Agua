package com.example.tomagua.ui.notification

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tomagua.data.local.entity.ConsumptionRecords
import com.example.tomagua.domain.repository.ConfigurationReminderRepository
import com.example.tomagua.domain.repository.ConsumptionRecordsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

data class ConsumptionRequest(val configId: Long, val amountMl: Int)

@HiltViewModel
class RegisterConsumptionViewModel @Inject constructor(
    private val configurationRepository: ConfigurationReminderRepository,
    private val consumptionRepository: ConsumptionRecordsRepository
) : ViewModel() {

    fun register(request: ConsumptionRequest) {
        viewModelScope.launch {
            // A configuração pode ter sido apagada depois da notificação: inserir com FK
            // inexistente derrubaria o app, então validamos antes.
            if (configurationRepository.findById(request.configId) == null) return@launch
            consumptionRepository.insert(
                ConsumptionRecords(
                    configurationId = request.configId,
                    dateTime = LocalDateTime.now(),
                    mlQuantity = request.amountMl,
                    confirmed = true
                )
            )
        }
    }
}

@Composable
fun RegisterConsumptionDialog(
    request: ConsumptionRequest,
    onDismiss: () -> Unit,
    viewModel: RegisterConsumptionViewModel = hiltViewModel()
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar consumo") },
        text = { Text("Você bebeu ${request.amountMl} ml de água?") },
        confirmButton = {
            TextButton(onClick = {
                viewModel.register(request)
                onDismiss()
            }) { Text("Sim, bebi") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Agora não") }
        }
    )
}