package com.example.scmu_app.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.Handler
import android.util.Log

@SuppressLint("MissingPermission")
class BLEScanCallback(
    private val mBluetoothLeScanner: BluetoothLeScanner,
) : ScanCallback() {


    private val devices = mutableMapOf<String, BluetoothDevice>()
    private val mHandler = Handler()
    private var onResultChange: (MutableMap<String, BluetoothDevice>) -> Unit = {}
    private var discovering = false

    fun stopDiscover() {
        mBluetoothLeScanner.stopScan(this)
        discovering = false
    }

    @SuppressLint("MissingPermission")
    fun startDiscover() {
        val filters = mutableListOf<ScanFilter>()
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        devices.clear()
        onResultChange.invoke(devices)
        mBluetoothLeScanner.startScan(filters, settings, this)

        mHandler.postDelayed({
            mBluetoothLeScanner.stopScan(this)
            onResultChange.invoke(devices)
            discovering = false
        }, 2000)

        discovering = true
    }

    override fun onScanResult(callbackType: Int, result: ScanResult?) {
        super.onScanResult(callbackType, result)

        if (result != null && result.device != null) {
            val name = result.device.name

            if (!devices.containsKey(name)) {
                devices[name] = result.device
                onResultChange.invoke(devices)
            }
        }
    }

    override fun onBatchScanResults(results: List<ScanResult>) {
        var hasChanged = false
        for (result in results) {
            val name = result.device.name
            if (!devices.containsKey(name)) {
                devices[name] = result.device
                hasChanged = true
            }
        }

        if (hasChanged)
            onResultChange.invoke(devices)

        super.onBatchScanResults(results)
    }

    override fun onScanFailed(errorCode: Int) {
        discovering = false
        super.onScanFailed(errorCode)
    }

    fun isDiscovering(): Boolean {
        return discovering;
    }

    fun setOnResultChange(onResultChange : (MutableMap<String, BluetoothDevice>) -> Unit) {
        this.onResultChange = onResultChange
    }

}