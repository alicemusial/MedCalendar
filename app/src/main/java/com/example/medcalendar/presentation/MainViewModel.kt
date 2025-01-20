package com.example.medcalendar.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medcalendar.data.repository.ReminderRepository
import com.example.medcalendar.domain.model.Reminder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val repository: ReminderRepository,
) : ViewModel() {


    private val _reminders = MutableLiveData<UiState<List<Reminder>>>()
    val reminder: LiveData<UiState<List<Reminder>>>
        get() = _reminders

    private val _addReminder = MutableLiveData<UiState<String>>()
    val addReminder: LiveData<UiState<String>>
        get() = _addReminder

    private val _updateReminder = MutableLiveData<UiState<String>>()
    val updateReminder: LiveData<UiState<String>>
        get() = _updateReminder

    private val _deleteReminder = MutableLiveData<UiState<String>>()
    val deleteReminder: LiveData<UiState<String>>
        get() = _deleteReminder

    fun getReminders() {
        _reminders.value = UiState.Loading
        Log.d("MainViewModel", "getReminders() called")
        repository.getAllReminders {
            Log.d("MainViewModel", "getReminders() result: $it")
            _reminders.value = it

        }
    }

    fun insert(reminder: Reminder) {
        _addReminder.value = UiState.Loading
        repository.insert(reminder) {
            _addReminder.value = it
            if (it is UiState.Success) {
                getReminders()
            }
        }
    }



    fun update(reminder: Reminder) {
        _updateReminder.value = UiState.Loading
        repository.update(reminder) {
            _updateReminder.value = it
            getReminders()
        }
        }

    fun delete(reminder: Reminder) {
        _deleteReminder.value = UiState.Loading
        repository.delete(reminder) {
            _deleteReminder.value = it
            getReminders()
        }

    }
}

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Failure(val error: String?) : UiState<Nothing>()
}
