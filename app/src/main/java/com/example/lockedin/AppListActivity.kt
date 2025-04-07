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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class AppListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppListScreen(context = this)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListScreen(context: Context) {
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

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Select App") })
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(appsList) { app ->
                AppListItem(appInfo = app, context = context)
            }
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
                // When an item is tapped, launch TimeRangeActivity.
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