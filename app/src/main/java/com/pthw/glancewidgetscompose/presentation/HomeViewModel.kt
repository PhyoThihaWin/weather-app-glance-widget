package com.pthw.glancewidgetscompose.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pthw.glancewidgetscompose.data.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by P.T.H.W on 02/09/2024.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    val weatherCurrent = mutableStateOf<WeatherCurrentVo?>(null)

    init {
        getWeatherCurrent("Bangkok")
    }

    fun getWeatherCurrent(query: String) {
        viewModelScope.launch {
            weatherCurrent.value = weatherRepository.getWeatherCurrent(query)
        }
    }

}