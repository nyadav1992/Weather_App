package com.nyinnovations.androidcleanarchitecturesample.data.di

import android.content.Context
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.nyinnovations.androidcleanarchitecturesample.BuildConfig
import com.nyinnovations.androidcleanarchitecturesample.data.local.WeatherDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nyinnovations.androidcleanarchitecturesample.data.local.dao.SavedCityDao
import com.nyinnovations.androidcleanarchitecturesample.data.local.dao.WeatherDao
import com.nyinnovations.androidcleanarchitecturesample.data.location.LocationRepository
import com.nyinnovations.androidcleanarchitecturesample.data.remote.GeocodingApi
import com.nyinnovations.androidcleanarchitecturesample.data.remote.WeatherApi
import com.nyinnovations.androidcleanarchitecturesample.data.repository.WeatherRepositoryImpl
import com.nyinnovations.androidcleanarchitecturesample.domain.repository.WeatherRepository
import com.nyinnovations.androidcleanarchitecturesample.util.AppConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(AppConstants.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(AppConstants.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherApi(client: OkHttpClient): WeatherApi {
        return Retrofit.Builder()
            .baseUrl(AppConstants.WEATHER_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WeatherDatabase {
        // migration v2→v3: added isAutoCity column to saved_cities
        val migration2to3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE saved_cities ADD COLUMN isAutoCity INTEGER NOT NULL DEFAULT 0"
                )
            }
        }
        return Room.databaseBuilder(context, WeatherDatabase::class.java, AppConstants.DB_NAME)
            .addMigrations(migration2to3)
            .fallbackToDestructiveMigration() // safety net for anything older than v2
            .build()
    }

    @Provides
    @Singleton
    fun provideGeocodingApi(client: OkHttpClient): GeocodingApi {
        return Retrofit.Builder()
            .baseUrl(AppConstants.GEO_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeocodingApi::class.java)
    }

    @Provides
    fun provideWeatherDao(db: WeatherDatabase): WeatherDao = db.weatherDao()

    @Provides
    fun provideSavedCityDao(db: WeatherDatabase): SavedCityDao = db.savedCityDao()

    @Provides
    @Singleton
    fun provideFusedLocationClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(
        @ApplicationContext context: Context,
        locationClient: FusedLocationProviderClient,
        geocodingApi: GeocodingApi
    ): LocationRepository {
        return LocationRepository(context, locationClient, geocodingApi, apiKey = BuildConfig.WEATHER_API_KEY)
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(
        api: WeatherApi,
        geocodingApi: GeocodingApi,
        weatherDao: WeatherDao,
        cityDao: SavedCityDao
    ): WeatherRepository {
        return WeatherRepositoryImpl(api, geocodingApi, weatherDao, cityDao, apiKey = BuildConfig.WEATHER_API_KEY)
    }
}
