package com.example.medcalendar.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medcalendar.domain.model.Reminder
import com.example.medcalendar.domain.usecases.DeleteUseCase
import com.example.medcalendar.domain.usecases.GetAllRemindersUseCase
import com.example.medcalendar.domain.usecases.InsertUseCase
import com.example.medcalendar.domain.usecases.UpdateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val insertUseCase: InsertUseCase,
    private val updateUseCase: UpdateUseCase,
    private val deleteUseCase: DeleteUseCase,
    private val getAllRemindersUseCase: GetAllRemindersUseCase
) : ViewModel() {

    // stan interfejsu użytkownika
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> get() = _uiState

    init {
        fetchReminders()
    }

    private fun fetchReminders() {
        viewModelScope.launch {
            try {
                val reminders = getAllRemindersUseCase.invoke() // wywołanie zawieszalnej funkcji
                _uiState.value = UiState(reminders)
            } catch (e: Exception) {
                _uiState.value = UiState(emptyList())
            }
        }
    }

    fun insert(reminder: Reminder) = viewModelScope.launch {
        insertUseCase.invoke(reminder)
    }

    fun update(reminder: Reminder) = viewModelScope.launch {
        updateUseCase.invoke(reminder)
    }

    fun delete(reminder: Reminder) = viewModelScope.launch {
        deleteUseCase.invoke(reminder)
    }
}

data class UiState(
    val data: List<Reminder> = emptyList()
)
