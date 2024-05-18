package com.example.scmu_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.scmu_app.others.BoardInfo
import com.example.scmu_app.others.Event
import com.example.scmu_app.ui.theme.SCMUAppTheme
import com.example.scmu_app.ui.theme.createTile
import com.example.scmu_app.others.dateToStandardFormat
import com.example.scmu_app.others.fetchBoardInfo
import com.example.scmu_app.others.getDateTime
import com.example.scmu_app.ui.theme.CreateDefaultScaffold
import com.example.scmu_app.ui.theme.mintGreen
import com.example.scmu_app.ui.theme.swampGreen
import com.example.scmu_app.ui.theme.titleExtraLarge
import kotlinx.coroutines.delay
import java.time.format.TextStyle
import java.util.Locale


class SystemStatus : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SCMUAppTheme {
                PreSystemStatusContent()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PreSystemStatusContent() {
    val showLoading = remember { mutableStateOf(true) }
    val boardInfo: MutableState<BoardInfo?> = remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        while (true) {
            fetchBoardInfo(
                onFailure = {},
                onSuccess = {
                    showLoading.value = false
                    boardInfo.value = it
                }
            )
            delay(1000)
        }
    }

    CreateDefaultScaffold(showLoading.value) {
        SystemStatusContent(boardInfo)
    }
}

@SuppressLint("SuspiciousIndentation")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemStatusContent(boardInfo: MutableState<BoardInfo?>) {

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
                            shape = RoundedCornerShape(15.dp)
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
                            .size(40.dp)
                            .offset(-10.dp, 5.dp)
                            .background(swampGreen, RoundedCornerShape(15.dp))


                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier.size(80.dp),
                            tint = Color.White,


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

                                boardInfo.value?.let {

                                    StatusItem(
                                        status = it.board.getStates(),
                                        event = (if (it.board.isOnline()) "Online" else "Offline")
                                    )

                                }



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

                                boardInfo.value?.let {

                                    for (item in it.events.reversed()) {
                                        if (item.eventState != 2)//TODO MAYBE?
                                            HistoryItem(item)

                                    }

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


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryItem(item: Event) {
    val hasEvent = item.eventState == 3
    val startTime = getDateTime(item.start)
    val endTime = getDateTime(item.end)

    val month = startTime.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
    val time = dateToStandardFormat(startTime) + " - " + dateToStandardFormat(endTime)

    Surface(
        color = if (hasEvent) swampGreen else Color.LightGray,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp, 3.dp),
        shape = RoundedCornerShape(15.dp)

    ) {


        Row(
            modifier = Modifier.padding(15.dp)
        ) {

            Column() {
                Text(
                    color = if (hasEvent) Color.White else Color.Black,
                    fontSize = 20.sp,
                    fontWeight = if (hasEvent) FontWeight.Bold else FontWeight.Normal,
                    text = "${startTime.dayOfMonth} ${month}",
                    modifier = Modifier
                )

            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()) {
                Text(
                    color = if (hasEvent) Color.White else Color.Black,
                    fontSize = 16.sp,
                    text = if (hasEvent) time else item.getStates(),
                    modifier = Modifier
                )
            }

        }

    }
}

@Composable
fun StatusItem(status: String, event: String) {
    val darkGreen = Color(0xFF306044)
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.padding(0.dp),
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
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Next event in: $event",
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        IconButton(

            onClick = { showDialog.value = true },
            modifier = Modifier
                .size(48.dp)
                .offset(-20.dp, 0.dp)
                .background(shape = RoundedCornerShape(12.dp), color = swampGreen)

        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White,


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
                        containerColor = darkGreen
                    ),
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
