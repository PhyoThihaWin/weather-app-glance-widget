package com.pthw.glancewidgetscompose.presentation

import com.pthw.glancewidgetscompose.data.WeatherCurrentResponse
import kotlinx.serialization.Serializable

/**
 * Created by P.T.H.W on 02/09/2024.
 */
@Serializable
data class WeatherCurrentVo(
    val name: String,
    val country: String,
    val tempC: Double,
    val tempF: Double,
    val conditionText: String,
    val conditionIcon: String
) {
    companion object {
        val fake = WeatherCurrentVo(
            name = "Springfield",
            country = "USA",
            tempC = 22.5,
            tempF = 72.5,
            conditionText = "Partly cloudy",
            conditionIcon = "//cdn.weatherapi.com/weather/64x64/day/116.png",
        )

    }

}
