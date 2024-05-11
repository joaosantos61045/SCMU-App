package com.example.scmu_app


import android.annotation.SuppressLint
import android.content.Intent

import androidx.compose.runtime.Composable

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.example.scmu_app.ui.theme.SCMUAppTheme

class MainActivity : ComponentActivity() {

    var systems : MutableList<String> = mutableListOf()
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SCMUAppTheme {
                Scaffold {
                    SystemsList(systems)
                }
            }
        }

    }


}


@Composable
fun SystemsList(systems: MutableList<String>) {

   // var count =cout
  //  var systems by remember { mutableStateOf<List<String>>(listOf()) }

    val showDialog = remember { mutableStateOf(false) }




    Column(modifier = Modifier.padding(16.dp)) {
        // Image at the top
        Image(
            painter = painterResource(id = R.drawable.image), // Replace with your image resource
            contentDescription = "Your Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        // Header
        Text(
            text = "Systems",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Display the list of systems
        LazyColumn {
            items(systems) { system ->
                SystemItem(name = system)
            }
        }
        // Add button to add a new system
        Button(onClick = {
            showDialog.value = true

        }) {
            Text("Add System")
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

                        systems.add(" System ${systems.size+1}")
                        showDialog.value = false}) {

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
        color = Color(0xFF98FB98),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                // Navigate to detail screen when clicked
                val intent = Intent(context, MainActivity2::class.java)

                context.startActivity(intent)
            }
    ) {
        Text(
            text = name,
            modifier = Modifier.padding(16.dp)
        )
    }
}
/*
@Preview(showBackground = true)
@Composable
fun SystemsListPreview() {
    SCMUAppTheme {
        SystemsList(systems)
    }
}*/
