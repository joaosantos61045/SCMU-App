package com.example.scmu_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.scmu_app.others.Board
import com.example.scmu_app.others.BoardInfo
import com.example.scmu_app.others.Event
import com.example.scmu_app.others.StateNotificationService
import com.example.scmu_app.others.User
import com.example.scmu_app.others.dateToStandardFormat
import com.example.scmu_app.others.fetchBoardInfo
import com.example.scmu_app.others.formatDuration
import com.example.scmu_app.others.getDateTime
import com.example.scmu_app.others.updateBoard
import com.example.scmu_app.ui.theme.CreateDefaultScaffold
import com.example.scmu_app.ui.theme.SCMUAppTheme
import com.example.scmu_app.ui.theme.bgGreen
import com.example.scmu_app.ui.theme.createTile
import com.example.scmu_app.ui.theme.darkGreen
import com.example.scmu_app.ui.theme.darkRed
import com.example.scmu_app.ui.theme.mintGreen
import com.example.scmu_app.ui.theme.swampGreen
import com.example.scmu_app.ui.theme.titleExtraLarge
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.format.TextStyle
import java.util.Locale


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
                val sysId = intent.getStringExtra("systemId")!!
                val notiSystem = StateNotificationService(LocalContext.current)

                notiSystem.createNotificationChannel()
                PreSystemStatusContent(systemName, user, sysId, notiSystem, this)
            }
        }
    }

}

@SuppressLint("SuspiciousIndentation", "MutableCollectionMutableState")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PreSystemStatusContent(
    systemName: String,
    user: User,
    sysId: String,
    notiSystem: StateNotificationService,
    clazz: SystemStatus
) {

    val showLoading = remember { mutableStateOf(true) }
    val boardInfo: MutableState<BoardInfo?> = remember { mutableStateOf(null) }
    val events = remember { mutableStateOf<MutableList<Event>?>(null) }
    val currentDate = remember { mutableStateOf(0L) }
    var state = remember { mutableStateOf(2) }
    DisposableEffect(1) {
        val scope = CoroutineScope(Dispatchers.Main)


        val job = scope.launch {
            while (isActive) {

                fetchBoardInfo(sysId, onFailure = {}, onSuccess = { board ->
                    showLoading.value = false
                    boardInfo.value = board

                    board.board.lastFetch = System.currentTimeMillis()
                    val userBoard = user.boards.find { it.board == sysId }!!


                    Log.w("Notification", userBoard.notifications.toString())

                    if (board.board.isOnline() && userBoard.notifications)
                        if (state.value != board.board.currentState) {

                            if (state.value == 1 && board.board.currentState == 0) {
                                Log.w("Notification", "Notification paused to resume")
                                notiSystem.showBasicNotification(3)
                            } else if (board.board.currentState != 1) {
                                Log.w("Notification", "Notification normal")
                                notiSystem.showBasicNotification(board.board.currentState)
                            }

                            state.value = board.board.currentState
                        }


                    if (board.eventsChanged(events.value))
                        events.value = board.events
                })


                delay(1000)
            }
        }

        onDispose {
            job.cancel()
            Log.w("Notification", "Dispose")
        }
    }



    CreateDefaultScaffold(showLoading.value) {
        SystemStatusContent(boardInfo, systemName, user, sysId, events, clazz)
    }

}


