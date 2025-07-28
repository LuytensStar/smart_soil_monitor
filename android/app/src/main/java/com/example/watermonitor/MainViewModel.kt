package com.example.watermonitor


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainViewModel(
    private val repo: Repository
) : ViewModel() {


    private val _state = MutableStateFlow(SensorState(0, 0, 0))
    val state: StateFlow<SensorState> = _state

    init {

        viewModelScope.launch {
            while (isActive) {
                runCatching { repo.latest() }
                    .onSuccess { _state.value = it }
                delay(2_000)
            }
        }
    }
}
