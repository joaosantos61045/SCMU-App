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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import com.example.scmu_app.others.User
import com.example.scmu_app.others.cancelEvent
import com.example.scmu_app.ui.theme.darkGreen
import com.google.gson.Gson


class SystemStatus : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {


            SCMUAppTheme {
                val user: User = Gson().fromJson(intent.getStringExtra("user"), User::class.java)
                val systemName = intent.getStringExtra("systemName")!!
                val sysId=intent.getStringExtra("systemId")!!
                PreSystemStatusContent(systemName,user,sysId)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PreSystemStatusContent(systemName: String, user: User, sysId: String) {
    val showLoading = remember { mutableStateOf(true) }
    val boardInfo: MutableState<BoardInfo?> = remember { mutableStateOf(null) }
    val currentTime = remember { mutableStateOf(0L) }
    val state = remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {

        while (true) {
            fetchBoardInfo(
                onFailure = {},
                onSuccess = {
                    showLoading.value = false
                    boardInfo.value = it

                    val bInfo = boardInfo.value!!

                    if(state.value < bInfo.board.currentState) {
                        state.value = bInfo.board.currentState

                        if (bInfo.events.isNotEmpty())
                            currentTime.value = bInfo.board.currentDate- bInfo.events[bInfo.events.size - 1].start

                    }
                }
            )
            delay(1000)
        }
    }


    CreateDefaultScaffold(showLoading.value) {

        SystemStatusContent(boardInfo, systemName,user,sysId)
    }
}

@SuppressLint("SuspiciousIndentation")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemStatusContent(
    boardInfo: MutableState<BoardInfo?>,
    systemName: String,
    user: User,
    sysId: String
) {

    val context = LocalContext.current
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
                                    InfoItem(it, systemName, user,sysId)
                                }



                                Spacer(modifier = Modifier.size(0.dp, 40.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(0.dp, 40.dp))
                    BoxWithConstraints {
                        createTile("Event:")
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
                                    StatusItem(it)
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
                modifier = Modifier.fillMaxWidth()
            ) {
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InfoItem(boardInfo: BoardInfo, systemName: String,user: User,sysId:String) {
    val context = LocalContext.current

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
                text = systemName,
                color = Color.Black,
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )

            Row() {
                Text(
                    text = "   Status: ",
                    color = Color.Black,
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = if (boardInfo.board.isOnline()) "Online" else "Offline",
                    color = if (boardInfo.board.isOnline()) swampGreen else Color.Red,
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

        }

        Column(
            modifier = Modifier.padding(end = 15.dp)
        ) {

            IconButton(
                onClick = {
                    val intent = Intent(context, EditSystem::class.java).apply{
                        putExtra("user", Gson().toJson(user))
                        putExtra("systemName", systemName)
                        putExtra("systemId", sysId)
                    }

                    context.startActivity(intent)
                },
                modifier = Modifier
                    .size(50.dp)
                    .background(swampGreen, RoundedCornerShape(15.dp))
                    .zIndex(100f)


            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier.size(35.dp),
                    tint = Color.White,


                    )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatusItem(boardInfo: BoardInfo) {
    val showDialog = remember { mutableStateOf(false) }
    val shouldShow = boardInfo.board.currentState < 2

    Row(
        modifier = Modifier.padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f).padding(start= 15.dp)
        ) {
            Text(
                text = "Status: ${boardInfo.board.getStates()}",
                color = Color.Black,
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                modifier = Modifier
            )

            if (shouldShow) {
                val lastEvent = boardInfo.events[boardInfo.events.size - 1]
                val pausedTime = boardInfo.board.currentDate -lastEvent.pausedTime
                val currentTime = boardInfo.board.currentDate - lastEvent.start
                val teoricExecTime = boardInfo.board.duration * 60
                val percentDone = Math.min(1f, currentTime / (teoricExecTime.toFloat()))

                Text(pausedTime.toString(), color =  Color.Black)
                Text("$currentTime/$teoricExecTime $percentDone%", color =  Color.Black)
                showProgress(percentDone)
            }

            Text(
                text = "    Next event : ${dateToStandardFormat(getDateTime(boardInfo.board.hourToStart.toLong() * 60))}",
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

        }

        Column(
            modifier = Modifier.padding(end = 15.dp)
        ) {
            if (shouldShow)

                IconButton(
                    onClick = { showDialog.value = true },
                    modifier = Modifier
                        .size(50.dp)
                        .background(shape = RoundedCornerShape(15.dp), color = swampGreen)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(35.dp))
                }
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
                    containerColor = darkGreen
                ),
                    onClick = {

                        showDialog.value = false
                        cancelEvent(2)

                    }) {

                    Text("Cancel")
                }
            },

            )
    }
}


@Composable
fun showProgress(percentage: Float) {

    val height = 30;
    val maxWidth = 250;

    BoxWithConstraints {
        Row(
            modifier = Modifier
                .padding(4.dp)
                .width(maxWidth.dp)
                .height(height.dp)
                .background(swampGreen, RoundedCornerShape(50.dp))
                .clip(RoundedCornerShape(50)),
            verticalAlignment = Alignment.CenterVertically
        ) {}

        Row(
            modifier = Modifier
                .padding(4.dp)
                .width((maxWidth * percentage).dp)
                .height(height.dp)
                .background(darkGreen, RoundedCornerShape(50.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {

        }
    }


}

