package com.nyinnovations.androidcleanarchitecturesample.util

object AppConstants {

    // ---- Network ----
    const val WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/"
    const val GEO_BASE_URL = "https://api.openweathermap.org/geo/1.0/"
    const val UNITS_METRIC = "metric"
    const val GEOCODING_LIMIT = 5
    const val CONNECT_TIMEOUT_SECONDS = 15L
    const val READ_TIMEOUT_SECONDS = 15L

    // ---- Database ----
    const val DB_NAME = "weather_db"
    const val DB_VERSION_CURRENT = 3

    // ---- Location ----
    const val LOCATION_TIMEOUT_MS = 8_000L
    const val FALLBACK_CITY = "London"

    // ---- City limits ----
    const val MAX_MANUAL_CITIES = 5

    // ---- Search UI ----
    const val SEARCH_DEBOUNCE_MS = 300L
    const val SEARCH_MIN_CHARS = 2
    const val CURRENT_LOCATION_PREFIX = "📍"
    const val CURRENT_LOCATION_SUFFIX = "(current)"

    // ---- Error messages ----
    const val ERROR_MAX_CITIES = "Maximum 5 cities allowed"
    const val ERROR_WEATHER_LOAD = "Couldn't load weather"
    const val ERROR_FORECAST_LOAD = "Couldn't load forecast"

    // ---- Weather condition keywords ----
    const val CONDITION_THUNDERSTORM = "thunderstorm"
    const val CONDITION_DRIZZLE = "drizzle"
    const val CONDITION_LIGHT_RAIN = "light rain"
    const val CONDITION_RAIN = "rain"
    const val CONDITION_SNOW = "snow"
    const val CONDITION_MIST = "mist"
    const val CONDITION_FOG = "fog"
    const val CONDITION_HAZE = "haze"
    const val CONDITION_CLEAR = "clear"
    const val CONDITION_CLOUD = "cloud"

    // ---- Temperature thresholds ----
    const val TEMP_HOT = 30.0
    const val TEMP_MILD = 20.0
    const val TEMP_FREEZING = 0.0

    // ---- Weather mood emojis ----
    const val EMOJI_THUNDERSTORM = "⛈️"
    const val EMOJI_DRIZZLE = "🌦️"
    const val EMOJI_RAIN = "🌧️"
    const val EMOJI_SNOW = "❄️"
    const val EMOJI_MIST = "🌫️"
    const val EMOJI_HOT = "🥵"
    const val EMOJI_SUNNY = "☀️"
    const val EMOJI_PARTLY_CLOUDY = "⛅"
    const val EMOJI_CLOUDY = "☁️"
    const val EMOJI_FREEZING = "🥶"
    const val EMOJI_DEFAULT = "🌤️"

    // ---- Weather mood suggestions ----
    const val SUGGESTION_THUNDERSTORM = "Stay cozy indoors, maybe watch a movie"
    const val SUGGESTION_DRIZZLE = "Grab a light jacket, good for a café visit"
    const val SUGGESTION_RAIN = "Don't forget your umbrella! Great for reading"
    const val SUGGESTION_SNOW = "Bundle up! Perfect for hot chocolate"
    const val SUGGESTION_MIST = "Drive carefully, visibility might be low"
    const val SUGGESTION_HOT = "Stay hydrated! Maybe hit the pool"
    const val SUGGESTION_SUNNY = "Perfect day for a walk or outdoor workout"
    const val SUGGESTION_PARTLY_CLOUDY = "Nice and mild, great for errands or cycling"
    const val SUGGESTION_CLOUDY = "A bit grey, perfect for a cozy coffee break"
    const val SUGGESTION_FREEZING = "Freezing out there! Layer up before heading out"
    const val SUGGESTION_DEFAULT = "Looks decent outside, enjoy your day!"
}

