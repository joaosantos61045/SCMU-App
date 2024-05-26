package com.example.scmu_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.scmu_app.others.Board
import com.example.scmu_app.others.User
import com.example.scmu_app.others.fetchFindBoard
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
    @OptIn(ExperimentalMaterial3Api::class)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SCMUAppTheme {

                val user: User = Gson().fromJson(intent.getStringExtra("user"),User::class.java)
                val systemName = intent.getStringExtra("systemName")!!
                val sysId=intent.getStringExtra("systemId")!!
                    MyAppContent(user,systemName,sysId)


            }
        }
    }//onResume
}

@Composable
fun MyAppContent(user: User, systemName: String, sysId: String) {
    // Initialize the board with default values
    var board by remember { mutableStateOf<Board?>(null) }

    val showLoading = remember { mutableStateOf(true) }
    CreateDefaultScaffold(showLoading.value) {
        // Fetch the board data asynchronously
        LaunchedEffect(Unit) {
            fetchFindBoard(sysId, onFailure = {
                // Handle failure case
            }, onSuccess = { fetchedBoard ->
                // Update the board with the fetched data
                board = fetchedBoard
                showLoading.value = false
            })
        }

        // Only show content after data has been fetched

            ShowEditSystemContent(systemName , sysId, board,user,showLoading)

    }
}
@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ShowEditSystemContent(
    sysName: String,
    sysId: String,
    board: Board?,
    user: User,
    showLoading: MutableState<Boolean>
) {
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
                        showLoading.value=true
                        if(board!=null)
                            updateBoard(board,{showLoading.value=true},{
                                showLoading.value=false
                                val intent = Intent(context, MainScreen::class.java)
                                context.startActivity(intent)
                            })
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

                                    if (board != null) {
                                       board.duration= Dropdown(
                                            "",
                                            timeDurations,
                                            board.duration.toString(),
                                        )
                                    }
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
                                    var hour:Int
                                    var minute: Int
                                    if (board != null) {

                                       hour= Dropdown(
                                            "",
                                            hourIntervals,
                                            (board.hourToStart / 60).toString(),

                                        )*60
                                    Spacer(modifier = Modifier.size(10.dp, 0.dp))

                                        minute = Dropdown(
                                            "",
                                            minuteIntervals,
                                            (board.hourToStart % 60).toString(),
                                        )
                                        board.hourToStart =hour+ minute

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
                                        listOf("Sun","Mon", "Tue", "Wed", "Thu", "Fri", "Sat")


                                    if(board != null)
                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Absolute.Center
                                    ) {
                                        for (index in daysOfWeek.indices) {
                                            DayButton(
                                                day = daysOfWeek[index],
                                                // isSelected = index in selectedIndices,
                                                onToggle = {

                                                        board.rotation[index] =! board.rotation[index]


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

                                var isChecked by remember { mutableStateOf( board.active ) }


                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(16.dp)
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
                                            checkedTrackColor = darkGreen
                                        ),

                                        checked = isChecked ,
                                        onCheckedChange = {

                                            board.active = !board.active
                                        isChecked=board.active
                                        }
                                    )




                                }
                                Spacer(modifier = Modifier.size(0.dp, 40.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.size(0.dp, 40.dp))
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
                                            Text(text = "Remove")
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
                        containerColor = darkGreen
                    ),
                    onClick = { showDialog.value = false }) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                Button(colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                ),
                    onClick = {
                        showLoading.value=true
                        user.boards.removeIf{ it.board.equals(sysId)}
                        updateUser(context.contentResolver,{
                            showLoading.value=false;
                        },{
                            showLoading.value=false;
                            val intent = Intent(context, MainScreen::class.java)
                            context.startActivity(intent)

                        },user)
                        showDialog.value = false

                    }) {

                    Text("Remove")
                }
            },

            )
    }


}


@Preview(showBackground = true)
@Composable
fun EditSystemContentPreview() {
    SCMUAppTheme {
     //   ShowManageSystemContent(true,"", "",)
    }
}
