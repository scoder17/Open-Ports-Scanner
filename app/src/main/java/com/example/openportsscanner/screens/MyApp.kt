package com.example.openportsscanner.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.Socket

@Composable
fun MyApp(context: Context) {
//    var ipAddress by remember { mutableStateOf("172.25.44.225") }
    val ipAddress = remember { getIpAddress() }
    var scanResult by remember { mutableStateOf("") }
    var isScanning by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!isScanning) {
            isScanning = true
            scanPorts(ipAddress) { result ->
                scanResult = result
                isScanning = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
//        TextField(
//            value = ipAddress,
//            onValueChange = { ipAddress = it },
//            label = { Text("Enter IP address") },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 16.dp),
//            keyboardOptions = KeyboardOptions.Default.copy(
//                imeAction = ImeAction.Done
//            ),
//            keyboardActions = KeyboardActions(
//                onDone = {
//                    LocalSoftwareKeyboardController.current?.hide()
//                }
//            )
//        )

//        Button(
//            onClick = {
//                isScanning = false
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(48.dp)
//        ) {
//            Text(if (isScanning) "Scanning..." else "Scan Ports")
//        }

//        Spacer(modifier = Modifier.height(16.dp))

        Text("Your IP Address: $ipAddress")
        Text("Scan Result: $scanResult")
    }
}

@Composable
@Preview(showBackground = true)
fun MyAppPreview() {
    MyApp(LocalContext.current)
}

suspend fun scanPorts(ipAddress: String, callback: (String) -> Unit) {
    withContext(Dispatchers.IO) {
        val openPorts = mutableListOf<String>()
        for (port in 1..10000) {
            // You can adjust the range based on your needs
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress(ipAddress, port), 1000)
                socket.close()
                println("Connection successful")
                openPorts.add(port.toString())
            } catch (e: Exception) {
//                e.printStackTrace()
                println("Connection failed: ${e.message}")
                // Port is closed
            }
        }
        callback("Open ports: ${openPorts.joinToString(", ")}")
    }
}

fun getIpAddress(): String {
    try {
        val networkInterfaces = NetworkInterface.getNetworkInterfaces()
        while (networkInterfaces.hasMoreElements()) {
            val networkInterface = networkInterfaces.nextElement()
            val inetAddresses = networkInterface.inetAddresses
            while (inetAddresses.hasMoreElements()) {
                val address = inetAddresses.nextElement()
                if (!address.isLoopbackAddress && address is Inet4Address) {
                    return address.hostAddress
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return "Unknown"
}