@SuppressLint("SuspiciousIndentation")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SystemStatusContent(
    boardInfo: MutableState<BoardInfo?>,
    systemName: String,
    user: User,
    sysId: String,
    events: MutableState<MutableList<Event>?>, clazz: SystemStatus
) {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxHeight()
            .background(mintGreen)

    ) {


        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(0.dp, 50.dp)
                    .zIndex(1000f),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(modifier = Modifier
                    .background(
                        color = darkGreen,
                        shape = RoundedCornerShape(15.dp)
                    )
                    .padding(10.dp, 5.dp), onClick = {
                    val intent = Intent(context, MainScreen::class.java)
                    context.startActivity(intent)
                    clazz.finish()
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
                            color = darkGreen,
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
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(0.dp, 70.dp)
                    //.height(2175.dp) //TODO
                    .background(swampGreen)
                    .heightIn(800.dp)
            ) {
                Column(modifier = Modifier.offset(0.dp, 50.dp)) {
                    BoxWithConstraints {
                        createTile("Information:")
                        Row(
                            Modifier.offset(0.dp, 20.dp)
                        ) {
                            Column(
                                Modifier
                                    .background(mintGreen)
                                    .offset(0.dp, 30.dp)
                            ) {

                                boardInfo.value?.let {
                                    InfoItem(it, systemName, user, sysId, it.board, clazz)
                                }



                                Spacer(modifier = Modifier.size(0.dp, 40.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(0.dp, 40.dp))

                    if (boardInfo.value != null && boardInfo.value!!.board.isOnline()) {
                        BoxWithConstraints {
                            createTile("Events:")
                            Row(
                                Modifier.offset(0.dp, 20.dp)
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
                    }
                    BoxWithConstraints {
                        createTile("History:")
                        Row(
                            Modifier.offset(0.dp, 20.dp)
                        ) {
                            Column(
                                Modifier
                                    .background(mintGreen)
                                    .offset(0.dp, 30.dp)
                            ) {
                                ListEvents(events)
                                Spacer(modifier = Modifier.size(0.dp, 40.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(0.dp, 210.dp))
                }

            }

        }
    }


}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ListEvents(events: MutableState<MutableList<Event>?>) {
    if (events.value == null) return

    for (item in events.value!!.reversed()) {
        if (item.eventState != 2) HistoryItem(item)
    }
}


@SuppressLint("DefaultLocale")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryItem(item: Event) {
    val hasEvent = item.eventState == 3
    val startTime = getDateTime(item.start)
    val endTime = getDateTime(item.end)

    val month = startTime.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
    val time = dateToStandardFormat(startTime) + " - " + dateToStandardFormat(endTime)

    var isExpanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = if (hasEvent) swampGreen else Color.LightGray),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp, 3.dp)
            .clickable {
                if (hasEvent)
                    isExpanded = !isExpanded
            },
        shape = RoundedCornerShape(15.dp)
    ) {
        Column(modifier = Modifier) {

            Row(
                modifier = Modifier.padding(15.dp)
            ) {

                Column {
                    Text(
                        color = if (hasEvent) Color.White else Color.Black,
                        fontSize = 20.sp,
                        fontWeight = if (hasEvent) FontWeight.Bold else FontWeight.Normal,
                        text = "${startTime.dayOfMonth} ${month}",
                    )

                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row {
                        Text(
                            color = if (hasEvent) Color.White else Color.Black,
                            fontSize = 16.sp,
                            text = if (hasEvent) time else item.getStates(),
                            modifier = Modifier.padding(top = 3.dp)
                        )

                        if (hasEvent)
                            Icon(
                                if (!isExpanded) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropUp,
                                contentDescription = "",
                                modifier = Modifier.size(30.dp),
                                tint = Color.White
                            )
                    }
                }

            }

            if (isExpanded && hasEvent) {
                Column(
                    modifier = Modifier
                        .background(bgGreen)
                        .padding(15.dp)
                ) {

                    Column {
                        Row() {
                            Text(
                                "Timeline:",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Thermostat,
                                    contentDescription = "Temp",
                                    modifier = Modifier.size(14.dp),
                                    tint = Color.Black,
                                )
                                Text(
                                    text = String.format("%.1fCº", item.avgTemp),
                                    color = Color.Black,
                                    style = androidx.compose.ui.text.TextStyle(
                                        fontSize = 14.sp,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = "Hum",
                                    modifier = Modifier.size(14.dp),
                                    tint = Color.Black,
                                )
                                Text(
                                    text = String.format("%.1f%%", item.avgHum),
                                    color = Color.Black,
                                    style = androidx.compose.ui.text.TextStyle(
                                        fontSize = 14.sp,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                        }

                        Row {

                            Box(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                val total = item.executionTime + item.pausedTime
                                var current = total.toFloat()
                                for (e in item.timeLine.reversed()) {
                                    LinearProgressIndicator(
                                        trackColor = Color.Transparent,
                                        color = if (e.state == 0) darkGreen else swampGreen,
                                        progress = current / total,
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .fillMaxWidth()
                                            .height(20.dp)
                                            .background(
                                                Color.Transparent,
                                                RoundedCornerShape(50.dp)
                                            )
                                            .clip(RoundedCornerShape(50)),
                                    )
                                    current -= e.duration
                                }
                            }
                        }

                        Column(modifier = Modifier.padding(20.dp, 0.dp)) {

                            Row {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = "Temp",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.Black,
                                )
                                Text(
                                    "Run time: ",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    formatDuration(item.executionTime.toLong(), true),
                                    color = Color.Black,
                                    fontSize = 15.sp
                                )
                            }

                            Row {
                                Icon(
                                    imageVector = Icons.Default.TimerOff,
                                    contentDescription = "Temp",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.Black,
                                )
                                Text(
                                    "Paused time: ",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    formatDuration(item.pausedTime.toLong(), true),
                                    color = Color.Black,
                                    fontSize = 15.sp
                                )
                            }
                        }
                        Column(modifier = Modifier.padding(20.dp, 0.dp)) {

                        }
                    }


                }
            }

        }


    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InfoItem(
    boardInfo: BoardInfo,
    systemName: String,
    user: User,
    sysId: String,
    board: Board,
    clazz: SystemStatus
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier.padding(0.dp), verticalAlignment = Alignment.CenterVertically
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
                text = systemName, color = Color.Black, style = androidx.compose.ui.text.TextStyle(
                    fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black
                )
            )

            Row() {
                Text(
                    text = "   Status: ",
                    color = Color.Black,
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = if (boardInfo.board.isOnline()) "Online" else "Offline",
                    color = if (boardInfo.board.isOnline()) swampGreen else darkRed,
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

            }

            if (boardInfo.board.isOnline()) {
                Row() {
                    Spacer(modifier = Modifier.size(10.dp))
                    Icon(
                        imageVector = Icons.Default.Thermostat,
                        contentDescription = "Temp",
                        modifier = Modifier.size(25.dp),
                        tint = darkGreen,
                    )
                    Text(
                        text = String.format("%.1fCº", board.currentTemp),
                        color = Color.Black,
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = "Hum",
                        modifier = Modifier.size(25.dp),
                        tint = darkGreen,
                    )
                    Text(
                        text = String.format("%.1f%%", board.currentHum),
                        color = Color.Black,
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

        }

        Column(
            modifier = Modifier.padding(end = 15.dp)
        ) {

            IconButton(
                onClick = {
                    val intent = Intent(context, EditSystem::class.java).apply {
                        putExtra("user", Gson().toJson(user))
                        putExtra("systemName", systemName)
                        putExtra("systemId", sysId)
                        putExtra("board", Gson().toJson(board))

                    }

                    context.startActivity(intent)
                    clazz.finish()
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
    val lastTime = remember { mutableStateOf(0F) }
    val targetValue = remember { mutableStateOf(0F) }
    val lastFetch = remember { mutableStateOf(System.currentTimeMillis()) }
    var shouldShow = boardInfo.board.currentState < 2
    var state = remember { mutableStateOf(boardInfo.board.state) }
    Row(
        modifier = Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 15.dp)
        ) {
            Text(
                text = "State: ${boardInfo.board.getStates()}",
                color = Color.Black,
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black
                ),
                modifier = Modifier
            )
            LaunchedEffect(boardInfo, targetValue) {
                while (true) {
                    if (state.value == -1 && boardInfo.board.currentState != 2)
                        if (boardInfo.board.currentState == 0) {
                            val stepSize = (targetValue.value - lastTime.value) / 5F
                            lastTime.value += stepSize
                            lastTime.value = lastTime.value.coerceAtMost(targetValue.value)
                        }
                    if (boardInfo.board.currentState == 2) state.value = boardInfo.board.state

                    delay(500)
                }
            }

            if (shouldShow) {


                if (boardInfo.board.currentState == 0 && System.currentTimeMillis() - lastFetch.value > 1000) {
                    val lastEvent = boardInfo.events[boardInfo.events.size - 1]
                    targetValue.value =
                        ((boardInfo.board.currentDate - lastEvent.start) - lastEvent.pausedTime).toFloat()
                    lastFetch.value = System.currentTimeMillis()
                }

                val teoricExecTime = boardInfo.board.duration!! * 60
                val percentDone = Math.min(1f, lastTime.value / (teoricExecTime.toFloat()))

                showProgress(percentDone)
            } else lastTime.value = 0F


            if (boardInfo.board.currentState == 2) {
                Text(
                    text = "    Next event : ${dateToStandardFormat(getDateTime(boardInfo.board.hourToStart!!.toLong() * 60))}",
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            } else {
                Text(
                    text = "    Duration : ${formatDuration(lastTime.value.toLong())}",
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

        }

        Column(
            modifier = Modifier.padding(end = 15.dp)
        ) {

            if (state.value == -1 && boardInfo.board.currentState != 2)

                IconButton(
                    onClick = {

                        showDialog.value = true
                    },
                    modifier = Modifier
                        .size(50.dp)
                        .background(shape = RoundedCornerShape(15.dp), color = swampGreen)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(35.dp)
                    )
                }
        }
    }
    if (showDialog.value) {
        AlertDialog(
            containerColor = mintGreen,
            onDismissRequest = { showDialog.value = false },
            title = { Text("Cancel Event", color = Color.Black, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .fillMaxWidth(),
                        text = "Are you sure you want to cancel the current event?",
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                }
            },
            dismissButton = {
                Button(colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray
                ), onClick = { showDialog.value = false }) {
                    Text("Cancel", color = Color.Black)
                }
            },
            confirmButton = {
                Button(colors = ButtonDefaults.buttonColors(
                    containerColor = darkGreen
                ), onClick = {

                    showDialog.value = false
                    state.value = 2
                    boardInfo.board.state = 2
                    // cancelEvent(2, boardInfo.board.id)
                    updateBoard(boardInfo.board, {}, {})

                }) {

                    Text("Confirm", color = Color.White)
                }
            },

            )
    }
}


@Composable
fun showProgress(percentage: Float) {

    LinearProgressIndicator(
        trackColor = swampGreen,
        color = darkGreen,
        progress = percentage,
        modifier = Modifier
            .padding(4.dp)
            .width(250.dp)
            .height(30.dp)
            .background(swampGreen, RoundedCornerShape(50.dp))
            .clip(RoundedCornerShape(50))
    )

}

