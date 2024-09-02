package com.pthw.glancewidgetscompose.glance

import com.pthw.glancewidgetscompose.presentation.WeatherCurrentVo
import kotlinx.serialization.Serializable

/**
 * Created by P.T.H.W on 02/09/2024.
 */
@Serializable
sealed class WeatherCurrentWidgetState {
    @Serializable
    data object Loading : WeatherCurrentWidgetState()

    @Serializable
    data class Available(
        val currentData: WeatherCurrentVo
    ) : WeatherCurrentWidgetState()

    @Serializable
    data class Unavailable(val message: String) : WeatherCurrentWidgetState()
}
