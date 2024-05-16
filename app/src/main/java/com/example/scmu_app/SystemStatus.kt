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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.scmu_app.ui.theme.SCMUAppTheme
import com.example.scmu_app.ui.theme.createTile
import com.example.scmu_app.ui.theme.mintGreen
import com.example.scmu_app.ui.theme.swampGreen
import com.example.scmu_app.ui.theme.titleExtraLarge


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
        listOf("Wed", "2:15 PM", "1 minute"),
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


        BoxWithConstraints {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(0.dp, 50.dp)
                    .zIndex(1000f),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    modifier = Modifier
                        .background(
                            color = com.example.scmu_app.ui.theme.darkGreen,
                            shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp)
                        )
                        .padding(10.dp, 5.dp),
                    onClick = {
                        val intent = Intent(context, MainScreen::class.java)
                        context.startActivity(intent)
                    }) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Go back",
                        modifier = Modifier.size(30.dp),
                        tint = Color.White

                    )
                }


                Spacer(modifier = Modifier.size(15.dp, 0.dp))

                Row(

                    modifier = Modifier
                        .background(
                            color = com.example.scmu_app.ui.theme.darkGreen,
                            shape = RoundedCornerShape(15.dp, 0.dp, 0.dp, 15.dp)
                        )
                        .padding(10.dp, 5.dp)
                ) {
                    Text(
                        text = " System ",
                        style = titleExtraLarge,
                        color = Color.White,
                        modifier = Modifier.padding(20.dp, 3.dp, 30.dp, 3.dp)
                    )
                    IconButton(


                        onClick = {
                            val intent = Intent(context, EditSystem::class.java)

                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .size(30.dp)
                            .offset(-10.dp, 10.dp)



                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier.background(swampGreen),
                            tint = Color.White


                        )
                    }
                }


            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(0.dp, 70.dp)
                    .height(1175.dp) //TODO
                    .background(com.example.scmu_app.ui.theme.swampGreen)
            ) {
                Column(modifier = Modifier.offset(0.dp, 50.dp)) {
                    BoxWithConstraints {
                        createTile("Information:")
                        Row(
                            Modifier
                                .offset(0.dp, 20.dp)
                        ) {
                            Column(
                                Modifier
                                    .background(com.example.scmu_app.ui.theme.mintGreen)
                                    .offset(0.dp, 30.dp)
                            ) {


                                StatusItem(status = "Waiting", event = "1 hour")



                                Spacer(modifier = Modifier.size(0.dp, 40.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(0.dp, 40.dp))
                    BoxWithConstraints {
                        createTile("History:")
                        Row(
                            Modifier
                                .offset(0.dp, 20.dp)
                        ) {
                            Column(
                                Modifier
                                    .background(mintGreen)
                                    .offset(0.dp, 30.dp)
                            ) {


                                for (item in history) {
                                    HistoryItem(day = item[0], time = item[1], duration = item[2])

                                }
                                Spacer(modifier = Modifier.size(0.dp, 40.dp))
                            }
                        }
                    }

                }
            }
        }


    }

}


@Composable
fun HistoryItem(day: String, time: String, duration: String) {

    Surface(
        color = swampGreen,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(25.dp)

    ) {


            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    color = Color.White,
                    fontSize = 25.sp,
                    text = "$day",
                    modifier = Modifier.padding(bottom = 8.dp).offset(0.dp, 25.dp)
                )
                Text(
                    color = Color.White,
                    fontSize = 20.sp,
                    text = "Time: $time",
                    modifier = Modifier.padding(bottom = 8.dp).offset(190.dp, -20.dp)
                )
                Text(
                    color = Color.White,
                    fontSize = 20.sp,
                    text = "Duration: $duration",
                    modifier = Modifier.offset(160.dp, -20.dp)
                )
            }

    }
}

@Composable
fun StatusItem(status: String, event: String) {
    val darkGreen = Color(0xFF306044)
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.image01),
            contentDescription = "",
            modifier = Modifier
                .padding(start = 10.dp)
                .height(100.dp)
                .width(100.dp)
        )
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
            modifier = Modifier.size(48.dp).offset(0.dp,0.dp)
                .background(shape = RoundedCornerShape(12.dp),color = swampGreen)

        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint= Color.White,




            )
        }
    }
    if (showDialog.value) {
        AlertDialog(
            containerColor = mintGreen,
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = darkGreen),
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


@Preview(showBackground = true)
@Composable
fun ManageSystemPreview() {
    SCMUAppTheme {
        SystemStatusContent()
    }
}
