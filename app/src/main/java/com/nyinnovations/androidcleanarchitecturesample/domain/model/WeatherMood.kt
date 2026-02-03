package com.nyinnovations.androidcleanarchitecturesample.domain.model

import androidx.compose.ui.graphics.Color

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
            "thunderstorm" in descLower -> WeatherMood(
                emoji = "⛈️",
                suggestion = "Stay cozy indoors, maybe watch a movie",
                gradientStart = Color(0xFF1a1a2e),
                gradientEnd = Color(0xFF16213e)
            )
            "drizzle" in descLower || "light rain" in descLower -> WeatherMood(
                emoji = "🌦️",
                suggestion = "Grab a light jacket, good for a café visit",
                gradientStart = Color(0xFF4a6fa5),
                gradientEnd = Color(0xFF7db8c9)
            )
            "rain" in descLower -> WeatherMood(
                emoji = "🌧️",
                suggestion = "Don't forget your umbrella! Great for reading",
                gradientStart = Color(0xFF3d5a80),
                gradientEnd = Color(0xFF5c8a9e)
            )
            "snow" in descLower -> WeatherMood(
                emoji = "❄️",
                suggestion = "Bundle up! Perfect for hot chocolate",
                gradientStart = Color(0xFFcce5ff),
                gradientEnd = Color(0xFFe8f4fd)
            )
            "mist" in descLower || "fog" in descLower || "haze" in descLower -> WeatherMood(
                emoji = "🌫️",
                suggestion = "Drive carefully, visibility might be low",
                gradientStart = Color(0xFF8e9eab),
                gradientEnd = Color(0xFFeef2f3)
            )
            "clear" in descLower && temp > 30 -> WeatherMood(
                emoji = "🥵",
                suggestion = "Stay hydrated! Maybe hit the pool",
                gradientStart = Color(0xFFf7971e),
                gradientEnd = Color(0xFFffd200)
            )
            "clear" in descLower -> WeatherMood(
                emoji = "☀️",
                suggestion = "Perfect day for a walk or outdoor workout",
                gradientStart = Color(0xFF56CCF2),
                gradientEnd = Color(0xFF2F80ED)
            )
            "cloud" in descLower && temp > 20 -> WeatherMood(
                emoji = "⛅",
                suggestion = "Nice and mild, great for errands or cycling",
                gradientStart = Color(0xFF89b4c4),
                gradientEnd = Color(0xFFb8d4e3)
            )
            "cloud" in descLower -> WeatherMood(
                emoji = "☁️",
                suggestion = "A bit grey, perfect for a cozy coffee break",
                gradientStart = Color(0xFF757f9a),
                gradientEnd = Color(0xFFd7dde8)
            )
            temp < 0 -> WeatherMood(
                emoji = "🥶",
                suggestion = "Freezing out there! Layer up before heading out",
                gradientStart = Color(0xFF2193b0),
                gradientEnd = Color(0xFF6dd5ed)
            )
            else -> WeatherMood(
                emoji = "🌤️",
                suggestion = "Looks decent outside, enjoy your day!",
                gradientStart = Color(0xFF2196F3),
                gradientEnd = Color(0xFF64B5F6)
            )
        }
    }
}

