package com.pthw.glancewidgetscompose.data

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.pthw.glancewidgetscompose.BuildConfig
import com.pthw.glancewidgetscompose.data.ktor.AuthTokenInterceptor
import com.pthw.glancewidgetscompose.data.ktor.ktorHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

/**
 * Created by P.T.H.W on 02/09/2024.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppDiModule {
    @Provides
    @Singleton
    fun provideChuckerInterceptor(@ApplicationContext context: Context): ChuckerInterceptor {
        // Create the Collector
        val chuckerCollector = ChuckerCollector(
            context = context,
            showNotification = true,
            retentionPeriod = RetentionManager.Period.ONE_HOUR
        )
        // Create the Interceptor
        return ChuckerInterceptor.Builder(context)
            .collector(chuckerCollector)
            .maxContentLength(250_000L)
            .redactHeaders("Auth-Token", "Bearer")
            .alwaysReadResponseBody(true)
            .createShortcut(BuildConfig.DEBUG)
            .build()
    }

    @Provides
    @Singleton
    fun provideKtorClient(
        authTokenInterceptor: AuthTokenInterceptor,
        chuckerInterceptor: ChuckerInterceptor,
    ): HttpClient {
        val interceptors =
            listOfNotNull(authTokenInterceptor, chuckerInterceptor.takeIf { BuildConfig.DEBUG })
        return ktorHttpClient(interceptors)
    }

    @Provides
    @Singleton
    fun provideMovieService(ktor: HttpClient) = WeatherApiService(ktor)

    @Provides
    @Singleton
    fun provideWeatherRepository(apiService: WeatherApiService) = WeatherRepository(apiService)
}

//@Module
//@InstallIn(ViewModelComponent::class)
//object MainServiceModule {
//    @Provides
//    fun provideMovieService(ktor: HttpClient) = WeatherApiService(ktor)
//
//    @Provides
//    fun provideWeatherRepository(apiService: WeatherApiService) = WeatherRepository(apiService)
//}