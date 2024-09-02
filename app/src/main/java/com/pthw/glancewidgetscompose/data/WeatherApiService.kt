package com.pthw.glancewidgetscompose.data

import com.pthw.glancewidgetscompose.data.ktor.API_KEY
import com.pthw.glancewidgetscompose.data.ktor.ENDPOINT_GET_CURRENT
import com.pthw.glancewidgetscompose.data.ktor.PARAM_KEY
import com.pthw.glancewidgetscompose.data.ktor.PARAM_QUERY
import com.pthw.glancewidgetscompose.data.ktor.toKtor
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * Created by P.T.H.W on 24/04/2024.
 */
class WeatherApiService(private val client: HttpClient) {
    suspend fun getCurrentWeather(query: String): WeatherCurrentResponse {
        val endpoint = ENDPOINT_GET_CURRENT.toKtor()
        return client.get(endpoint) {
            parameter(PARAM_KEY, API_KEY)
            parameter(PARAM_QUERY, query)
        }.body()
    }
}