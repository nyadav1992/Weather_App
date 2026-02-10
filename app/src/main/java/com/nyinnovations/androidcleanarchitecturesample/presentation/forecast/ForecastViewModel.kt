package com.nyinnovations.androidcleanarchitecturesample.presentation.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyinnovations.androidcleanarchitecturesample.domain.repository.WeatherRepository
import com.nyinnovations.androidcleanarchitecturesample.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ForecastUiState())
    val state: StateFlow<ForecastUiState> = _state.asStateFlow()

    fun loadForecast(city: String) {
        viewModelScope.launch {
            repository.getForecast(city).collect { result ->
                _state.value = when (result) {
                    is Result.Loading -> _state.value.copy(isLoading = true, error = null)
                    is Result.Success -> _state.value.copy(forecast = result.data, isLoading = false, error = null)
                    is Result.Error -> _state.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }
}
