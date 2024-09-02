package com.pthw.glancewidgetscompose.glance

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.pthw.glancewidgetscompose.data.WeatherRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Duration
import kotlin.random.Random

/**
 * Created by P.T.H.W on 02/09/2024.
 */
@HiltWorker
class WeatherWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: WeatherRepository
) : CoroutineWorker(context, workerParameters) {

    companion object {
        private val uniqueWorkName = WeatherWorker::class.java.simpleName

        fun enqueue(context: Context, force: Boolean = false) {
            val manager = WorkManager.getInstance(context)
            val requestBuilder = PeriodicWorkRequestBuilder<WeatherWorker>(
                Duration.ofMinutes(30)
            )
            var workPolicy = ExistingPeriodicWorkPolicy.KEEP

            // Replace any enqueued work and expedite the request
            if (force) {
                workPolicy = ExistingPeriodicWorkPolicy.REPLACE
            }

            manager.enqueueUniquePeriodicWork(
                uniqueWorkName,
                workPolicy,
                requestBuilder.build()
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName)
        }
    }

    override suspend fun doWork(): Result {
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(WeatherGlanceWidget::class.java)
        return try {
            // Update state to indicate loading
            setWidgetState(glanceIds, WeatherCurrentWidgetState.Loading)
            // Update state with new data
            val list = listOf("Myanmar", "Bangkok", "Singapore")
            val newState = repository.getWeatherCurrentForWorker(list[Random.nextInt(3)])
            println("NewState: ${newState.currentData}")
            setWidgetState(glanceIds, newState)

            Result.success()
        } catch (e: Exception) {
            setWidgetState(glanceIds, WeatherCurrentWidgetState.Unavailable(e.message.orEmpty()))
            if (runAttemptCount < 10) {
                // Exponential backoff strategy will avoid the request to repeat
                // too fast in case of failures.
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    private suspend fun setWidgetState(glanceIds: List<GlanceId>, newState: WeatherCurrentWidgetState) {
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(
                context = context,
                definition = WeatherCurrentStateDefinition,
                glanceId = glanceId,
                updateState = { newState }
            )
        }
        WeatherGlanceWidget().updateAll(context)
    }
}