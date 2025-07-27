package bhargava.kartik.weatherdashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import bhargava.kartik.weatherdashboard.presentation.navigation.WeatherDestination
import bhargava.kartik.weatherdashboard.presentation.navigation.WeatherNavigation
import bhargava.kartik.weatherdashboard.presentation.viewmodel.SettingsViewModel
import bhargava.kartik.weatherdashboard.ui.theme.WeatherDashboardTheme
import bhargava.kartik.weatherdashboard.utils.ThemeUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen before super.onCreate()
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Get settings at the top level to apply theme changes
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()

            WeatherDashboardTheme(
                darkTheme = settingsState.darkModeEnabled,
                dynamicColor = true
            ) {
                WeatherDashboardApp()
            }
        }
    }
}

@Composable
fun WeatherDashboardApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Get settings at the app level to ensure they're shared
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val isDarkMode = settingsState.darkModeEnabled

    val navigationItems = listOf(
        WeatherDestination.Home,
        WeatherDestination.Forecast,
        WeatherDestination.Locations,
        WeatherDestination.Settings
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Clean, minimal bottom navigation with dark mode support
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = ThemeUtils.getNavigationBackground(isDarkMode),
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    navigationItems.forEach { destination ->
                        val isSelected = currentDestination?.hierarchy?.any {
                            it.route == destination.route
                        } == true

                        NavigationItem(
                            destination = destination,
                            isSelected = isSelected,
                            isDarkMode = isDarkMode,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        // Dynamic background based on dark mode
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = ThemeUtils.getBackgroundGradient(isDarkMode)
                    )
                )
        ) {
            WeatherNavigation(
                navController = navController,
                settingsViewModel = settingsViewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun NavigationItem(
    destination: WeatherDestination,
    isSelected: Boolean,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    // Adjust colors based on theme
    val selectedColor = ThemeUtils.getTextPrimary(isDarkMode)
    val unselectedColor = ThemeUtils.getTextTertiary(isDarkMode)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            destination.icon,
            contentDescription = destination.title,
            tint = if (isSelected) selectedColor else unselectedColor,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = destination.title,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) selectedColor else unselectedColor,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )

        // Active indicator dot
        if (isSelected) {
            Spacer(modifier = Modifier.height(3.dp))
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(selectedColor, CircleShape)
            )
        }
    }
}