package com.example.lockedin

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.Calendar

class TimeRangeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve extras passed from the previous activity
        val appName = intent.getStringExtra("appName") ?: "Unknown App"
        val packageName = intent.getStringExtra("packageName") ?: ""
        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                TimeRangeScreen(appName = appName, packageName = packageName)
            }
        }
    }
}

@Composable
fun TimeRangeScreen(appName: String, packageName: String) {
    val context = LocalContext.current
    // State variables for start and end times (as display strings)
    var startTime by remember { mutableStateOf("Select Start Time") }
    var endTime by remember { mutableStateOf("Select End Time") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display the app's name at the top
        Text(text = appName, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        // Button to select the start time
        Button(onClick = {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    startTime = String.format("%02d:%02d", hourOfDay, minute)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }) {
            Text(text = startTime)
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Button to select the end time
        Button(onClick = {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    endTime = String.format("%02d:%02d", hourOfDay, minute)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }) {
            Text(text = endTime)
        }
        Spacer(modifier = Modifier.height(32.dp))
        // Button to save the time range
        Button(onClick = {
            // Save the time range to SharedPreferences for app lock times.
            val appLockPrefs = context.getSharedPreferences("app_lock_times", Context.MODE_PRIVATE)
            appLockPrefs.edit().putString("time_range_$packageName", "$startTime-$endTime").apply()

            // Also add the app to the blocked apps list.
            val blockedPrefs = context.getSharedPreferences("blocked_apps", Context.MODE_PRIVATE)
            val currentBlockedApps = blockedPrefs.getStringSet("apps", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
            currentBlockedApps.add(packageName)
            blockedPrefs.edit().putStringSet("apps", currentBlockedApps).apply()

            Toast.makeText(context, "Time range saved", Toast.LENGTH_SHORT).show()
        }) {
            Text("Save Time Range")
        }
    }
}
