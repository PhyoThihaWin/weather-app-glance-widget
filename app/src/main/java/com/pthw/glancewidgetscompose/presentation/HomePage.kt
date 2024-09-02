package com.pthw.glancewidgetscompose.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pthw.glancewidgetscompose.ui.theme.GlanceWidgetsComposeTheme

/**
 * Created by P.T.H.W on 02/09/2024.
 */

@Composable
fun HomePage(
    viewModel: HomeViewModel = hiltViewModel()
) {
    PageContent(viewModel.weatherCurrent.value)
    LaunchedEffect(Unit) {
        viewModel.getWeatherCurrent("Bangkok")
    }
}

@Composable
fun PageContent(currentWeather: WeatherCurrentVo?) {
    Scaffold {
        LazyColumn() {  }
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            if (currentWeather == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Text(currentWeather.toString(), modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Preview
@Composable
private fun PageContentPreview() {
    GlanceWidgetsComposeTheme {
        PageContent(WeatherCurrentVo.fake)
    }
}