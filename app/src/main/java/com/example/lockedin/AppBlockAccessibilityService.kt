package com.example.lockedin

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.util.Log

class AppBlockAccessibilityService : AccessibilityService() {
    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("AppBlockService", "Accessibility service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            Log.d("Event occured!", "Something happened")
            if(it.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

                Log.d("Event type:", "TYPE_WINDOW_STATE_CHANGED")
                val currentPackage = it.packageName?.toString() ?: return
                if (isBlocked(currentPackage)) {
                    Log.d("Event type:", "App That opened is a blocked app")
                    // Launches the custom "you're locked out" activity
                    val intent = Intent(this, LockedOutActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    }
                    startActivity(intent)
                }
            }
        }
    }

    // Checks if a package is blocked
    private fun isBlocked(packageName: String): Boolean {
        val prefs = getSharedPreferences("blocked_apps", Context.MODE_PRIVATE)
        val blockedApps = prefs.getStringSet("apps", setOf()) ?: setOf()
        return blockedApps.contains(packageName)
    }

    override fun onInterrupt() {
        TODO("Not yet implemented")
    }
}