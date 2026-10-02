package com.example.tomagua.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tomagua.data.local.entity.Profile
import com.example.tomagua.domain.repository.ConfigurationReminderRepository
import com.example.tomagua.domain.repository.ProfileRepository
import com.example.tomagua.domain.schedule.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileListViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val configurationRepository: ConfigurationReminderRepository,
    private val scheduler: ReminderScheduler
) : ViewModel() {

    val profiles: StateFlow<List<Profile>> = profileRepository.showAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteProfile(profile: Profile) {
        viewModelScope.launch {
            // Cancela os alarmes ANTES de apagar: depois do cascade não há mais ids para consultar
            configurationRepository.watchByProfile(profile.id).first()
                .forEach { scheduler.cancel(it.id) }
            profileRepository.delete(profile)
        }
    }

    fun setActiveProfile(profileId: Long) {
        viewModelScope.launch { profileRepository.defineAsActive(profileId) }
    }

    fun canScheduleExactAlarms(): Boolean = scheduler.canScheduleExactAlarms()
}