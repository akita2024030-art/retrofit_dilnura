package com.example.retrofit_9

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _usersState = MutableStateFlow<UiState<List<User>>>(UiState.Loading)
    val usersState: StateFlow<UiState<List<User>>> = _usersState.asStateFlow()

    private val _selectedUserState = MutableStateFlow<UiState<User>?>(null)
    val selectedUserState: StateFlow<UiState<User>?> = _selectedUserState.asStateFlow()

    var retryCount = 0
        private set

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _usersState.value = UiState.Loading
            val result = safeApiCall {
                ApiInstance.service.users(perPage = 5)
            }
            _usersState.value = result
            if (result is UiState.Success) {
                retryCount = 0
            }
        }
    }

    fun retry() {
        viewModelScope.launch {
            val result = safeApiCall {
                ApiInstance.service.users(perPage = 5)
            }
            _usersState.value = result
            if (result is UiState.Success) {
                retryCount = 0
            } else {
                retryCount++
                Log.d("MainViewModel", "Retry failed count: $retryCount")
            }
        }
    }

    fun loadUserDetail(login: String) {
        viewModelScope.launch {
            _selectedUserState.value = UiState.Loading
            _selectedUserState.value = safeApiCall {
                ApiInstance.service.user(login)
            }
        }
    }

    fun loadTest404() {
        viewModelScope.launch {
            _selectedUserState.value = UiState.Loading
            _selectedUserState.value = safeApiCall {
                ApiInstance.service.user("no-such-login-xyz")
            }
        }
    }

    fun clearSelectedUser() {
        _selectedUserState.value = null
    }
}
