package com.example.scmu_app

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.scmu_app.ui.theme.SCMUAppTheme

class EditSystem : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SCMUAppTheme {
                Scaffold(

                ) {


                    ShowManageSystemContent(true, " ", "")

                }
            }
        }
    }//onResume
}

@Preview(showBackground = true)
@Composable
fun EditSystemContentPreview() {
    SCMUAppTheme {
        ShowManageSystemContent(true,"", "")
    }
}
