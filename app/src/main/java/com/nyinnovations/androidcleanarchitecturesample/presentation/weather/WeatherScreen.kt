package com.nyinnovations.androidcleanarchitecturesample.presentation.weather

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nyinnovations.androidcleanarchitecturesample.R
import com.nyinnovations.androidcleanarchitecturesample.domain.model.Weather
import com.nyinnovations.androidcleanarchitecturesample.domain.model.WeatherMoodResolver
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    onCityClick: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showSearchDialog by remember { mutableStateOf(false) }

    // ask for location permission on first launch to auto-detect city
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onLocationPermissionResult(granted)
    }

    LaunchedEffect(Unit) {
        val already = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (!already) {
            permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.weather_app_icon),
                            contentDescription = "App logo",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text("My Cities", style = MaterialTheme.typography.headlineLarge)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshAll() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            if (state.cities.size < 5) {
                FloatingActionButton(
                    onClick = { showSearchDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add city")
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading && state.cityWeathers.isEmpty()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(state.cities, key = { it }) { city ->
                        val weather = state.cityWeathers[city]
                        CityWeatherCard(
                            cityName = city,
                            weather = weather,
                            onClick = { onCityClick(city) },
                            onRemove = { viewModel.removeCity(city) }
                        )
                    }
                }
            }
        }
    }

    // error snackbar
    state.error?.let { error ->
        LaunchedEffect(error) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearError()
        }
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = { TextButton(onClick = { viewModel.clearError() }) { Text("OK") } }
        ) { Text(error) }
    }

    if (showSearchDialog) {
        SearchCityDialog(
            viewModel = viewModel,
            onDismiss = { showSearchDialog = false },
            onSearch = { city ->
                viewModel.addCity(city)
                showSearchDialog = false
            }
        )
    }
}

@Composable
private fun CityWeatherCard(
    cityName: String,
    weather: Weather?,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    val mood = remember(weather?.description, weather?.temperature) {
        weather?.let { WeatherMoodResolver.resolve(it.description, it.temperature) }
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            mood?.gradientStart ?: MaterialTheme.colorScheme.surfaceVariant,
                            mood?.gradientEnd ?: MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(20.dp)
        ) {
            // remove button
            IconButton(
                onClick = onRemove,
                modifier = Modifier.align(Alignment.TopEnd).size(28.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
            }

            Row(
                Modifier.fillMaxWidth().padding(end = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // emoji + city info
                Column(Modifier.weight(1f)) {
                    Text(mood?.emoji ?: "🌡️", fontSize = 36.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(cityName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    weather?.let {
                        Text(
                            it.description.replaceFirstChar { c -> c.uppercase() },
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                // temperature
                weather?.let {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "${it.temperature.roundToInt()}°",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "${it.tempMin.roundToInt()}° / ${it.tempMax.roundToInt()}°",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                } ?: CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            }
        }
    }
}

@Composable
private fun SearchCityDialog(
    viewModel: WeatherViewModel,
    onDismiss: () -> Unit,
    onSearch: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val suggestions by viewModel.searchSuggestions.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        onDispose { viewModel.clearSuggestions() }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add City") },
        text = {
            Column {
                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        viewModel.onSearchQueryChanged(it)
                    },
                    label = { Text("Search city...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // suggestions dropdown
                if (suggestions.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column {
                            suggestions.forEach { suggestion ->
                                val cityOnly = suggestion.substringBefore(",").trim()
                                Text(
                                    text = suggestion,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            query = cityOnly
                                            viewModel.clearSuggestions()
                                        }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                if (suggestion != suggestions.last()) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.surface,
                                        thickness = 0.5.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (query.isNotBlank()) onSearch(query.trim()) },
                enabled = query.isNotBlank()
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
