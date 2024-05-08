package com.example.scmu_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.scmu_app.ui.theme.SCMUAppTheme

class MainActivity2 : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SCMUAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    AddSystemContent(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSystemContent(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(modifier) {
        TopAppBar(
            title = { Text("Add System") },
            navigationIcon = {
                IconButton(onClick = {
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Go back")
                }
            }
        )

        Image(
            painter = painterResource(id = R.drawable.image), // Replace 'your_image_resource' with your image resource ID
            contentDescription = null, // Add appropriate content description
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )

        Subheader("General")
        TextBox("WiFi")
        TextBox("House Name")

        Subheader("Schedule")
        Dropdown("Duration", listOf("1 hour", "2 hours", "3 hours")) // Replace with appropriate duration options
        Dropdown("Hour", listOf("9:00 AM", "10:00 AM", "11:00 AM")) // Replace with appropriate hour options
        Subsubheader("Rotation")
        // Add rotation content here
    }
}

@Composable
fun Subheader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
    )
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

@Composable
fun Dropdown(label: String, options: List<String>) {
    var selectedIndex by remember { mutableStateOf(0) }

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.titleSmall)
        DropdownMenu(
            expanded = false,
            onDismissRequest = { },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    { Text(text = option) },
                    onClick = {
                        selectedIndex = index
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
        AddSystemContent()
    }
}
