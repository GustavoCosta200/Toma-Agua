package com.example.tomagua.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tomagua.data.local.entity.Profile
import com.example.tomagua.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileListViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
): ViewModel() {

    val profiles: StateFlow<List<Profile>> = profileRepository.showAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteProfile(profile: Profile){
        viewModelScope.launch { profileRepository.delete(profile) }
    }

    fun setActiveProfile(profileId: Long){
        viewModelScope.launch { profileRepository.defineAsActive(profileId) }
    }
}