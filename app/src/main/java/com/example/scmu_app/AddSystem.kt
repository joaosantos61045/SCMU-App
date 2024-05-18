package com.example.scmu_app

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.Nullable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.scmu_app.ui.theme.CreateDefaultScaffold
import com.example.scmu_app.ui.theme.SCMUAppTheme
import com.example.scmu_app.ui.theme.createTile
import com.example.scmu_app.ui.theme.darkGreen
import com.example.scmu_app.ui.theme.mintGreen
import com.example.scmu_app.ui.theme.swampGreen
import com.example.scmu_app.ui.theme.titleExtraLarge
import com.example.scmu_app.ui.theme.titleMedium
import okhttp3.internal.wait


class AddSystem : ComponentActivity() {


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            SCMUAppTheme {
                CreateDefaultScaffold(false) {
                    val systemName = intent.getStringExtra("systemName")!!
                    val systemId = intent.getStringExtra("systemId")!!

                    ShowManageSystemContent(false, systemName, systemId)
                }
            }
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowManageSystemContent(canDelete: Boolean, sysName: String, sysId: String) {
    val context = LocalContext.current
    val hourIntervals = mutableListOf<String>()
    val minuteIntervals = mutableListOf<String>()
    for (i in 1..23) hourIntervals.add(i.toString())
    for (i in 1..59) minuteIntervals.add(i.toString())

    val timeDurations = listOf(
        "5", "10", "15", "20", "25", "30"
    )

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
                            color = darkGreen,
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
                    .height(1175.dp) //TODO
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

                                TextBox("Name", sysName)
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

                                TextBox("Name", sysName)
                                TextBox("Password", password = true)
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

                                    Dropdown(
                                        "",
                                        timeDurations,
                                        "Duration",
                                    )
                                }

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

                                    Dropdown(
                                        "",
                                        hourIntervals,
                                        "H",
                                    )
                                    Spacer(modifier = Modifier.size(10.dp, 0.dp))
                                    Dropdown(
                                        "",
                                        minuteIntervals,
                                        "M",
                                    )
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
                                    DaySelectionRow()

                                }

                                Spacer(modifier = Modifier.size(0.dp, 40.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.size(0.dp, 40.dp))

                    BoxWithConstraints {
                        createTile("Settings")
                        Row(
                            Modifier
                                .offset(0.dp, 20.dp)
                        ) {
                            Column(
                                Modifier
                                    .background(mintGreen)
                                    .offset(0.dp, 30.dp)
                            ) {

                                ToggleButton("Smart Watering")
                                Spacer(modifier = Modifier.size(0.dp, 40.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.size(0.dp, 40.dp))
                    BoxWithConstraints {
                        if (canDelete) {
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
                                            Text(text = "Remove")
                                        }
                                    }
                                    Spacer(modifier = Modifier.size(0.dp, 40.dp))
                                }
                            }

                        } else {

                            Row(
                                Modifier
                                    .offset(0.dp, -25.dp)
                            ) {
                                Column(
                                    Modifier

                                        .offset(0.dp, 0.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(16.dp)
                                    ) {

                                        Button(onClick = {
                                            val intent = Intent(context, EditSystem::class.java)

                                            context.startActivity(intent)
                                        },
                                           modifier= Modifier.size(400.dp,50.dp),
                                            colors= ButtonDefaults.buttonColors(darkGreen)) {
                                            Text(
                                                fontSize = 22.sp,
                                                text = "Add System")
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

    if (showDialog.value) {
        AlertDialog(
            containerColor = mintGreen,
            onDismissRequest = { showDialog.value = false },
            title = { Text("Remove System") },
            text = {
                Column {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .fillMaxWidth(),
                        text = "Are you sure you want to remove the system?"
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                }
            },
            dismissButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = darkGreen),
                    onClick = { showDialog.value = false }) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                Button(colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                ),
                    onClick = {

                        showDialog.value = false
                    }) {

                    Text("Remove")
                }
            },

            )
    }


}


@Composable
fun ToggleButton(text: String) {
    var isChecked by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            modifier = Modifier.weight(1f)
        )
        Switch(
            colors = SwitchDefaults.colors(
                checkedTrackColor= darkGreen
            ),

            checked = isChecked,
            onCheckedChange = { isChecked = it }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DaySelectionRow() {
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val selectedIndices = remember { mutableStateListOf<Int>() }

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Absolute.Center
    ) {
        for (index in daysOfWeek.indices) {
            DayButton(
                day = daysOfWeek[index],
                // isSelected = index in selectedIndices,
                onToggle = { toggleDaySelection(index, selectedIndices) }
            )
        }
    }
}

@Composable
fun DayButton(
    day: String,
    onToggle: () -> Unit
) {
    var isSelected by remember { mutableStateOf(false) }

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

private fun toggleDaySelection(index: Int, selectedIndices: MutableList<Int>) {
    if (index in selectedIndices) {
        selectedIndices.remove(index)
    } else {
        selectedIndices.add(index)
    }
}


@Composable
fun TextBox(label: String, value: String = "", password: Boolean = false) {
    var textValue by remember { mutableStateOf(value) }

    OutlinedTextField(
        visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
        maxLines = 1,
        value = textValue,
        onValueChange = { newText -> textValue = newText },
        label = { Text(label, style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)) },
        colors =
        TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            focusedTextColor = Color.Black,
            focusedPlaceholderColor = darkGreen,
            focusedIndicatorColor = darkGreen,
            focusedLabelColor = darkGreen,
            unfocusedContainerColor = Color.White,
            unfocusedTextColor = Color.Black,
            unfocusedPlaceholderColor = darkGreen,
            unfocusedIndicatorColor = darkGreen,
            unfocusedLabelColor = darkGreen,
        ),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .fillMaxWidth(),

        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dropdown(label: String, options: List<String>, initialValue: String) {

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
                contentColor = Color.White,
                containerColor = darkGreen
            )
        )
        {
            Text(text = selectedOption)
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
}

@Composable
fun Subsubheader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(start = 32.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun AddSystemContentPreview() {
    SCMUAppTheme {
        ShowManageSystemContent(true, "Test", "#arduino-01")
    }
}
