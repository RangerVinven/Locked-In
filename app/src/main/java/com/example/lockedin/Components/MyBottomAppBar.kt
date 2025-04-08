package com.example.lockedin.Components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.lockedin.AppListActivity
import com.example.lockedin.MainActivity
import com.example.lockedin.SettingsActivity

@Composable
fun MyBottomAppBar(context: Context) {
    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home icon: Launches MainActivity.
            IconButton(onClick = {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home"
                )
            }
            // Time icon: Launches AppListActivity.
            IconButton(onClick = {
                val intent = Intent(context, AppListActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Time"
                )
            }
            // Settings icon: Launches a Settings activity (if needed).
            IconButton(onClick = {
                // Uncomment if you add a SettingsActivity.
                 val intent = Intent(context, SettingsActivity::class.java)
                 context.startActivity(intent)
            }) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings"
                )
            }
        }
    }
}
