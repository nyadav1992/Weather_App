package com.nyinnovations.androidcleanarchitecturesample.presentation.forecast

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nyinnovations.androidcleanarchitecturesample.domain.model.ForecastItem
import com.nyinnovations.androidcleanarchitecturesample.domain.model.WeatherMoodResolver
import com.nyinnovations.androidcleanarchitecturesample.ui.theme.AccentCyan
import com.nyinnovations.androidcleanarchitecturesample.ui.theme.CardSurface
import com.nyinnovations.androidcleanarchitecturesample.ui.theme.CardSurfaceLight
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastScreen(
    cityName: String,
    viewModel: ForecastViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(cityName) { viewModel.loadForecast(cityName) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(cityName, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading && state.forecast == null -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                state.error != null && state.forecast == null -> {
                    Column(
                        Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(state.error!!, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadForecast(cityName) }) { Text("Retry") }
                    }
                }
                state.forecast != null -> {
                    val grouped = remember(state.forecast) {
                        state.forecast!!.forecasts.groupBy { it.date.substringBefore("•").trim() }
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        grouped.forEach { (day, items) ->
                            // day header
                            item(key = "header_$day") {
                                Text(
                                    text = day,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = AccentCyan,
                                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                                )
                            }
                            // forecast items for this day
                            items(items, key = { it.timestamp }) { item ->
                                PremiumForecastCard(item)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumForecastCard(item: ForecastItem) {
    val mood = remember(item.description, item.temperature) {
        WeatherMoodResolver.resolve(item.description, item.temperature)
    }
    val time = item.date.substringAfter("•").trim()

    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(CardSurface, CardSurfaceLight)
                )
            )
            .padding(16.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // time + emoji
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(60.dp)) {
                Text(mood.emoji, fontSize = 28.sp)
                Spacer(Modifier.height(4.dp))
                Text(time, fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f))
            }

            Spacer(Modifier.width(16.dp))

            // description
            Column(Modifier.weight(1f)) {
                Text(
                    item.description.replaceFirstChar { it.uppercase() },
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                // temp range bar
                TempRangeBar(
                    min = item.tempMin,
                    max = item.tempMax,
                    current = item.temperature
                )
            }

            Spacer(Modifier.width(12.dp))

            // temperature
            Text(
                "${item.temperature.roundToInt()}°",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun TempRangeBar(min: Double, max: Double, current: Double) {
    val range = (max - min).coerceAtLeast(1.0)
    val fraction = ((current - min) / range).toFloat().coerceIn(0f, 1f)

    Column {
        Row {
            Text("${min.roundToInt()}°", fontSize = 11.sp, color = Color.White.copy(alpha = 0.4f))
            Spacer(Modifier.weight(1f))
            Text("${max.roundToInt()}°", fontSize = 11.sp, color = Color.White.copy(alpha = 0.4f))
        }
        Spacer(Modifier.height(4.dp))
        Box(
            Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(Color.White.copy(alpha = 0.1f))
        ) {
            Box(
                Modifier.fillMaxHeight().fillMaxWidth(fraction).clip(RoundedCornerShape(3.dp)).background(
                    Brush.horizontalGradient(listOf(AccentCyan.copy(alpha = 0.6f), AccentCyan))
                )
            )
        }
    }
}
