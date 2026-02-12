package com.nyinnovations.androidcleanarchitecturesample.presentation.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyinnovations.androidcleanarchitecturesample.domain.model.Weather
import com.nyinnovations.androidcleanarchitecturesample.domain.repository.WeatherRepository
import com.nyinnovations.androidcleanarchitecturesample.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WeatherUiState())
    val state: StateFlow<WeatherUiState> = _state.asStateFlow()

    private val _searchSuggestions = MutableStateFlow<List<String>>(emptyList())
    val searchSuggestions: StateFlow<List<String>> = _searchSuggestions.asStateFlow()

    private var searchJob: Job? = null

    init {
        observeCities()
    }

    private fun observeCities() {
        viewModelScope.launch {
            repository.getSavedCities().collect { cities ->
                if (cities.isEmpty()) {
                    // seed with London on first launch
                    repository.addCity("London")
                    return@collect
                }
                _state.value = _state.value.copy(cities = cities)
                cities.forEach { loadWeatherForCity(it) }
            }
        }
    }

    private fun loadWeatherForCity(city: String) {
        viewModelScope.launch {
            repository.getCurrentWeather(city).collect { result ->
                when (result) {
                    is Result.Success -> {
                        val updated = _state.value.cityWeathers.toMutableMap()
                        updated[city] = result.data
                        _state.value = _state.value.copy(cityWeathers = updated, isLoading = false)
                    }
                    is Result.Loading -> _state.value = _state.value.copy(isLoading = true)
                    is Result.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun addCity(city: String) {
        viewModelScope.launch {
            val added = repository.addCity(city)
            if (!added) {
                _state.value = _state.value.copy(error = "Maximum 5 cities allowed")
            }
        }
    }

    fun removeCity(city: String) {
        viewModelScope.launch {
            repository.removeCity(city)
            val updated = _state.value.cityWeathers.toMutableMap()
            updated.remove(city)
            _state.value = _state.value.copy(cityWeathers = updated)
        }
    }

    fun refreshAll() {
        _state.value.cities.forEach { loadWeatherForCity(it) }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun onSearchQueryChanged(query: String) {
        searchJob?.cancel()
        if (query.length < 2) {
            _searchSuggestions.value = emptyList()
            return
        }
        searchJob = viewModelScope.launch {
            delay(300) // debounce so we don't spam the API
            val results = repository.searchCities(query)
            _searchSuggestions.value = results
        }
    }

    fun clearSuggestions() {
        _searchSuggestions.value = emptyList()
    }
}
