package com.pthw.glancewidgetscompose

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pthw.glancewidgetscompose.presentation.HomePage
import com.pthw.glancewidgetscompose.ui.theme.GlanceWidgetsComposeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GlanceWidgetsComposeTheme {
                HomePage()
            }
        }
    }
}

@Composable
fun AppWidgetsPage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val widgetManager = AppWidgetManager.getInstance(context)

    // Get a list of our app widget providers to retrieve their info
    val widgetProviders = widgetManager.getInstalledProvidersForPackage(context.packageName, null)
    Scaffold {
        LazyColumn(contentPadding = it) {
            item {
                Text(
                    "Glance Widgets", style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // If the launcher does not support pinning request show a banner
            if (!widgetManager.isRequestPinAppWidgetSupported) {
                item {
                    PinUnavailableBanner()
                }
            }

            items(widgetProviders) { providerInfo ->
                WidgetInfoCard(providerInfo)
            }
        }
    }
}

@Composable
private fun PinUnavailableBanner() {
    Text(
        text = "Pinning is not supported in the default launcher",
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.error)
            .padding(16.dp),
        color = MaterialTheme.colorScheme.onError
    )
}

@Composable
private fun WidgetInfoCard(providerInfo: AppWidgetProviderInfo) {
    val context = LocalContext.current
    val label = providerInfo.loadLabel(context.packageManager)
    val description = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        providerInfo.loadDescription(context).toString()
    } else {
        "Description not available"
    }
    val preview = painterResource(id = providerInfo.previewImage)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        onClick = {
            providerInfo.pin(context)
        }
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Image(painter = preview, contentDescription = description)
        }
    }
}

private fun AppWidgetProviderInfo.pin(context: Context) {
    val successCallback = PendingIntent.getBroadcast(
        context,
        0,
        Intent(context, AppWidgetPinnedReceiver::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    AppWidgetManager.getInstance(context).requestPinAppWidget(provider, null, successCallback)
}