package com.larrykin.chemistpos.core.networkUtils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ConnectivityReceiver(private val onConnected: () -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (NetworkUtils.isInternetAvailable(context)) {
            onConnected()
        }
    }
}