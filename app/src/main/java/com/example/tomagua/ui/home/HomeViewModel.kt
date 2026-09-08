package com.example.tomagua.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tomagua.data.local.entity.ConsumptionRecords
import com.example.tomagua.data.local.entity.Profile
import com.example.tomagua.domain.repository.ConfigurationReminderRepository
import com.example.tomagua.domain.repository.ConsumptionRecordsRepository
import com.example.tomagua.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

data class HomeUiState(
    val activeProfile: Profile? = null,
    val consumedTodayMl: Int = 0,
    val goalMl: Int = 2000,
    val isLoading: Boolean = true
){
    val progress: Float
        get() = if (goalMl == 0) 0f else (consumedTodayMl.toFloat()/goalMl).coerceIn(0f, 1f)
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val configurationReminderRepository: ConfigurationReminderRepository,
    private val consumptionRecordsRepository: ConsumptionRecordsRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = profileRepository.showActive()
        .flatMapLatest { profile ->
            if (profile == null) {
                flowOf(HomeUiState(isLoading = false))
            } else {
                combine(
                    configurationReminderRepository.watchDailyGoal(profile.id),
                    consumptionRecordsRepository.watchTotalConsumedTodayByProfile(profile.id)
                ) { goalMl, consumedMl ->
                    HomeUiState(
                        activeProfile = profile,
                        consumedTodayMl = consumedMl,
                        goalMl = goalMl,
                        isLoading = false
                    )
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState()
        )

    fun registerConsumption(configurationId: Long, amountMl: Int) {
        viewModelScope.launch {
            consumptionRecordsRepository.insert(
                ConsumptionRecords(
                    configurationId = configurationId,
                    dateTime = LocalDateTime.now(),
                    mlQuantity = amountMl,
                    confirmed = true
                )
            )
        }
    }
}