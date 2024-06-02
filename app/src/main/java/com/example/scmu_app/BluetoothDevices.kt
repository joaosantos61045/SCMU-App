package com.example.scmu_app

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.scmu_app.bluetooth.BLEGattCallback
import com.example.scmu_app.bluetooth.BLEManager

class BluetoothDevices : ComponentActivity() {

    private lateinit var bluetooth: BLEManager

    @SuppressLint("MissingPermission", "MutableCollectionMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bluetooth = BLEManager(this)
        enableEdgeToEdge()
        setContent {

            val context = LocalContext.current
            val list = remember { mutableStateOf(mutableMapOf<String, BluetoothDevice>()) }

            bluetooth.getScan().setOnResultChange {
                list.value = it.toMutableMap()
            }

            Column {
                Spacer(modifier = Modifier.size(20.dp, 20.dp))
                Text(bluetooth.isConnected().toString(), color = Color.Black)

                Button(onClick = {
                    bluetooth.start()
                    if (bluetooth.isConnected()) bluetooth.getScan().startDiscover()
                }) {
                    Text(text = "Start Discovery", color = Color.White)
                }

                Column {
                    for ((name, ble) in list.value) {
                        Button(onClick = {
                            ble.connectGatt(context, false, BLEGattCallback(sendMessage = {
                                "wifi arduino01 12345 {\"ssid\":\"MEO-6B144F\",\"pwd\":\"CA22156F4B\"}*"
                            }, onResponse = {
                                Log.w("PT-irineu", "Data received: $it")
                            })
                            )
                        }) {
                            Text(text = "$name", color = Color.Black)
                        }
                    }
                }

            }

        }

    }


}
