package com.example.scmu_app.ui.theme


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.text.Layout
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

val version = "1.1"

@Composable
fun createTile(text: String) {
    Row(
        modifier = Modifier
            .background(
                color = darkGreen,
                shape = RoundedCornerShape(0.dp, 15.dp, 15.dp, 0.dp)
            )
            .width(310.dp)
            .zIndex(100f)
    ) {
        Text(
            text = text,
            style = titleLarge,
            color = androidx.compose.ui.graphics.Color.White,
            modifier = Modifier.padding(vertical = 5.dp, horizontal = 20.dp)
        )
    }
}

@Composable
fun hasWIFI(): Boolean {
    val context = LocalContext.current
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val isConnected = remember {
        val networkInfo = connectivityManager.activeNetworkInfo
        networkInfo != null && networkInfo.isConnected
    }

    return isConnected;
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreateDefaultScaffold(showLoading: Boolean,  system: @Composable () -> Unit) {
    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(darkGreen),
            )
        }
    ) {
        BoxWithConstraints() {
            loadingScreen(showLoading)
            if (hasWIFI())
                system()
            else createWifiConnectionPanel()
        }
    }
}

@Composable
fun createWifiConnectionPanel() {
    Box(
        modifier = Modifier
            .background(mintGreen)
            .fillMaxSize()
    ) {
        Icon(
            Icons.Filled.WifiOff,
            contentDescription = "Ad",
            tint = darkGreen,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-50).dp)
                .size(90.dp)
                .size(40.dp)
        )
        Text("No wifi connection",
            color = darkGreen,
            modifier = Modifier
                .offset(y = 20.dp)
                .align(Alignment.Center))
    }
}

@Composable
fun loadingScreen(show: Boolean) {
    if (!show) return

    Column(
        Modifier.zIndex(2000f)
            .fillMaxSize()
            .background(lockColor)
            .clickable(false) {},
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        CircularProgressIndicator(
            modifier = Modifier.width(70.dp),
            strokeWidth = 8.dp,
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}