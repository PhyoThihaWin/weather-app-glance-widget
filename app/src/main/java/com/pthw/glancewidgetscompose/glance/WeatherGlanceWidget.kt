package com.pthw.glancewidgetscompose.glance

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.pthw.glancewidgetscompose.R
import com.pthw.glancewidgetscompose.presentation.WeatherCurrentVo

/**
 * Created by P.T.H.W on 01/09/2024.
 */


class WeatherGlanceWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Single
    override val stateDefinition = WeatherCurrentStateDefinition
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                val weatherState = currentState<WeatherCurrentWidgetState>()
                MyContent(weatherState)
            }
        }
    }
}

@Composable
private fun MyContent(weatherCurrentWidgetState: WeatherCurrentWidgetState) {
    val size = LocalSize.current
    val context = LocalContext.current

    Row(
        modifier = GlanceModifier
            .background(ImageProvider(R.drawable.bg_round_background))
            .fillMaxSize()
            .padding(12.dp)
            .clickable {
                WeatherWorker.enqueue(context, true)
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (weatherCurrentWidgetState) {
            WeatherCurrentWidgetState.Loading -> {
                CircularProgressIndicator(modifier = GlanceModifier)
            }

            is WeatherCurrentWidgetState.Available -> {
                println("Reached: RefreshAction")
                val data = weatherCurrentWidgetState.currentData
                Row(modifier = GlanceModifier.fillMaxSize()) {
                    Column(
                        modifier = GlanceModifier.fillMaxHeight().width(size.width * 2f / 3),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = data.conditionText,
                            modifier = GlanceModifier.fillMaxWidth(),
                            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        )

                        Image(
                            modifier = GlanceModifier.padding(
                                end = 12.dp,
                                top = 8.dp,
                                bottom = 8.dp,
                                start = 16.dp
                            ),
                            provider = ImageProvider(R.drawable.ic_weather_sun),
                            contentDescription = null
                        )
                    }

                    Box(
                        modifier = GlanceModifier.fillMaxHeight().width(1.dp)
                            .background(Color.LightGray)
                    ) {}


                    Column(
                        modifier = GlanceModifier.fillMaxHeight().defaultWeight()
                            .background(Color.White)
                            .padding(start = 12.dp)
                    ) {
                        Text(
                            text = data.country,
                            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        )
                        Text(text = "Monday", style = TextStyle(fontSize = 11.sp))
                        Column(
                            modifier = GlanceModifier.fillMaxHeight(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                "${data.tempC}°",
                                style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                "${data.tempF} F",
                                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            is WeatherCurrentWidgetState.Unavailable -> {
                Text(weatherCurrentWidgetState.message, style = TextStyle(fontSize = 20.sp))
            }
        }

    }
}

private fun getImageProvider(path: String): ImageProvider {
    return if (path == "") { /*dummy image to return if path is blank*/
        ImageProvider(R.drawable.ic_launcher_background)
    } else {
        val bitmap = BitmapFactory.decodeFile(path)
        ImageProvider(bitmap)
    }
}

class RefreshAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {

//        updateAppWidgetState(context, glanceId) {
//            it[intPreferencesKey("number")] = Random.nextInt(1000)
//        }
    }
}


@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 350, heightDp = 150)
@Composable
private fun MyContentLoadingPreview() {
    MyContent(WeatherCurrentWidgetState.Loading)
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 350, heightDp = 150)
@Composable
private fun MyContentErrorPreview() {
    MyContent(WeatherCurrentWidgetState.Unavailable("Error"))
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 350, heightDp = 150)
@Composable
private fun MyContentAvailablePreview() {
    MyContent(
        WeatherCurrentWidgetState.Available(
            WeatherCurrentVo.fake
        )
    )
}

class WeatherGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WeatherGlanceWidget()
    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        context?.let {
            WeatherWorker.enqueue(context)
        }
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        context?.let {
            WeatherWorker.cancel(context)
        }
    }
}