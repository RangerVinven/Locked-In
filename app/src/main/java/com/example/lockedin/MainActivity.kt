package com.example.lockedin

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
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
        topBar = { TopAppBar(title = { Text("Select Apps to Lock") }) }
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
                Button(
                    modifier = Modifier.padding(12.dp),
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

@Composable
fun AppItem(appInfo: ApplicationInfo, context: Context, selectedApps: MutableMap<String, Boolean>) {
    val pm = context.packageManager
    val appLabel = appInfo.loadLabel(pm).toString()
    // Track selection state for the current app
    var isSelected by remember { mutableStateOf(selectedApps[appInfo.packageName] ?: false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
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
}
