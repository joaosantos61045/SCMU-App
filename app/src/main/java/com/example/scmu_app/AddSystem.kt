package com.example.scmu_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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


class AddSystem : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SCMUAppTheme {
                Scaffold(

                ) {


                    ManageSystemContent(false)

                }
            }
        }
    }//onResume
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageSystemContent(canDelete: Boolean) {
    val context = LocalContext.current
    val timeIntervals = listOf(
        "00:00", "00:30",
        "01:00", "01:30",
        "02:00", "02:30",
        "03:00", "03:30",
        "04:00", "04:30",
        "05:00", "05:30",
        "06:00", "06:30",
        "07:00", "07:30",
        "08:00", "08:30",
        "09:00", "09:30",
        "10:00", "10:30",
        "11:00", "11:30",
        "12:00", "12:30",
        "13:00", "13:30",
        "14:00", "14:30",
        "15:00", "15:30",
        "16:00", "16:30",
        "17:00", "17:30",
        "18:00", "18:30",
        "19:00", "19:30",
        "20:00", "20:30",
        "21:00", "21:30",
        "22:00", "22:30",
        "23:00", "23:30"
    )
    val timeDurations = listOf(
        "5", "10", "15", "20", "25", "30"
    )
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

            title = { Text(" System") },
            navigationIcon = {
                IconButton(onClick = {
                    val intent = Intent(context, MainScreen::class.java)
                    context.startActivity(intent)
                }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Go back")
                }
            },
        )


        Image(
            painter = painterResource(id = R.drawable.image), // Replace 'your_image_resource' with your image resource ID
            contentDescription = null, // Add appropriate content description
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )

        Subheader("General", darkGreen)
        var text = TextBox("WiFi")
        TextBox("House Name")

        Subheader("Schedule", darkGreen)
        Dropdown(
            "Duration (in Minutes)",
            timeDurations,
            "Duration"
        ) // Replace with appropriate duration options
        Dropdown(
            "Hour of System Start",
            timeIntervals,
            "Hour"
        ) // Replace with appropriate hour options
        Subheader("Rotation", darkGreen)
        // Add rotation content here
        DaySelectionRow()
        Subheader("Notifications", darkGreen)
        ToggleButton("Smart Watering")
        if(canDelete) {
            Subheader("Dangerous", darkGreen)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Remove System",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = { showDialog.value = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text(
                        text = "Remove System",
                        color = Color.White
                    )

                }
            }


        }else{
            Button(onClick = {
                val intent = Intent(context, EditSystem::class.java)

                context.startActivity(intent)
            }) {
                Text(text = "Add System")

            }

        }


        if (showDialog.value) {
            AlertDialog(
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
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Switch(
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
            containerColor = if (isSelected) Color.Blue else Color.LightGray
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
fun Subheader(text: String, color: Color) {
    Box(
        modifier = Modifier
            .padding(top = 15.dp, start = 0.dp, end = 16.dp)
            .background(color, shape = AbsoluteRoundedCornerShape(0.dp, 15.dp, 15.dp, 0.dp))
            .width(200.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 0.dp, start = 15.dp, end = 16.dp),
            color = Color.White,
            fontSize = 20.sp

        )
    }

}

@Composable
fun TextBox(label: String) {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = { newText -> text = newText },
        label = { Text(label) },
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dropdown(label: String, options: List<String>, initialValue: String) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(initialValue) }
    Text(text = label)
    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopEnd)
    ) {
        ElevatedButton(

            onClick = { expanded = true }) {
            Text(
                text = selectedOption,


                )
        }


        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }

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
        ManageSystemContent(false)
    }
}
