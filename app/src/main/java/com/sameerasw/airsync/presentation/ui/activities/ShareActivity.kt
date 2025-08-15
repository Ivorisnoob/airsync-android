package com.sameerasw.airsync.presentation.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.sameerasw.airsync.data.local.DataStoreManager
import com.sameerasw.airsync.utils.ClipboardSyncManager
import com.sameerasw.airsync.utils.WebSocketUtil
import com.sameerasw.airsync.utils.FileSender
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ShareActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when (intent?.action) {
            Intent.ACTION_SEND -> {
                if (intent.type == "text/plain") {
                    handleTextShare(intent)
                } else {
                    // Try to handle file share
                    val stream = intent.getParcelableExtra<android.net.Uri>(Intent.EXTRA_STREAM)
                    if (stream != null) {
                        handleFileShare(stream)
                    }
                }
            }
        }
    }

    private fun handleTextShare(intent: Intent) {
        lifecycleScope.launch {
            try {
                val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                if (sharedText != null) {
                    val dataStoreManager = DataStoreManager(this@ShareActivity)

                    // Try to connect if not already connected
                    if (!WebSocketUtil.isConnected()) {
                        val ipAddress = dataStoreManager.getIpAddress().first()
                        val port = dataStoreManager.getPort().first().toIntOrNull() ?: 6996
                        val lastConnectedDevice = dataStoreManager.getLastConnectedDevice().first()
                        val symmetricKey = lastConnectedDevice?.symmetricKey

                        WebSocketUtil.connect(
                            context = this@ShareActivity,
                            ipAddress = ipAddress,
                            port = port,
                            symmetricKey = symmetricKey,
                            onConnectionStatus = { connected ->
                                if (connected) {
                                    // Send text after connection
                                    ClipboardSyncManager.syncTextToDesktop(sharedText)
                                    showToast("Text shared to PC")
                                } else {
                                    showToast("Failed to connect to PC")
                                }
                                finish()
                            },
                            onMessage = { }
                        )
                    } else {
                        // Already connected, send directly
                        ClipboardSyncManager.syncTextToDesktop(sharedText)
                        showToast("Text shared to PC")
                        finish()
                    }
                } else {
                    showToast("No text to share")
                    finish()
                }
            } catch (e: Exception) {
                showToast("Failed to share text: ${e.message}")
                finish()
            }
        }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleFileShare(uri: android.net.Uri) {
        lifecycleScope.launch {
            try {
                val dataStoreManager = DataStoreManager(this@ShareActivity)

                if (!WebSocketUtil.isConnected()) {
                    val ipAddress = dataStoreManager.getIpAddress().first()
                    val port = dataStoreManager.getPort().first().toIntOrNull() ?: 6996
                    val lastConnectedDevice = dataStoreManager.getLastConnectedDevice().first()
                    val symmetricKey = lastConnectedDevice?.symmetricKey

                    WebSocketUtil.connect(
                        context = this@ShareActivity,
                        ipAddress = ipAddress,
                        port = port,
                        symmetricKey = symmetricKey,
                        onConnectionStatus = { connected ->
                            if (connected) {
                                FileSender.sendFile(this@ShareActivity, uri)
                                showToast("File shared to Mac")
                            } else {
                                showToast("Failed to connect to Mac")
                            }
                            finish()
                        },
                        onMessage = { }
                    )
                } else {
                    FileSender.sendFile(this@ShareActivity, uri)
                    showToast("File shared to Mac")
                    finish()
                }
            } catch (e: Exception) {
                showToast("Failed to share file: ${e.message}")
                finish()
            }
        }
    }
}
