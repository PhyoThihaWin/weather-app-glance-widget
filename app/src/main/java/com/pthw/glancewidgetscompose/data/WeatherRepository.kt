package com.pthw.glancewidgetscompose.data

import com.pthw.glancewidgetscompose.glance.WeatherCurrentWidgetState
import com.pthw.glancewidgetscompose.presentation.WeatherCurrentVo
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by P.T.H.W on 02/09/2024.
 */
class WeatherRepository @Inject constructor(
    private val weatherApiService: WeatherApiService
) {
    suspend fun getWeatherCurrent(query: String): WeatherCurrentVo {
        val data = weatherApiService.getCurrentWeather(query)
        return data.toWeatherCurrentVo()
    }

    suspend fun getWeatherCurrentForWorker(query: String): WeatherCurrentWidgetState.Available {
        val data = weatherApiService.getCurrentWeather(query)
        Timber.i("Reached: $data")
        return WeatherCurrentWidgetState.Available(
            data.toWeatherCurrentVo()
        )
    }
}