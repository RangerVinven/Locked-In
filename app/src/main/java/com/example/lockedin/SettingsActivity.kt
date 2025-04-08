package com.example.lockedin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lockedin.Components.MyBottomAppBar

data class SettingsCategory(val title: String, val description: String, val route: String)

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SettingsScreen(context = this)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(context: Context) {
    // Define a list of settings categories.
    val settingsCategories = listOf(
        SettingsCategory(
            title = "Account Details",
            description = "View and edit your account details",
            route = "account_details"
        ),
        SettingsCategory(
            title = "Manage My Subscription",
            description = "View and manage your subscription details",
            route = "manage_subscription"
        )
        // Add more categories here as needed.
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text("Select Apps to Lock") }) },
        bottomBar = { MyBottomAppBar(context = context) }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(settingsCategories) { category ->
                SettingsItem(category = category, context = context)
                Divider()
            }
        }
    }
}

@Composable
fun SettingsItem(category: SettingsCategory, context: Context) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // Launch the corresponding activity based on the route.
                when (category.route) {
                    "account_details" -> {
                        // For now, we launch a dummy AccountDetailsActivity.
//                        val intent = Intent(context, AccountDetailsActivity::class.java)
//                        context.startActivity(intent)
                    }
                    "manage_subscription" -> {
                        val intent = Intent(context, ManageSubscriptionActivity::class.java)
                        context.startActivity(intent)
                    }
                }
            }
            .padding(16.dp)
    ) {
        Text(text = category.title)
        Text(text = category.description)
    }
}
