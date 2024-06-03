package com.example.scmu_app


import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent
import android.os.Build

import androidx.compose.runtime.Composable

import android.os.Bundle
import android.provider.Settings

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add

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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.scmu_app.others.User
import com.example.scmu_app.others.UserBoard
import com.example.scmu_app.others.fetchFindBoard
import com.example.scmu_app.others.fetchUser
import com.example.scmu_app.others.updateUser
import com.example.scmu_app.ui.theme.CreateDefaultScaffold

import com.example.scmu_app.ui.theme.SCMUAppTheme
import com.example.scmu_app.ui.theme.bgGreen
import com.example.scmu_app.ui.theme.darkGreen
import com.example.scmu_app.ui.theme.mintGreen
import com.example.scmu_app.ui.theme.titleExtraLarge
import com.example.scmu_app.ui.theme.titleSmall
import com.example.scmu_app.ui.theme.titleMedium
import com.google.gson.Gson

class MainScreen : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            SCMUAppTheme {
                PreMain(this.contentResolver,this)
            }
        }


    }
}

@SuppressLint("HardwareIds")
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun PreMain(contextResolver: ContentResolver, clazz: MainScreen) {

    val showDialog = remember { mutableStateOf(false) }
    val showLoading = remember { mutableStateOf(true) }
    val user = remember { mutableStateOf(User("", mutableListOf())) }
    val boards = remember { mutableStateOf(listOf<UserBoard>()) }

    //Fetch user from the database
    fetchUser(User(Settings.Secure.getString(contextResolver, Settings.Secure.ANDROID_ID), mutableListOf()),
        onFailure = {},
        onSuccess = {
            showLoading.value = false
            user.value = it
            boards.value = it.boards
        }
    )

    CreateDefaultScaffold(showLoading.value) {
        ShowMain(user, showDialog, showLoading,boards, clazz)
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ShowMain(
    user: MutableState<User>,
    showDialog: MutableState<Boolean>,
    showLoading: MutableState<Boolean>,
    boards: MutableState<List<UserBoard>>,
    clazz: MainScreen
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(mintGreen)
    ) {
        Image(
            painter = painterResource(id = R.drawable.image),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 25.dp)
                .height(110.dp)
        )

        BoxWithConstraints {
            Row(

                modifier = Modifier
                    .background(
                        color = darkGreen,
                        shape = RoundedCornerShape(0.dp, 15.dp, 15.dp, 0.dp)
                    )
                    .zIndex(1f)
                    .padding(10.dp, 5.dp)
            ) {
                Text(
                    text = " Systems        ",
                    style = titleExtraLarge,
                    color = Color.White,
                    modifier = Modifier.padding(20.dp, 3.dp, 30.dp, 3.dp)
                )
            }

            Column(
                content = {
                    Spacer(modifier = Modifier.height(25.dp))

                    Column(
                        modifier = Modifier
                            .background(bgGreen)
                            .fillMaxSize()
                    ) {
                        Spacer(modifier = Modifier.height(40.dp))

                        LazyColumn(
                            userScrollEnabled = true,
                            modifier = Modifier.padding(bottom = 65.dp)
                        ) {
                            items(boards.value) { board ->
                                SystemItem(name = board.name, id = board.board, user.value,clazz)
                            }
                        }

                    }
                }
            )

            Button(
                colors = ButtonDefaults.buttonColors(darkGreen),
                modifier = Modifier
                    .zIndex(1000f)
                    .padding(bottom = 60.dp, end = 10.dp)
                    .size(80.dp)
                    .align(Alignment.BottomEnd),
                onClick = {
                    showDialog.value = true
                }) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Ad",
                    tint = Color.White,
                    modifier = Modifier
                        .background(darkGreen)
                        .size(40.dp)
                )
            }
        }

        if (showDialog.value)
            SystemListDialog(user, showDialog, showLoading,boards,clazz)
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun SystemListDialog(
    user: MutableState<User>,
    showDialog: MutableState<Boolean>,
    showLoading: MutableState<Boolean>,
    boards: MutableState<List<UserBoard>>,
    clazz: MainScreen
) {
    var systemName by remember { mutableStateOf("") }
    var systemId by remember { mutableStateOf("") }
    var systemPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

    AlertDialog(
        containerColor = mintGreen,
        onDismissRequest = { showDialog.value = false },
        title = {
            Text(
                "Add System",
                color = darkGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
        },
        icon = {},
        text = {
            Column {
                systemName = TextBox(
                    value = "",
                    label = "Name",
                    password = false
                )
                systemId = TextBox(
                    value = "",
                    label = "ID",
                    password = false
                )

                systemPassword = TextBox(
                    value = "",
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
                onClick = { showDialog.value = false }) {
                Text("Back", color = Color.Black)
            }
        },
        confirmButton = {
            Button(colors = ButtonDefaults.buttonColors(
                containerColor = darkGreen
            ),
                onClick = {

                    if(systemName.isEmpty() || systemId.isEmpty())
                        return@Button

                    if (user.value.boards.find {
                            it.name == systemName || it.board == systemId
                        } != null) {
                        showDialog.value = false
                        return@Button
                    }

                    showLoading.value = true
                    fetchFindBoard(systemId,null,
                        onFailure = {
                            showDialog.value = false
                            showLoading.value = false
                            if(systemName.isEmpty() || systemId.isEmpty() || systemPassword.isEmpty())
                                return@fetchFindBoard
                            val intent = Intent(context, AddSystem::class.java).apply {
                                putExtra("systemName", systemName)
                                putExtra("systemId", systemId)
                                putExtra("systemPassword", systemPassword)
                                putExtra("user", Gson().toJson(user.value))
                            }

                            context.startActivity(intent)
                            clazz.finish()
                        },
                        onSuccess = {
                            fetchFindBoard(systemId,systemPassword,
                                onSuccess = {
                                    user.value.boards.add(UserBoard(systemId, systemName, true))
                                    updateUser(
                                        onFailure = {
                                            showLoading.value = false
                                            },
                                        onSuccess = {
                                            showLoading.value = false
                                            user.value = it
                                            boards.value = it.boards;
                                        }, user.value
                                    )

                                },
                                onFailure = {showDialog.value = false}
                            )

                        }
                    )

                    showDialog.value = false
                }) {
                Text("Add", color = Color.White)
            }
        }
    )
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun SystemNameDialog(showNameDialog: MutableState<Boolean>) {
    AlertDialog(
        containerColor = mintGreen,
        onDismissRequest = { showNameDialog.value = false },
        title = {
            Text(
                "Give a name",
                color = darkGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
        },
        icon = {},
        text = {
            Column {
                TextBox(
                    value = "",
                    label = "Name",
                    password = false
                )
            }
        },
        dismissButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray
                ),
                onClick = { showNameDialog.value = false }) {
                Text("Cancel", color = Color.Black)
            }
        },
        confirmButton = {
            Button(colors = ButtonDefaults.buttonColors(
                containerColor = darkGreen
            ),
                onClick = {
                    showNameDialog.value = false
                }) {
                Text("Add", color = Color.White)
            }
        }
    )
}

@Composable
fun SystemItem(name: String, id: String, user: User,clazz: MainScreen) {
    val context = LocalContext.current

    Surface(
        color = mintGreen,
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
                // Navigate to detail screen when clicked

                val intent = Intent(context, SystemStatus::class.java)
                    .apply {
                        putExtra("systemName", name)
                        putExtra("systemId", id)
                        putExtra("user", Gson().toJson(user))
                    }

                context.startActivity(intent)
                clazz.finish()
            }
    ) {

        Row(Modifier.padding(5.dp)) {
            Image(
                painter = painterResource(id = R.drawable.image01),
                contentDescription = "",
                modifier = Modifier
                    .padding(start = 10.dp)
                    .height(100.dp)
                    .width(100.dp)
            )

            Column(
                modifier =
                Modifier.padding(start = 16.dp, top = 30.dp)
            ) {
                Text(
                    text = name,
                    color = Color.Black,
                    style = titleMedium,
                )

                Text(
                    text = "ID: #$id",
                    color = Color.Black,
                    style = titleSmall,
                    modifier = Modifier.padding(start = 15.dp)
                )

            }
        }
    }
}

