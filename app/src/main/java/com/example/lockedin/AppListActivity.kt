package com.example.lockedin

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class AppListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppListScaffold(context = this)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListScaffold(context: Context) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Select App") }) },
        bottomBar = { MyBottomAppBar(context = context) }
    ) { innerPadding ->
        AppListScreen(
            context = context,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun AppListScreen(context: Context, modifier: Modifier = Modifier) {
    // State holding the list of installed (third‑party) apps.
    val appsList = remember { mutableStateListOf<ApplicationInfo>() }

    // Load installed apps on first composition.
    LaunchedEffect(Unit) {
        val pm = context.packageManager
        val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val thirdPartyApps = installedApps.filter { app ->
            // Filter out system apps (except updated ones)
            (app.flags and ApplicationInfo.FLAG_SYSTEM) == 0 ||
                    (app.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
        }
        appsList.addAll(thirdPartyApps)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        items(appsList) { app ->
            AppListItem(appInfo = app, context = context)
        }
    }
}

@Composable
fun AppListItem(appInfo: ApplicationInfo, context: Context) {
    val pm = context.packageManager
    val appName = appInfo.loadLabel(pm).toString()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                // Launch TimeRangeActivity with the selected app's info.
                val intent = Intent(context, TimeRangeActivity::class.java).apply {
                    putExtra("appName", appName)
                    putExtra("packageName", appInfo.packageName)
                }
                context.startActivity(intent)
            }
    ) {
        Text(text = appName)
    }
}
