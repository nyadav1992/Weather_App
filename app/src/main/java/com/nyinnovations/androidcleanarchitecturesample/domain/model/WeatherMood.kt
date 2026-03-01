package com.nyinnovations.androidcleanarchitecturesample.domain.model

import androidx.compose.ui.graphics.Color
import com.nyinnovations.androidcleanarchitecturesample.util.AppConstants

// maps weather conditions to a vibe — colors, suggestions, emoji
data class WeatherMood(
    val emoji: String,
    val suggestion: String,
    val gradientStart: Color,
    val gradientEnd: Color
)

object WeatherMoodResolver {

    fun resolve(description: String, temp: Double): WeatherMood {
        val descLower = description.lowercase()

        return when {
            AppConstants.CONDITION_THUNDERSTORM in descLower -> WeatherMood(
                emoji = AppConstants.EMOJI_THUNDERSTORM,
                suggestion = AppConstants.SUGGESTION_THUNDERSTORM,
                gradientStart = Color(0xFF1a1a2e),
                gradientEnd = Color(0xFF16213e)
            )
            AppConstants.CONDITION_DRIZZLE in descLower || AppConstants.CONDITION_LIGHT_RAIN in descLower -> WeatherMood(
                emoji = AppConstants.EMOJI_DRIZZLE,
                suggestion = AppConstants.SUGGESTION_DRIZZLE,
                gradientStart = Color(0xFF4a6fa5),
                gradientEnd = Color(0xFF7db8c9)
            )
            AppConstants.CONDITION_RAIN in descLower -> WeatherMood(
                emoji = AppConstants.EMOJI_RAIN,
                suggestion = AppConstants.SUGGESTION_RAIN,
                gradientStart = Color(0xFF3d5a80),
                gradientEnd = Color(0xFF5c8a9e)
            )
            AppConstants.CONDITION_SNOW in descLower -> WeatherMood(
                emoji = AppConstants.EMOJI_SNOW,
                suggestion = AppConstants.SUGGESTION_SNOW,
                gradientStart = Color(0xFFcce5ff),
                gradientEnd = Color(0xFFe8f4fd)
            )
            AppConstants.CONDITION_MIST in descLower
                    || AppConstants.CONDITION_FOG in descLower
                    || AppConstants.CONDITION_HAZE in descLower -> WeatherMood(
                emoji = AppConstants.EMOJI_MIST,
                suggestion = AppConstants.SUGGESTION_MIST,
                gradientStart = Color(0xFF8e9eab),
                gradientEnd = Color(0xFFeef2f3)
            )
            AppConstants.CONDITION_CLEAR in descLower && temp > AppConstants.TEMP_HOT -> WeatherMood(
                emoji = AppConstants.EMOJI_HOT,
                suggestion = AppConstants.SUGGESTION_HOT,
                gradientStart = Color(0xFFf7971e),
                gradientEnd = Color(0xFFffd200)
            )
            AppConstants.CONDITION_CLEAR in descLower -> WeatherMood(
                emoji = AppConstants.EMOJI_SUNNY,
                suggestion = AppConstants.SUGGESTION_SUNNY,
                gradientStart = Color(0xFF56CCF2),
                gradientEnd = Color(0xFF2F80ED)
            )
            AppConstants.CONDITION_CLOUD in descLower && temp > AppConstants.TEMP_MILD -> WeatherMood(
                emoji = AppConstants.EMOJI_PARTLY_CLOUDY,
                suggestion = AppConstants.SUGGESTION_PARTLY_CLOUDY,
                gradientStart = Color(0xFF89b4c4),
                gradientEnd = Color(0xFFb8d4e3)
            )
            AppConstants.CONDITION_CLOUD in descLower -> WeatherMood(
                emoji = AppConstants.EMOJI_CLOUDY,
                suggestion = AppConstants.SUGGESTION_CLOUDY,
                gradientStart = Color(0xFF757f9a),
                gradientEnd = Color(0xFFd7dde8)
            )
            temp < AppConstants.TEMP_FREEZING -> WeatherMood(
                emoji = AppConstants.EMOJI_FREEZING,
                suggestion = AppConstants.SUGGESTION_FREEZING,
                gradientStart = Color(0xFF2193b0),
                gradientEnd = Color(0xFF6dd5ed)
            )
            else -> WeatherMood(
                emoji = AppConstants.EMOJI_DEFAULT,
                suggestion = AppConstants.SUGGESTION_DEFAULT,
                gradientStart = Color(0xFF2196F3),
                gradientEnd = Color(0xFF64B5F6)
            )
        }
    }
}
