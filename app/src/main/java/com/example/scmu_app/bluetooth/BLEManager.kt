package com.example.scmu_app.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class BLEManager(
    private val activity: ComponentActivity
) {

    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mScanCallback: BLEScanCallback
    private var mBluetoothLeScanner: BluetoothLeScanner
    private var rpl: ActivityResultLauncher<Array<String>>
    private var bluetoothActivityResultLauncher: ActivityResultLauncher<Intent>
    private var REQUIRED_PERMISSIONS: Array<String>

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            REQUIRED_PERMISSIONS = arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE
            )
        } else {
            REQUIRED_PERMISSIONS = arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }

        mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner

        bluetoothActivityResultLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { }

        rpl = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { isGranted ->
            var granted = true
            for ((key, value) in isGranted)
                if (!value) granted = false
            if (granted) startbt()
        }

        mScanCallback = BLEScanCallback(mBluetoothLeScanner)
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
        else startbt()

    }

    private fun startbt() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null)
            return

        if (!mBluetoothAdapter!!.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            bluetoothActivityResultLauncher.launch(enableBtIntent)
        }
    }

    fun isConnected(): Boolean {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null)
            return false;
        return mBluetoothAdapter!!.isEnabled
    }

    fun getScan(): BLEScanCallback {
        return mScanCallback
    }
}