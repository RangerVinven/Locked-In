package com.example.lockedin

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.util.Log
import java.util.Calendar

class AppBlockAccessibilityService : AccessibilityService() {
    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("AppBlockService", "Accessibility service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            if (it.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                val currentPackage = it.packageName?.toString() ?: return
                Log.d("AppBlockService", "Current package: $currentPackage")
                if (isBlocked(currentPackage)) {
                    val timeRange = getTimeRangeForPackage(currentPackage)
                    Log.d("AppBlockService", "Retrieved time range: $timeRange")
                    if (timeRange != null && isCurrentTimeWithinRange(timeRange)) {
                        Log.d("AppBlockService", "Time is within range; launching LockedOutActivity")
                        val intent = Intent(this, LockedOutActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        }
                        startActivity(intent)
                    } else {
                        Log.d("AppBlockService", "Current time is not within range")
                    }
                } else {
                    Log.d("AppBlockService", "App is not blocked")
                }
            }
        }
    }


    private fun getTimeRangeForPackage(packageName: String): Pair<Int, Int>? {
        // Returns the start and end times in minutes since midnight.
        val prefs = getSharedPreferences("app_lock_times", Context.MODE_PRIVATE)
        val timeRangeStr = prefs.getString("time_range_$packageName", null) ?: return null
        // Expected format "HH:mm-HH:mm"
        val parts = timeRangeStr.split("-")
        if (parts.size != 2) return null

        val startParts = parts[0].split(":")
        val endParts = parts[1].split(":")
        if (startParts.size != 2 || endParts.size != 2) return null

        return try {
            val startHour = startParts[0].toInt()
            val startMinute = startParts[1].toInt()
            val endHour = endParts[0].toInt()
            val endMinute = endParts[1].toInt()
            val startTotal = startHour * 60 + startMinute
            val endTotal = endHour * 60 + endMinute
            Pair(startTotal, endTotal)
        } catch (e: NumberFormatException) {
            null
        }
    }

    private fun isCurrentTimeWithinRange(timeRange: Pair<Int, Int>): Boolean {
        val now = Calendar.getInstance()
        val currentTotal = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        // This check assumes that the start and end times are on the same day.
        return currentTotal in timeRange.first until timeRange.second
    }


    // Checks if a package is blocked
    private fun isBlocked(packageName: String): Boolean {
        val prefs = getSharedPreferences("blocked_apps", Context.MODE_PRIVATE)
        val blockedApps = prefs.getStringSet("apps", setOf()) ?: setOf()
        Log.d("AppBlockService", "Blocked apps: $blockedApps")
        Log.d("AppBlockService", "Checking package: $packageName")
        return blockedApps.contains(packageName)
    }


    override fun onInterrupt() {
        TODO("Not yet implemented")
    }
}