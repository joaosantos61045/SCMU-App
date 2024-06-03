package com.example.scmu_app.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService

class LOCManager(
    private val activity: ComponentActivity
) {

    private var locationManager:LocationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var isGpsEnabled: Boolean = false
    private var isNetworkEnabled: Boolean = false
    private var REQUIRED_PERMISSIONS: Array<String>
    private var rpl: ActivityResultLauncher<Array<String>>
    private var locationActivityResultLauncher: ActivityResultLauncher<Intent>

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            REQUIRED_PERMISSIONS = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            REQUIRED_PERMISSIONS = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }

        locationActivityResultLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { }

        rpl = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { isGranted ->
            var granted = true
            for ((key, value) in isGranted)
                if (!value) granted = false
            if (granted) startlc()
        }

    }

    private fun allPermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            )
                return false
        }
        return true
    }

    fun start() {
        if (isConnected())
            return

        if (!allPermissionsGranted()) rpl.launch(REQUIRED_PERMISSIONS)
        else startlc()

    }

    private fun startlc() {
        isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled && !isNetworkEnabled) {
            val enableLocationIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            locationActivityResultLauncher.launch(enableLocationIntent)
        }
    }

    fun isConnected(): Boolean {
        return isGpsEnabled && isNetworkEnabled
    }

}