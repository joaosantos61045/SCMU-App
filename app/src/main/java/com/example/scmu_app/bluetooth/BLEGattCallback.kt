package com.example.scmu_app.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.os.Handler
import android.util.Log
import java.util.UUID

private const val SERVICE_UUID = "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
private const val CHARACTERISTIC_UUID = "beb5483e-36e1-4688-b7f5-ea07361b26a8"
private const val DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb"

@SuppressLint("MissingPermission")
class BLEGattCallback(
    val sendMessage : ()-> String,
    val onResponse : (Int) -> Unit
):  BluetoothGattCallback() {

    private lateinit var service : BluetoothGattService
    private lateinit var characteristic : BluetoothGattCharacteristic



    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            gatt.discoverServices()
        } else {
            onResponse.invoke(3)
            gatt.disconnect()
            gatt.close()
        }

    }

    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        super.onServicesDiscovered(gatt, status)

        if (status == BluetoothGatt.GATT_SUCCESS) {
            enableNotificationForCharacteristic(gatt)
        }

    }

    private fun enableNotificationForCharacteristic(gatt: BluetoothGatt) {
        service = gatt.getService(UUID.fromString(SERVICE_UUID))
        characteristic = service.getCharacteristic(UUID.fromString(CHARACTERISTIC_UUID))

        gatt.setCharacteristicNotification(characteristic, true);
        val descriptor = characteristic.getDescriptor(UUID.fromString(DESCRIPTOR_UUID))
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
        gatt.writeDescriptor(descriptor)

    }

    override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
        super.onDescriptorWrite(gatt, descriptor, status)
        if (status == BluetoothGatt.GATT_SUCCESS) {

            characteristic.let {
                it.value = sendMessage.invoke().toByteArray()
                gatt.writeCharacteristic(it)
            }
        }
    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        val receivedData = characteristic.value
        val receivedMessage = String(receivedData)

        try {
            val response = receivedMessage.toInt()
            onResponse.invoke(response)
            gatt.disconnect()
            gatt.close()
        }catch (_: Exception){}


    }

}