package com.example.tomagua.ui.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tomagua.data.local.entity.Profile
import com.example.tomagua.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileEditorUiState(
    val name: String = "",
    val isActive: Boolean = false,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveCompleted: Boolean = false
)

@HiltViewModel
class ProfileEditorViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel(){

    private val profileId: Long? = savedStateHandle.get<Long>("profileId")
        .takeIf { it != -1L} // -1 é o valor padrão do NavArgument para criação de novo perfil

    private val _uiState = MutableStateFlow(ProfileEditorUiState(isEditing = profileId != null))
    val uiState: StateFlow<ProfileEditorUiState> = _uiState.asStateFlow()

    init {
        profileId?.let { id ->
            viewModelScope.launch {
                profileRepository.getProfileById(id)?.let { profile ->
                    _uiState.update { it.copy(name = profile.name, isActive = profile.isActive) }
                }
            }
        }
    }

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name, error = null) }
    }

    fun save() {
        val name = _uiState.value.name.trim()
        if (name.isBlank()) {
            _uiState.update { it.copy(error = "Nome não pode ser vazio!") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            if (profileId != null){
                profileRepository.update(Profile(id = profileId, name = name, isActive = _uiState.value.isActive))
            } else {
                profileRepository.insert(Profile(name = name))
            }
            _uiState.update { it.copy(isSaving = false, saveCompleted = true) }
        }
    }

}