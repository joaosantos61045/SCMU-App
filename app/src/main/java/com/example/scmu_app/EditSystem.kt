package com.example.scmu_app

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.example.scmu_app.bluetooth.BLEGattCallback
import com.example.scmu_app.bluetooth.BLEManager
import com.example.scmu_app.others.Board
import com.example.scmu_app.others.User
import com.example.scmu_app.others.UserBoard
import com.example.scmu_app.others.WIFICred
import com.example.scmu_app.others.fetchFindBoard
import com.example.scmu_app.others.postBoard
import com.example.scmu_app.others.updateBoard
import com.example.scmu_app.others.updateUser
import com.example.scmu_app.ui.theme.CreateDefaultScaffold
import com.example.scmu_app.ui.theme.SCMUAppTheme
import com.example.scmu_app.ui.theme.createTile
import com.example.scmu_app.ui.theme.darkGreen
import com.example.scmu_app.ui.theme.mintGreen
import com.example.scmu_app.ui.theme.swampGreen
import com.example.scmu_app.ui.theme.titleExtraLarge
import com.google.gson.Gson

class EditSystem : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bluetooth = BLEManager(this)
        enableEdgeToEdge()
        setContent {
            SCMUAppTheme {
                val user: User = Gson().fromJson(intent.getStringExtra("user"), User::class.java)
                val systemName = intent.getStringExtra("systemName")!!
                val board: Board =
                    Gson().fromJson(intent.getStringExtra("board"), Board::class.java)
                val sysId = intent.getStringExtra("systemId")!!
                MyAppContent(user, systemName, sysId, board, bluetooth, true, this)
            }
        }
    }
}

@Composable
fun MyAppContent(
    user: User,
    systemName: String,
    sysId: String,
    board: Board,
    bluetooth: BLEManager,
    editSystem: Boolean,
    clazz: ComponentActivity
) {

    val showLoading = remember { mutableStateOf(false) }
    CreateDefaultScaffold(showLoading.value) {
        ShowEditSystemContent(
            systemName,
            sysId,
            board,
            user,
            showLoading,
            bluetooth,
            editSystem,
            clazz
        )
    }
}

