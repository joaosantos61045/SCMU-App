package com.example.scmu_app


import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent

import androidx.compose.runtime.Composable

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.scmu_app.objects.User

import com.example.scmu_app.ui.theme.SCMUAppTheme
import com.example.scmu_app.ui.theme.bgGreen
import com.example.scmu_app.ui.theme.createDefaultScaffold
import com.example.scmu_app.ui.theme.createTile
import com.example.scmu_app.ui.theme.darkGreen
import com.example.scmu_app.ui.theme.mintGreen
import com.example.scmu_app.ui.theme.titleSmall
import com.example.scmu_app.ui.theme.titleMedium

class MainScreen : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()


        enableEdgeToEdge()
        setContent {
            SCMUAppTheme {
                createDefaultScaffold {
                    SystemsList(this.contentResolver)
                }
            }
        }
    }
}

@Composable
fun SystemsList(contextResolver: ContentResolver) {
    val context = LocalContext.current

    //Fetch user from the database
    val user = remember { mutableStateOf(User("", mutableListOf())) }
    fetchUser(contextResolver,
        onFailure = {},
        onSuccess = {user.value = it}
    )

    val showDialog = remember { mutableStateOf(false) }

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
                .height(90.dp)
        )


        BoxWithConstraints {
            createTile(text = "Systems:")

            Column(
                content = {
                    Spacer(modifier = Modifier.height(25.dp))

                    Column(
                        modifier = Modifier
                            .background(bgGreen)
                            .fillMaxSize()
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))
                        LazyColumn(
                            userScrollEnabled = true,
                            modifier = Modifier.padding(bottom = 65.dp)
                        ) {
                            items(user.value.boards) { system ->
                                SystemItem(name = system)
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

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text("Find System") },
                text = {
                    Column {
                        TextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("System name") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = "",
                            onValueChange = {},
                            label = { Text(" System Id") }
                        )
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog.value = false }) {
                        Text("Back")
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val intent = Intent(context, AddSystem::class.java)

                        //context.startActivity(intent)
                        user.value.boards.add(" System ${user.value.boards.size + 1}")
                        showDialog.value = false
                    }) {

                        Text("Add System")
                    }
                },
                icon = {
                    Button(onClick = {

                    }) {
                        Text("Bluetooth")
                    }
                }
            )
        }

    }
}


@Composable
fun SystemItem(name: String) {
    val context = LocalContext.current
    Surface(
        color = mintGreen,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
            .clickable {
                // Navigate to detail screen when clicked
                val intent = Intent(context, SystemStatus::class.java)

                context.startActivity(intent)
            }
    ) {

        Row() {
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
                Modifier.padding(start = 16.dp, top = 16.dp)
            ) {
                Text(
                    text = name,
                    color = Color.Black,
                    style = titleMedium,
                )

                Text(
                    text = "ID: #arduino01",
                    color = Color.Black,
                    style = titleSmall,
                    modifier = Modifier.padding(start = 15.dp)
                )

                Text(
                    text = "Next event in: x time",
                    color = darkGreen,
                    style = titleSmall,
                    modifier = Modifier.padding(start = 15.dp)
                )
            }
        }
    }
}

