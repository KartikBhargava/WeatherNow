// Update: presentation/screens/settings/SettingsScreen.kt
package bhargava.kartik.weatherdashboard.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bhargava.kartik.weatherdashboard.presentation.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Units Section
            SettingsSection(title = "Units") {
                SettingsDropdownItem(
                    title = "Temperature",
                    subtitle = "Choose temperature unit",
                    selectedValue = uiState.temperatureUnit.displayName,
                    options = viewModel.getTemperatureUnitOptions(),
                    onValueChange = { viewModel.updateTemperatureUnitByName(it) },
                    icon = Icons.Default.Thermostat
                )

                SettingsDropdownItem(
                    title = "Wind Speed",
                    subtitle = "Choose wind speed unit",
                    selectedValue = uiState.windSpeedUnit.displayName,
                    options = viewModel.getWindSpeedUnitOptions(),
                    onValueChange = { viewModel.updateWindSpeedUnitByName(it) },
                    icon = Icons.Default.Air
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Notifications Section
            SettingsSection(title = "Notifications") {
                SettingsSwitchItem(
                    title = "Weather Alerts",
                    subtitle = "Get notified about weather changes",
                    checked = uiState.notificationsEnabled,
                    onCheckedChange = { viewModel.toggleNotifications(it) },
                    icon = Icons.Default.Notifications
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Privacy Section
            SettingsSection(title = "Privacy") {
                SettingsSwitchItem(
                    title = "Location Access",
                    subtitle = "Allow app to access your location",
                    checked = uiState.locationEnabled,
                    onCheckedChange = { viewModel.toggleLocationAccess(it) },
                    icon = Icons.Default.LocationOn
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Appearance Section
            SettingsSection(title = "Appearance") {
                SettingsSwitchItem(
                    title = "Dark Mode",
                    subtitle = "Use dark theme",
                    checked = uiState.darkModeEnabled,
                    onCheckedChange = { viewModel.toggleDarkMode(it) },
                    icon = Icons.Default.DarkMode
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Data Section
            SettingsSection(title = "Data") {
                SettingsClickableItem(
                    title = "Reset All Settings",
                    subtitle = "Restore default settings",
                    icon = Icons.Default.Restore,
                    onClick = { viewModel.showResetDialog() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // About Section
            SettingsSection(title = "About") {
                SettingsClickableItem(
                    title = "App Version",
                    subtitle = uiState.appVersion,
                    icon = Icons.Default.Info,
                    onClick = { }
                )

                SettingsClickableItem(
                    title = "Privacy Policy",
                    subtitle = "View our privacy policy",
                    icon = Icons.Default.PrivacyTip,
                    onClick = { /* TODO: Open privacy policy */ }
                )

                SettingsClickableItem(
                    title = "Terms of Service",
                    subtitle = "View terms and conditions",
                    icon = Icons.Default.Description,
                    onClick = { /* TODO: Open terms */ }
                )

                SettingsClickableItem(
                    title = "Rate App",
                    subtitle = "Rate us on Play Store",
                    icon = Icons.Default.Star,
                    onClick = { /* TODO: Open Play Store rating */ }
                )
            }

            Spacer(modifier = Modifier.height(100.dp)) // Space for bottom nav
        }

        // Reset dialog
        if (uiState.showResetDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.hideResetDialog() },
                title = { Text("Reset All Settings", color = Color.Black) },
                text = {
                    Text(
                        "This will restore all settings to their default values. This action cannot be undone.",
                        color = Color.Black
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.resetAllSettings() }
                    ) {
                        Text("Reset", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.hideResetDialog() }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.15f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(4.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun SettingsSwitchItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color.White.copy(alpha = 0.3f),
                uncheckedThumbColor = Color.White.copy(alpha = 0.7f),
                uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
            )
        )
    }
}

@Composable
fun SettingsDropdownItem(
    title: String,
    subtitle: String,
    selectedValue: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    icon: ImageVector
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }

        Box {
            TextButton(onClick = { expanded = true }) {
                Text(selectedValue, color = Color.White)
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                color = if (option == selectedValue) {
                                    Color(0xFF6366F1)
                                } else {
                                    Color.Black
                                }
                            )
                        },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        },
                        leadingIcon = if (option == selectedValue) {
                            {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color(0xFF6366F1),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else null
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsClickableItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }

        IconButton(onClick = onClick) {
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = "Open",
                tint = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2)
                    )
                )
            )
    ) {
        // Preview content
    }
}