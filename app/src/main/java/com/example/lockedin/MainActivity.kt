package com.example.lockedin

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the content view with Compose
        setContent {
            MyAppContent(context = this)
        }
    }
}

@Composable
fun MyBottomAppBar(context: Context) {
    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home icon: Launches MainActivity or a Home activity.
            IconButton(onClick = {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home"
                )
            }
            IconButton(onClick = {
                // val intent = Intent(context, SettingsActivity::class.java)
                // context.startActivity(intent)
            }) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Time"
                )
            }
            // Settings icon: Launches a Settings activity.
            IconButton(onClick = {
                // val intent = Intent(context, SettingsActivity::class.java)
                // context.startActivity(intent)
            }) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppContent(context: Context) {
    // State holding the list of third-party installed apps
    val appsList = remember { mutableStateOf<List<ApplicationInfo>>(emptyList()) }
    // State for selected apps; maps package names to a boolean (selected or not)
    val selectedApps = remember { mutableStateMapOf<String, Boolean>() }

    // Load installed apps and previously selected apps on first composition
    LaunchedEffect(Unit) {
        val pm = context.packageManager
        val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        // Filter out system apps (except updated ones)
        val thirdPartyApps = installedApps.filter { app ->
            ((app.flags and ApplicationInfo.FLAG_SYSTEM) == 0 ||
                    (app.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) &&
                    (app.packageName != "com.example.lockedin")
        }
        appsList.value = thirdPartyApps

        // Load saved selections from SharedPreferences
        val prefs = context.getSharedPreferences("blocked_apps", Context.MODE_PRIVATE)
        val savedApps = prefs.getStringSet("apps", setOf()) ?: setOf()
        thirdPartyApps.forEach { app ->
            if (savedApps.contains(app.packageName)) {
                selectedApps[app.packageName] = true
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Select Apps to Lock") }) },
        bottomBar = {
            MyBottomAppBar(context)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(appsList.value) { app ->
                AppItem(appInfo = app, context = context, selectedApps = selectedApps)
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        modifier = Modifier.padding(12.dp),
                        shape = RectangleShape,
                        onClick = {
                            // Gets the package names for selected apps
                            val blockedApps = selectedApps.filter { it.value }.keys
                            // Save the selections in SharedPreferences
                            val prefs = context.getSharedPreferences("blocked_apps", Context.MODE_PRIVATE)
                            prefs.edit().putStringSet("apps", blockedApps.toSet()).apply()
                            println("Blocked apps updated: $blockedApps")
                        }
                    ) {
                        Text("Lock Apps")
                    }
                }
            }
        }
    }
}

@Composable
fun AppItem(appInfo: ApplicationInfo, context: Context, selectedApps: MutableMap<String, Boolean>) {
    val pm = context.packageManager
    val appLabel = appInfo.loadLabel(pm).toString()
    // Track selection state for the current app
    var isSelected by remember { mutableStateOf(selectedApps[appInfo.packageName] ?: false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = appLabel, modifier = Modifier.weight(1f))
            Checkbox(
                checked = isSelected,
                onCheckedChange = { checked ->
                    isSelected = checked
                    selectedApps[appInfo.packageName] = checked
                }
            )
        }
        HorizontalDivider()
    }
}
