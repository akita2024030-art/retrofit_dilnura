package com.example.retrofit_9

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.retrofit_9.ui.theme.Retrofit_9Theme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Retrofit_9Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding),
                        onOpenWifiSettings = {
                            try {
                                startActivity(Intent(android.provider.Settings.ACTION_WIFI_SETTINGS))
                            } catch (e: Exception) {
                                startActivity(Intent(android.provider.Settings.ACTION_SETTINGS))
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onOpenWifiSettings: () -> Unit
) {
    val usersState by viewModel.usersState.collectAsState()
    val selectedUserState by viewModel.selectedUserState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lab 10: Retrofit GitHub Users") },
                actions = {
                    TextButton(onClick = { viewModel.loadTest404() }) {
                        Text("Test 404")
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = usersState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.data, key = { it.id }) { user ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.loadUserDetail(user.login) },
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = user.avatarUrl,
                                        contentDescription = "Avatar of ${user.login}",
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(MaterialTheme.shapes.small)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            text = user.login,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = "ID: ${user.id}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (state.isNetworkError) "No network" else state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        if (viewModel.retryCount < 3) {
                            Button(onClick = { viewModel.retry() }) {
                                Text("Retry (Attempt ${viewModel.retryCount}/3)")
                            }
                        } else {
                            Button(onClick = onOpenWifiSettings) {
                                Text("Open Wi-Fi settings")
                            }
                        }
                    }
                }
            }

            // User Detail Dialog
            selectedUserState?.let { detailState ->
                AlertDialog(
                    onDismissRequest = { viewModel.clearSelectedUser() },
                    title = { Text("User Details") },
                    text = {
                        when (detailState) {
                            is UiState.Loading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            is UiState.Success -> {
                                val user = detailState.data
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    AsyncImage(
                                        model = user.avatarUrl,
                                        contentDescription = "Avatar",
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(MaterialTheme.shapes.medium)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Login: ${user.login}", style = MaterialTheme.typography.titleMedium)
                                    Text("ID: ${user.id}", style = MaterialTheme.typography.bodyMedium)
                                    Text("Avatar URL: ${user.avatarUrl}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            is UiState.Error -> {
                                Text(
                                    text = if (detailState.isHttpError && detailState.httpCode == 404) {
                                        "Server error: 404"
                                    } else {
                                        detailState.message
                                    },
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { viewModel.clearSelectedUser() }) {
                            Text("Close")
                        }
                    }
                )
            }
        }
    }
}
