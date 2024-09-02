package com.pthw.glancewidgetscompose

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by P.T.H.W on 02/09/2024.
 */
@HiltAndroidApp
class WeatherApp : Application(), Configuration.Provider {
    override fun onCreate() {
        super.onCreate()
        setupTimber()
    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}

private fun setupTimber() {
    if (!BuildConfig.DEBUG) return
    Timber.plant(object : Timber.DebugTree() {
        override fun createStackElementTag(element: StackTraceElement): String {
            val prefix = super.createStackElementTag(element)?.substringBefore("$") ?: "Timber"
            return String.format("C:%s, L:%s", prefix, element.lineNumber, element.methodName)
        }
    })
}