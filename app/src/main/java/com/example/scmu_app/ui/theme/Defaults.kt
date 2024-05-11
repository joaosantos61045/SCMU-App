package com.example.scmu_app.ui.theme

import android.os.Build
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

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
            modifier = Modifier.padding(vertical = 5.dp, horizontal = 20.dp)
        )
    }
}