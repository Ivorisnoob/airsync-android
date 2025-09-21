package com.sameerasw.airsync.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.sameerasw.airsync.data.local.DataStoreManager
import com.sameerasw.airsync.utils.DeviceInfoUtil
import com.sameerasw.airsync.utils.NotificationUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Receives notification actions and updates sync/connection state and notifications.
 */
class NotificationActionReceiver : BroadcastReceiver() {

    companion object {
        // New: Continue Browsing dismiss action
        const val ACTION_CONTINUE_BROWSING_DISMISS = "com.sameerasw.airsync.CONTINUE_BROWSING_DISMISS"
        private const val TAG = "NotificationActionReceiver"
    }

    /** Coroutine scope for background operations. */
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_CONTINUE_BROWSING_DISMISS -> {
                val notifId = intent.getIntExtra("notif_id", -1)
                if (notifId != -1) {
                    try {
                        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
                        nm.cancel(notifId)
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to cancel continue-browsing notif: ${e.message}")
                    }
                }
            }
        }
    }
}
