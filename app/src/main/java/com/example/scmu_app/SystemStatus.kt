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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
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


    val history = listOf(
        listOf(" Mon", "9:00 AM", "30 minutes"),
        listOf("Tue", "10:30 AM", "45 minutes"),
        listOf("Wed", "2:15 PM", "1 hour"),
        listOf("Thu", "4:00 PM", "20 minutes"),
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
                    val intent = Intent(context, MainScreen::class.java)
                    context.startActivity(intent)
                }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Go back")
                }
            },
        )




        Subheader("Information", darkGreen)
        IconButton(
            onClick = { val intent = Intent(context, EditSystem::class.java)

                context.startActivity(intent)},
            modifier = Modifier.size(30.dp)

        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Close",
                modifier = Modifier.background(darkGreen)


            )
        }
        StatusItem(status = "Waiting", event ="1 hour" )

        Subheader("History", darkGreen)

           for (item in history) {
               HistoryItem(day = item[0], time = item[1], duration = item[2])

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

    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Day: $day",
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Time: $time",
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Duration: $duration",
            )
        }
    }
}

@Composable
fun StatusItem(status: String, event: String) {
    val darkGreen = Color(0xFF306044)
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }
    Surface(
        color = Color(0xFF98FB98),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)

    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Status: $status",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Next event in: $event",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            IconButton(
                onClick = { showDialog.value = true },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier.background(darkGreen)


                )
            }
        }
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text("Cancel Event") },
                text = {
                    Column {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .fillMaxWidth(),
                            text = "Are you sure you want to cancel the current event?"
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                    }
                },
                dismissButton = {
                    Button(

                        onClick = { showDialog.value = false }) {
                        Text("Back")
                    }
                },
                confirmButton = {
                    Button(colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ),
                        onClick = {

                            showDialog.value = false
                        }) {

                        Text("Cancel")
                    }
                },

                )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ManageSystemPreview() {
    SCMUAppTheme {
        SystemStatusContent()
    }
}
