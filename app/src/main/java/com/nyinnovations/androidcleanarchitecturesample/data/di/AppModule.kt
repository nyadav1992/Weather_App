package com.nyinnovations.androidcleanarchitecturesample.data.di

import android.content.Context
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.nyinnovations.androidcleanarchitecturesample.data.local.WeatherDatabase
import com.nyinnovations.androidcleanarchitecturesample.data.local.dao.SavedCityDao
import com.nyinnovations.androidcleanarchitecturesample.data.local.dao.WeatherDao
import com.nyinnovations.androidcleanarchitecturesample.data.location.LocationRepository
import com.nyinnovations.androidcleanarchitecturesample.data.remote.GeocodingApi
import com.nyinnovations.androidcleanarchitecturesample.data.remote.WeatherApi
import com.nyinnovations.androidcleanarchitecturesample.data.repository.WeatherRepositoryImpl
import com.nyinnovations.androidcleanarchitecturesample.domain.repository.WeatherRepository
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
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherApi(client: OkHttpClient): WeatherApi {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WeatherDatabase {
        return Room.databaseBuilder(context, WeatherDatabase::class.java, "weather_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideGeocodingApi(client: OkHttpClient): GeocodingApi {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/geo/1.0/")
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
        return LocationRepository(context, locationClient, geocodingApi, apiKey = "REMOVED_API_KEY")
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(
        api: WeatherApi,
        geocodingApi: GeocodingApi,
        weatherDao: WeatherDao,
        cityDao: SavedCityDao
    ): WeatherRepository {
        // TODO: replace with your own key from openweathermap.org
        return WeatherRepositoryImpl(api, geocodingApi, weatherDao, cityDao, apiKey = "REMOVED_API_KEY")
    }
}
