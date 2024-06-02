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
import com.example.scmu_app.bluetooth.BLEManager
import com.example.scmu_app.others.Board
import com.example.scmu_app.others.User
import com.example.scmu_app.others.UserBoard
import com.example.scmu_app.ui.theme.CreateDefaultScaffold
import com.example.scmu_app.ui.theme.SCMUAppTheme
import com.example.scmu_app.ui.theme.createTile
import com.example.scmu_app.ui.theme.darkGreen
import com.example.scmu_app.ui.theme.mintGreen
import com.example.scmu_app.ui.theme.swampGreen
import com.example.scmu_app.ui.theme.titleExtraLarge
import com.example.scmu_app.ui.theme.titleMedium
import com.google.gson.Gson
import okhttp3.internal.wait


class AddSystem : ComponentActivity() {


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bluetooth = BLEManager(this)
        enableEdgeToEdge()
        setContent {
            SCMUAppTheme {

                val systemName = intent.getStringExtra("systemName")!!
                val sysId = intent.getStringExtra("systemId")!!
                val user = Gson().fromJson(intent.getStringExtra("user"), User::class.java)
                val board = Board(
                    sysId,
                    true,
                    null,
                    null,
                    mutableListOf(false, false, false, false, false, false, false),
                    0,
                    0,
                    0,
                    0,
                    0
                )
                MyAppContent(user, systemName, sysId, board, bluetooth, false, this)
            }
        }
    }
}

