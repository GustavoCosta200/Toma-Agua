package com.example.tomagua.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tomagua.data.local.entity.ConsumptionRecords
import com.example.tomagua.domain.repository.ConsumptionRecordsRepository
import com.example.tomagua.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class ConsumptionHistoryUiState(
    val records: List<ConsumptionRecords> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val hasActiveProfile: Boolean = true,
    val isLoading: Boolean = false
)

@HiltViewModel
class ConsumptionHistoryViewModel @Inject constructor(
    private val consumptionRecordsRepository: ConsumptionRecordsRepository,
    profileRepository: ProfileRepository
) : ViewModel(){

    private val selectedDate = MutableStateFlow(LocalDate.now())

    val uiState: StateFlow<ConsumptionHistoryUiState> = selectedDate
        .flatMapLatest { date ->
            profileRepository.showActive().flatMapLatest { profile ->
                if (profile == null){
                    flowOf(
                        ConsumptionHistoryUiState(
                            selectedDate = date,
                            hasActiveProfile = false,
                            isLoading = false
                        )
                    )
                } else {
                    consumptionRecordsRepository.watchByProfileAndDate(profile.id, date)
                        .map { records ->
                            ConsumptionHistoryUiState(
                                records = records,
                                selectedDate = date,
                                hasActiveProfile = true,
                                isLoading = false
                            )
                        }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ConsumptionHistoryUiState()
        )

    fun onDateSelected(date: LocalDate){
        selectedDate.value = date
    }

    fun confirmRecord(recordId: Long){
        viewModelScope.launch {
            consumptionRecordsRepository.confirm(recordId)
        }
    }
}