package com.atek.smartble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.atek.smartble.databinding.ActivityMainBinding
import com.psp.bluetoothlibrary.Bluetooth
import com.psp.bluetoothlibrary.BluetoothListener
import com.psp.bluetoothlibrary.Connection
import java.util.*

@SuppressLint("MissingPermission")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var bluetooth = Bluetooth(this)
    private var connection = Connection(this)

    private lateinit var device: BluetoothDevice

    private lateinit var connectionListener: BluetoothListener.onConnectionListener
    private lateinit var receiverListener: BluetoothListener.onReceiveListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initListeners()
        //initServer()
        initClient()
        binding.ConnectBtn.setOnClickListener {
            try {
                connection.disconnect()
                val result = connection.connect(device, true, connectionListener, receiverListener)
                Toast.makeText(this, result.toString(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Log.e("Exception", "Already Connected")
            }
        }

        binding.SendMsgBtn.setOnClickListener {
            connection.send(binding.Message.text.toString())
        }

    }

    private fun initClient() {
        val devices = bluetooth.pairedDevices
        for (de in devices) {
            if (de.name == "ANDHERI") {
                device = de
            }
        }
    }

    private fun initServer() {
        try {
            connection.accept(true, connectionListener, receiverListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initListeners() {

        bluetooth.turnOnWithoutPermission()
        connection.setUUID(UUID.fromString("2464d2d0-4f9f-11ed-bdc3-0242ac120002"))

        connectionListener = object : BluetoothListener.onConnectionListener {
            override fun onConnectionStateChanged(socket: BluetoothSocket?, state: Int) {
                Toast.makeText(this@MainActivity, state.toString(), Toast.LENGTH_LONG).show()
            }

            override fun onConnectionFailed(errorCode: Int) {
                Toast.makeText(this@MainActivity, errorCode.toString(), Toast.LENGTH_LONG).show()
                connection.disconnect()
            }
        }
        receiverListener = BluetoothListener.onReceiveListener {
            Toast.makeText(this@MainActivity, it, Toast.LENGTH_LONG).show()
        }

    }

}