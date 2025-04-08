package com.example.lockedin

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.lockedin.Components.MyBottomAppBar
import java.util.Calendar

class TimeRangeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve extras passed from the previous activity.
        val appName = intent.getStringExtra("appName") ?: "Unknown App"
        val packageName = intent.getStringExtra("packageName") ?: ""
        setContent {
            TimeRangeScaffold(context = this, appName = appName, packageName = packageName)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeRangeScaffold(context: Context, appName: String, packageName: String) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Set Time Range") }) },
        bottomBar = { MyBottomAppBar(context = context) }
    ) { innerPadding ->
        TimeRangeScreen(
            appName = appName,
            packageName = packageName,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun TimeRangeScreen(appName: String, packageName: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // State variables for start and end times (as display strings)
    var startTime by remember { mutableStateOf("Select Start Time") }
    var endTime by remember { mutableStateOf("Select End Time") }

    // Load saved time range if it exists.
    LaunchedEffect(packageName) {
        val prefs = context.getSharedPreferences("app_lock_times", Context.MODE_PRIVATE)
        val savedTimeRange = prefs.getString("time_range_$packageName", null)
        if (savedTimeRange != null) {
            val parts = savedTimeRange.split("-")
            if (parts.size == 2) {
                startTime = parts[0]
                endTime = parts[1]
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display the app's name at the top.
        Text(text = appName, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        // Button to select the start time.
        Button(
            onClick = {
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
            },
            shape = RectangleShape
        ) {
            Text(text = startTime)
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Button to select the end time.
        Button(
            onClick = {
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
            },
            shape = RectangleShape
        ) {
            Text(text = endTime)
        }
        Spacer(modifier = Modifier.height(32.dp))
        // Button to save the time range and block the app.
        Button(
            onClick = {
                // Save the time range to SharedPreferences.
                val appLockPrefs = context.getSharedPreferences("app_lock_times", Context.MODE_PRIVATE)
                appLockPrefs.edit().putString("time_range_$packageName", "$startTime-$endTime").apply()

                // Also add the app to the blocked apps list.
                val blockedPrefs = context.getSharedPreferences("blocked_apps", Context.MODE_PRIVATE)
                val currentBlockedApps =
                    blockedPrefs.getStringSet("apps", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
                currentBlockedApps.add(packageName)
                blockedPrefs.edit().putStringSet("apps", currentBlockedApps).apply()

                Toast.makeText(context, "Time range saved and app blocked", Toast.LENGTH_SHORT).show()
            },
            shape = RectangleShape
        ) {
            Text("Save Time Range")
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Button to reset or remove the time limit for the app.
        Button(
            onClick = {
                // Remove the time range for this app.
                val appLockPrefs = context.getSharedPreferences("app_lock_times", Context.MODE_PRIVATE)
                appLockPrefs.edit().remove("time_range_$packageName").apply()

                // Also remove the app from the blocked apps list.
                val blockedPrefs = context.getSharedPreferences("blocked_apps", Context.MODE_PRIVATE)
                val currentBlockedApps =
                    blockedPrefs.getStringSet("apps", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
                currentBlockedApps.remove(packageName)
                blockedPrefs.edit().putStringSet("apps", currentBlockedApps).apply()

                // Reset the displayed times.
                startTime = "Select Start Time"
                endTime = "Select End Time"

                Toast.makeText(context, "Time limit removed", Toast.LENGTH_SHORT).show()
            },
            shape = RectangleShape
        ) {
            Text("Reset Time Limit")
        }
    }
}
