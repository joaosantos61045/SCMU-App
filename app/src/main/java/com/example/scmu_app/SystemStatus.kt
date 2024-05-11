package com.example.scmu_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scmu_app.ui.theme.SCMUAppTheme


class SystemStatus : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SCMUAppTheme {
                Scaffold {


                    SystemStatusContent()

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemStatusContent() {
    data class HistoryEntry(val day: String, val time: String, val duration: String)

    val history = listOf(
        HistoryEntry("Mon", "9:00 AM", "30 minutes"),
        HistoryEntry("Tue", "10:30 AM", "45 minutes"),
        HistoryEntry("Wed", "2:15 PM", "1 hour"),
        HistoryEntry("Thu", "4:00 PM", "20 minutes"),
        // Add more entries as needed
    )
    val context = LocalContext.current
    val mintGreen = Color(0xffbff4d2)
    val darkGreen = Color(0xFF306044)
    val bgGreen = Color(0xFF8CBF9F)
    val swampGreen = Color(0xFF5D8E70)
    val showDialog = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .background(mintGreen)

    ) {
        TopAppBar(

            title = { Text("My House") },
            navigationIcon = {
                IconButton(onClick = {
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Go back")
                }
            },
        )




        Subheader("Information", darkGreen)


        Subheader("History", darkGreen)
       LazyColumn {

            items(history) { history ->
               // HistoryItem(history.day, history.time, history.duration)
                println(history)
            }
        }




    }
}

@Composable
fun HistoryItem(day: String, time: String, duration: String) {
    val context = LocalContext.current
    Surface(
        color = Color(0xFF98FB98),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                // Navigate to detail screen when clicked
                val intent = Intent(context, MainActivity2::class.java)

                context.startActivity(intent)
            }
    ) {
        Text(
            text = day,
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = time,
            modifier = Modifier.padding(16.dp)
        )
        Text(
                text = duration,
        modifier = Modifier.padding(16.dp)
        )

    }
}

@Preview(showBackground = true)
@Composable
fun ManageSystemPreview() {
    SCMUAppTheme {
        SystemStatusContent()
    }
}