@SuppressLint("MutableCollectionMutableState", "SuspiciousIndentation", "MissingPermission")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShowEditSystemContent(
    sysName: String,
    sysId: String,
    board: Board?,
    user: User,
    showLoading: MutableState<Boolean>,
    bluetooth: BLEManager,
    editSystem: Boolean,
    clazz: ComponentActivity
) {
    val context = LocalContext.current
    val hourIntervals = mutableListOf<String>()
    val minuteIntervals = mutableListOf<String>()
    for (i in 0..23) hourIntervals.add(i.toString())
    for (i in 0..59) minuteIntervals.add(i.toString())

    val name = remember { mutableStateOf(sysName) }
    val credId = remember { mutableStateOf("") }
    val credPwd = remember { mutableStateOf("") }
    val showCredentials = remember { mutableStateOf(false) }

    val wifiSSID = remember { mutableStateOf("") }
    val wifiPWD = remember { mutableStateOf("") }

    val feedText = remember { mutableStateOf<Pair<String, Color>?>(null) }
    val device = remember { mutableStateOf<BluetoothDevice?>(null) }
    val btnBlock = remember { mutableStateOf(false) }

    val timeDurations = listOf("5", "10", "15", "20", "25", "30", "35", "40")
    val showDialog = remember { mutableStateOf(false) }

    val changed = remember { mutableStateOf(false) }

    if (showDialog.value) {
        AlertDialog(
            containerColor = mintGreen,
            onDismissRequest = { showDialog.value = false },
            title = {
                Text(
                    "Remove System",
                    color = darkGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            },
            text = {
                Column {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .fillMaxWidth(),
                        text = "Are you sure you want to remove the system?", color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                }
            },
            dismissButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray
                    ),
                    onClick = { showDialog.value = false }) {
                    Text("Cancel", color = Color.Black)
                }
            },
            confirmButton = {
                Button(colors = ButtonDefaults.buttonColors(
                    containerColor = darkGreen
                ),
                    onClick = {
                        showLoading.value = true
                        user.boards.removeIf { it.board.equals(sysId) }
                        updateUser({
                            showLoading.value = false
                        }, {
                            showLoading.value = false
                            val intent = Intent(context, MainScreen::class.java)
                            context.startActivity(intent)
                            clazz.finish()

                        }, user)
                        showDialog.value = false

                    }) {

                    Text("Confirm", color = Color.White)
                }
            })
    }

    if (showCredentials.value) {
        AlertDialog(
            containerColor = mintGreen,
            onDismissRequest = { showCredentials.value = false },
            title = {
                Text(
                    "Connect device",
                    color = darkGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            },
            icon = {},
            text = {

                Column {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .fillMaxWidth(),
                        text = "Please insert the board credentials before connecting!",
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    credId.value = TextBox(
                        value = credId.value,
                        label = "ID",
                        password = false
                    )
                    credPwd.value = TextBox(
                        value = credPwd.value,
                        label = "Password",
                        password = true
                    )
                }
            },
            dismissButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray
                    ),
                    onClick = { showCredentials.value = false }) {
                    Text("Back", color = Color.Black)
                }
            },
            confirmButton = {
                Button(colors = ButtonDefaults.buttonColors(
                    containerColor = darkGreen
                ),
                    onClick = {
                        showCredentials.value = false
                        showLoading.value = true

                        connectWifi(
                            device = device,
                            btnBlock = btnBlock,
                            context = context,
                            credId = credId,
                            credPwd = credPwd,
                            wifiSSID = wifiSSID,
                            wifiPWD = wifiPWD,
                            feedText = feedText,
                            showCredentials = showCredentials,
                            showLoading = showLoading,
                            onSuccess = { showLoading.value = false },
                            onFailure = { showLoading.value = false })

                    }) {
                    Text("Test", color = Color.White)
                }
            }
        )
    }

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
                            color = darkGreen,
                            shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp)
                        )
                        .padding(10.dp, 5.dp),
                    onClick = {

                        if (editSystem) {
                            showLoading.value = true
                            updateUser({}, {}, user)
                            if (board != null)
                                updateBoard(board, { showLoading.value = true }, {
                                    showLoading.value = false
                                    val intent = Intent(context, SystemStatus::class.java).apply {
                                        putExtra("user", Gson().toJson(user))
                                        putExtra(
                                            "systemName",
                                            user.boards.find { it.board == sysId }!!.name
                                        )
                                        putExtra("systemId", sysId)
                                    }
                                    context.startActivity(intent)
                                    clazz.finish()
                                })
                        } else {
                            val intent = Intent(context, MainScreen::class.java)
                            context.startActivity(intent)
                            clazz.finish()
                        }

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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(0.dp, 70.dp)
                    .background(swampGreen)
            ) {

                Column(modifier = Modifier.offset(0.dp, 50.dp)) {

                    BoxWithConstraints {
                        createTile("General")
                        Row(
                            Modifier
                                .offset(0.dp, 20.dp)
                        ) {
                            Column(
                                Modifier
                                    .background(mintGreen)
                                    .offset(0.dp, 30.dp)
                            ) {

                                if (editSystem)
                                    user.boards.find { it.board == sysId }!!.name =
                                        TextBox("Name", sysName)
                                else
                                    name.value = TextBox("Name", name.value)
                                Spacer(modifier = Modifier.size(0.dp, 40.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.size(0.dp, 40.dp))

                    BoxWithConstraints {
                        createTile("WiFi")
                        Row(
                            Modifier
                                .offset(0.dp, 20.dp)
                        ) {
                            Column(
                                Modifier
                                    .background(mintGreen)
                                    .offset(0.dp, 30.dp)
                            ) {

                                wifiSSID.value = TextBox("Name", wifiSSID.value)
                                wifiPWD.value = TextBox("Password", wifiPWD.value, password = true)

                                Row(modifier = Modifier.padding(15.dp, 5.dp)) {

                                    val blueDevices =
                                        remember { mutableStateOf(listOf<BluetoothDevice>()) }


                                    bluetooth.getScan().setOnResultChange {
                                        blueDevices.value = it.values.toList()
                                    }


                                    Button(
                                        colors = ButtonDefaults.buttonColors(
                                            contentColor = Color.White,
                                            containerColor = swampGreen,
                                            disabledContentColor = Color.Black,
                                            disabledContainerColor = Color.LightGray
                                        ),
                                        shape = RoundedCornerShape(15.dp),
                                        modifier = Modifier.height(50.dp),
                                        enabled = !btnBlock.value && device.value != null && wifiSSID.value.isNotEmpty() && wifiPWD.value.isNotEmpty(),
                                        onClick = {
                                            showCredentials.value = true
                                        }) {
                                        Text(text = "Connect")
                                    }
                                    Spacer(modifier = Modifier.size(10.dp, 0.dp))
                                    device.value =
                                        BlueDropdown(label = "", options = blueDevices.value,
                                            onClick = {
                                                bluetooth.start()
                                                bluetooth.getScan().startDiscover()
                                            })


                                }
                                feedText.value?.let {
                                    Text(
                                        feedText.value!!.first,
                                        color = feedText.value!!.second,
                                        modifier = Modifier.padding(start = 20.dp),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.size(0.dp, 40.dp))
                            }

                        }
                    }

                    Spacer(modifier = Modifier.size(0.dp, 40.dp))
                    BoxWithConstraints {
                        createTile("Schedule")
                        Row(
                            Modifier
                                .offset(0.dp, 20.dp)
                        ) {
                            Column(
                                Modifier
                                    .background(mintGreen)
                                    .offset(0.dp, 30.dp)
                            ) {
                                if (board != null && (changed.value || !changed.value)) {
                                    Row(Modifier.fillMaxWidth()) {
                                        Text(
                                            text = "Duration:",
                                            style = TextStyle(
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            ),
                                            modifier = Modifier.padding(22.dp, 10.dp, 20.dp, 0.dp)
                                        )


                                        board.duration = Dropdown(
                                            "",
                                            timeDurations,
                                            if (board.duration != null) board.duration.toString() else null,
                                        )

                                        changed.value = !changed.value
                                    }
                                }

                                if (board != null && (changed.value || !changed.value)) {
                                    Row(Modifier.fillMaxWidth()) {
                                        Text(
                                            text = "Hour:",
                                            style = TextStyle(
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            ),
                                            modifier = Modifier.padding(22.dp, 10.dp, 20.dp, 0.dp)
                                        )

                                        val hour: Int? = Dropdown(
                                            "",
                                            hourIntervals,
                                            if (board.hourToStart != null) (board.hourToStart!! / 60).toString() else null,
                                        )
                                        Spacer(modifier = Modifier.size(10.dp, 0.dp))

                                        val minute: Int? = Dropdown(
                                            "",
                                            minuteIntervals,
                                            if (board.hourToStart != null) (board.hourToStart!! % 60).toString() else null,
                                        )

                                        if (hour != null && minute != null)
                                            board.hourToStart = hour * 60 + minute

                                        changed.value = !changed.value
                                    }

                                }


                                Column(Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Rotation:",
                                        style = TextStyle(
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        ),
                                        modifier = Modifier.padding(20.dp, 10.dp, 20.dp, 0.dp)
                                    )

                                    val daysOfWeek =
                                        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")


                                    if (board != null)
                                        FlowRow(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Absolute.Center
                                        ) {
                                            for (index in daysOfWeek.indices) {
                                                DayButton(
                                                    day = daysOfWeek[index],
                                                    onToggle = {

                                                        board.rotation[index] =
                                                            !board.rotation[index]


                                                    },
                                                    isSelect = board.rotation.get(index)
                                                )


                                            }
                                        }
                                }

                                Spacer(modifier = Modifier.size(0.dp, 40.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.size(0.dp, 40.dp))

                    if (editSystem) {
                        BoxWithConstraints {
                            createTile("Settings")
                            Row(
                                Modifier
                                    .offset(0.dp, 20.dp)
                            ) {

                                if (board != null)
                                    Column(
                                        Modifier
                                            .background(mintGreen)
                                            .offset(0.dp, 30.dp)
                                    ) {

                                        var isChecked by remember { mutableStateOf(board.active) }
                                        var userBoard = user.boards.find { it.board == sysId }
                                        var notiCheck by remember { mutableStateOf(userBoard!!.notifications) }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        ) {
                                            Text(
                                                text = "Smart Watering",
                                                style = TextStyle(
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.Black
                                                ),
                                                modifier = Modifier.weight(1f)
                                            )

                                            Switch(
                                                colors = SwitchDefaults.colors(
                                                    checkedTrackColor = darkGreen,
                                                    checkedIconColor = swampGreen,
                                                    checkedBorderColor = swampGreen,
                                                    checkedThumbColor = swampGreen,
                                                ),

                                                checked = isChecked,
                                                onCheckedChange = {

                                                    board.active = !board.active
                                                    isChecked = board.active
                                                }
                                            )


                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        ) {
                                            Text(
                                                text = "Notifications",
                                                style = TextStyle(
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.Black
                                                ),
                                                modifier = Modifier.weight(1f)
                                            )

                                            Switch(
                                                colors = SwitchDefaults.colors(
                                                    checkedTrackColor = darkGreen,
                                                    checkedIconColor = swampGreen,
                                                    checkedBorderColor = swampGreen,
                                                    checkedThumbColor = swampGreen,
                                                ),
                                                checked = notiCheck,
                                                onCheckedChange = {

                                                    userBoard?.notifications =
                                                        !userBoard?.notifications!!
                                                    notiCheck = userBoard.notifications
                                                }
                                            )


                                        }
                                        Spacer(modifier = Modifier.size(0.dp, 40.dp))
                                    }
                            }
                        }
                        Spacer(modifier = Modifier.size(0.dp, 40.dp))
                    }
                    if (editSystem) {
                        BoxWithConstraints {

                            createTile("Dangerous")
                            Row(
                                Modifier
                                    .offset(0.dp, 20.dp)
                            ) {
                                Column(
                                    Modifier
                                        .background(mintGreen)
                                        .offset(0.dp, 30.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = "Remove System",
                                            style = TextStyle(
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )
                                        Button(
                                            onClick = { showDialog.value = true },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.Red
                                            )
                                        ) {
                                            Text(text = "Remove", color = Color.White)
                                        }
                                    }
                                    Spacer(modifier = Modifier.size(0.dp, 40.dp))
                                }
                            }


                        }
                        Spacer(modifier = Modifier.size(0.dp, 40.dp))
                    }
                    if (!editSystem) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()

                        ) {

                            if (board != null && (changed.value || !changed.value)) {
                                Button(
                                    onClick = {

                                        showLoading.value = true

                                        connectWifi(
                                            device = device,
                                            btnBlock = btnBlock,
                                            context = context,
                                            credId = credId,
                                            credPwd = credPwd,
                                            wifiSSID = wifiSSID,
                                            wifiPWD = wifiPWD,
                                            feedText = feedText,
                                            showCredentials = showCredentials,
                                            showLoading = showLoading,
                                            onSuccess = {
                                                postBoard(board!!,
                                                    onSuccess = {
                                                        user.boards.add(
                                                            UserBoard(
                                                                board.id,
                                                                sysName,
                                                                true
                                                            )
                                                        )
                                                        updateUser(user = user,
                                                            onSuccess = {
                                                                val intent = Intent(
                                                                    context,
                                                                    MainScreen::class.java
                                                                )
                                                                context.startActivity(intent)
                                                                clazz.finish()
                                                            },
                                                            onFailure = {
                                                                showLoading.value = false
                                                            }
                                                        )
                                                    },
                                                    onFailure = { showLoading.value = false })
                                            },
                                            onFailure = { showLoading.value = false })

                                    },
                                    modifier = Modifier.size(200.dp, 50.dp),
                                    enabled = wifiPWD.value.isNotEmpty() && wifiSSID.value.isNotEmpty() && name.value.isNotEmpty()
                                            && device.value != null && board.duration != null && board.hourToStart != null,
                                    colors = ButtonDefaults.buttonColors(
                                        contentColor = Color.White,
                                        containerColor = darkGreen,
                                        disabledContentColor = Color.Black,
                                        disabledContainerColor = Color.LightGray
                                    ),
                                ) {
                                    Text(
                                        fontSize = 22.sp,
                                        text = "Add System"
                                    )
                                }
                            }

                        }
                        Spacer(modifier = Modifier.size(0.dp, 10.dp))
                    }

                }
            }
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun connectWifi(
        device: MutableState<BluetoothDevice?>,
        btnBlock: MutableState<Boolean>,
        context: Context,
        credId: MutableState<String>,
        credPwd: MutableState<String>,
        wifiSSID: MutableState<String>,
        wifiPWD: MutableState<String>,
        feedText: MutableState<Pair<String, Color>?>,
        showCredentials: MutableState<Boolean>,
        showLoading: MutableState<Boolean>,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        device.value?.let {
            btnBlock.value = true
            it.connectGatt(
                context,
                false,
                BLEGattCallback(sendMessage = {
                    "wifi\t${credId.value}\t${credPwd.value}\t" + Gson().toJson(
                        WIFICred(
                            wifiSSID.value,
                            wifiPWD.value
                        )
                    ) + "\n"
                }, onResponse = { response ->
                    btnBlock.value = false
                    Log.w("PT-irineu", "Data received: $response")

                    when (response) {
                        0 -> {
                            feedText.value = Pair("Device connected successfully", darkGreen)
                            onSuccess()
                            return@BLEGattCallback
                        }

                        1 -> showCredentials.value = true
                        2 -> feedText.value = Pair("Wrong WIFI credentials!", Color.Red)
                        else -> feedText.value = Pair("Failed to communicate!", Color.Red)
                    }

                    onFailure()
                })
            )
        }
    }

    @Composable
    fun DayButton(
        day: String,
        onToggle: () -> Unit,
        isSelect: Boolean
    ) {
        var isSelected by remember { mutableStateOf(isSelect) }

        Button(
            onClick = {
                isSelected = !isSelected
                onToggle()
            },
            modifier = Modifier.padding(2.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSelected) darkGreen else Color.LightGray,
                contentColor = if (isSelected) Color.White else Color.Black
            ),
            shape = CircleShape
        ) {
            Text(
                modifier = Modifier.padding(1.dp),
                text = day
            )
        }
    }

    @Composable
    fun TextBox(label: String, value: String = "", password: Boolean = false): String {
        var textValue by remember { mutableStateOf(value) }

        OutlinedTextField(
            visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
            maxLines = 1,
            value = textValue,
            onValueChange = { newText -> textValue = newText },
            label = {
                Text(
                    label,
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold)
                )
            },
            colors =
            TextFieldDefaults.colors(
                focusedContainerColor = mintGreen,
                focusedTextColor = Color.Black,
                focusedPlaceholderColor = darkGreen,
                focusedIndicatorColor = darkGreen,
                focusedLabelColor = darkGreen,
                unfocusedContainerColor = mintGreen,
                unfocusedTextColor = Color.Black,
                unfocusedPlaceholderColor = darkGreen,
                unfocusedIndicatorColor = darkGreen,
                unfocusedLabelColor = darkGreen,
            ),
            textStyle = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()

        )

        return textValue;
    }

    @SuppressLint("MissingPermission")
    @Composable
    fun BlueDropdown(
        label: String,
        options: List<BluetoothDevice>,
        onClick: () -> Unit
    ): BluetoothDevice? {

        var expanded by remember { mutableStateOf(false) }
        var selectedOption by remember { mutableStateOf<BluetoothDevice?>(null) }
        Text(text = label)
        Box(
            modifier = Modifier
                .wrapContentSize(Alignment.TopEnd)
        ) {
            ElevatedButton(
                onClick = {
                    onClick()
                    expanded = true
                },
                colors = ButtonDefaults.buttonColors(
                    contentColor = if (selectedOption == null) Color.Black else Color.White,
                    containerColor = if (selectedOption == null) Color.LightGray else darkGreen,
                    disabledContentColor = Color.Black,
                    disabledContainerColor = Color.LightGray
                ),
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(15.dp)

            )
            {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        contentDescription = "",
                        modifier = Modifier.size(20.dp),
                        tint = if (selectedOption == null) Color.Black else Color.White

                    )

                    if (selectedOption != null)
                        Text(
                            text = selectedOption!!.name,
                            modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp)
                        )
                    else
                        Text(
                            text = "Select device",
                            modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp)
                        )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.height(Math.min(55 * options.size + 1, 200).dp)

            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.name) },
                        onClick = {
                            selectedOption = option
                            expanded = false
                        }
                    )
                }
            }
        }
        return selectedOption
    }

    @Composable
    fun Dropdown(label: String, options: List<String>, initialValue: String?): Int? {

        var expanded by remember { mutableStateOf(false) }
        var selectedOption by remember { mutableStateOf(initialValue) }
        Text(text = label)
        Box(
            modifier = Modifier
                .wrapContentSize(Alignment.TopEnd)
        ) {
            ElevatedButton(
                onClick = { expanded = true },
                colors = ButtonDefaults.buttonColors(
                    contentColor = if (initialValue == null) Color.Black else Color.White,
                    containerColor = if (initialValue == null) Color.LightGray else darkGreen,
                    disabledContentColor = Color.Black,
                    disabledContainerColor = Color.LightGray
                )
            )
            {
                (if (selectedOption == null) "-" else selectedOption)?.let { Text(text = it) }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.height(200.dp)

            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedOption = option
                            expanded = false
                        }
                    )
                }
            }
        }

        try {
            return selectedOption!!.toInt()
        } catch (e: Exception) {
            return null
        }
    }
