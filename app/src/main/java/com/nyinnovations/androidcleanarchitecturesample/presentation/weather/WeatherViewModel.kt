package com.nyinnovations.androidcleanarchitecturesample.presentation.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyinnovations.androidcleanarchitecturesample.data.location.LocationRepository
import com.nyinnovations.androidcleanarchitecturesample.domain.model.Weather
import com.nyinnovations.androidcleanarchitecturesample.domain.repository.WeatherRepository
import com.nyinnovations.androidcleanarchitecturesample.domain.util.Result
import com.nyinnovations.androidcleanarchitecturesample.util.AppConstants
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
    private val repository: WeatherRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WeatherUiState())
    val state: StateFlow<WeatherUiState> = _state.asStateFlow()

    private val _searchSuggestions = MutableStateFlow<List<String>>(emptyList())
    val searchSuggestions: StateFlow<List<String>> = _searchSuggestions.asStateFlow()

    // tracks the current GPS-detected city name so search can show it first
    private val _currentAutoCity = MutableStateFlow<String?>(null)
    val currentAutoCity: StateFlow<String?> = _currentAutoCity.asStateFlow()

    // prevent duplicate location updates within the same session
    private var locationUpdateDone = false

    private var searchJob: Job? = null

    init {
        observeCities()
    }

    private fun observeCities() {
        viewModelScope.launch {
            repository.getSavedCities().collect { cities ->
                _state.value = _state.value.copy(cities = cities)
                cities.forEach { loadWeatherForCity(it) }
            }
        }
    }

    // UI calls this with permission result — happens on every launch
    fun onLocationPermissionResult(granted: Boolean) {
        if (locationUpdateDone) return
        locationUpdateDone = true

        viewModelScope.launch {
            _state.value = _state.value.copy(isLocating = true)
            if (granted) {
                val city = locationRepository.detectCurrentCity() ?: AppConstants.FALLBACK_CITY
                _currentAutoCity.value = city
                repository.updateAutoCity(city)
            } else {
                repository.updateAutoCity(AppConstants.FALLBACK_CITY)
            }
            _state.value = _state.value.copy(isLocating = false)
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
                    is Result.Error -> _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun addCity(city: String) {
        viewModelScope.launch {
            val added = repository.addCity(city)
            if (!added) {
                _state.value = _state.value.copy(error = AppConstants.ERROR_MAX_CITIES)
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
        if (query.length < AppConstants.SEARCH_MIN_CHARS) {
            val autoCity = _currentAutoCity.value
            _searchSuggestions.value = if (autoCity != null)
                listOf("${AppConstants.CURRENT_LOCATION_PREFIX} $autoCity ${AppConstants.CURRENT_LOCATION_SUFFIX}")
            else emptyList()
            return
        }
        searchJob = viewModelScope.launch {
            delay(AppConstants.SEARCH_DEBOUNCE_MS)
            val results = repository.searchCities(query).toMutableList()
            val autoCity = _currentAutoCity.value
            if (autoCity != null && autoCity.contains(query, ignoreCase = true)) {
                results.removeAll { it.startsWith(autoCity, ignoreCase = true) }
                results.add(0, "${AppConstants.CURRENT_LOCATION_PREFIX} $autoCity ${AppConstants.CURRENT_LOCATION_SUFFIX}")
            }
            _searchSuggestions.value = results
        }
    }

    fun clearSuggestions() {
        _searchSuggestions.value = emptyList()
    }
}
