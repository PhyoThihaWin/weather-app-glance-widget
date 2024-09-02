package com.pthw.glancewidgetscompose.glance

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.glance.state.GlanceStateDefinition
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.io.OutputStream

/**
 * Created by P.T.H.W on 02/09/2024.
 */
object WeatherCurrentStateDefinition : GlanceStateDefinition<WeatherCurrentWidgetState> {
    override suspend fun getDataStore(
        context: Context,
        fileKey: String
    ): DataStore<WeatherCurrentWidgetState> {
        return context.datastore
    }

    override fun getLocation(context: Context, fileKey: String): File {
        return context.dataStoreFile(DATA_STORE_FILENAME)
    }

    private val DATA_STORE_FILENAME = "weatherInfo"
    private val Context.datastore by dataStore(DATA_STORE_FILENAME, WeatherCurrentStateSerializer)

    object WeatherCurrentStateSerializer : Serializer<WeatherCurrentWidgetState> {
        override val defaultValue: WeatherCurrentWidgetState
            get() = WeatherCurrentWidgetState.Unavailable("No data yet!")

        override suspend fun readFrom(input: InputStream): WeatherCurrentWidgetState {
            return try {
                Json.decodeFromString<WeatherCurrentWidgetState>(input.readBytes().decodeToString())
            } catch (e: Exception) {
                WeatherCurrentWidgetState.Unavailable("Something went wrong!")
            }
        }

        override suspend fun writeTo(t: WeatherCurrentWidgetState, output: OutputStream) {
            output.use {
                it.write(Json.encodeToString(t).encodeToByteArray())
            }
        }

    }
}