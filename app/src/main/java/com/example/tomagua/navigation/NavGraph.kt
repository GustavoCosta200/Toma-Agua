package com.example.tomagua.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tomagua.ui.configuration.ConfigurationReminderEditorScreen
import com.example.tomagua.ui.history.ConsumptionHistoryScreen
import com.example.tomagua.ui.home.HomeScreen
import com.example.tomagua.ui.profile.ProfileEditorScreen
import com.example.tomagua.ui.profile.ProfileListScreen

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Profiles : Screen("profiles")
    data object History : Screen("history")

    data object ProfileEditor : Screen("profile_editor?profileId={profileId}") {
        fun createRoute(profileId: Long? = null) = "profile_editor?profileId=${profileId ?: -1L}"
    }

    data object ReminderEditor : Screen("reminder_editor/{profileId}") {
        fun createRoute(profileId: Long) = "reminder_editor/$profileId"
    }
}

private val topLevelRoutes = setOf(Screen.Home.route, Screen.Profiles.route, Screen.History.route)

@Composable
fun TomaAguaNavHost(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in topLevelRoutes) {
                AppBottomBar(navController = navController, currentRoute = currentRoute)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToProfiles = { navController.navigate(Screen.Profiles.route) }
                )
            }

            composable(Screen.Profiles.route) {
                ProfileListScreen(
                    onNavigateToEditor = { profileId ->
                        navController.navigate(Screen.ProfileEditor.createRoute(profileId))
                    },
                    onNavigateToReminderEditor = { profileId ->
                        navController.navigate(Screen.ReminderEditor.createRoute(profileId))
                    }
                )
            }

            composable(
                route = Screen.ProfileEditor.route,
                arguments = listOf(
                    navArgument("profileId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) {
                ProfileEditorScreen(
                    onSaveComplete = { navController.popBackStack() },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.ReminderEditor.route,
                arguments = listOf(
                    navArgument("profileId") { type = NavType.LongType }
                )
            ) { entry ->
                val profileId = entry.arguments?.getLong("profileId") ?: return@composable
                ConfigurationReminderEditorScreen(
                    profileId = profileId,
                    onSaveComplete = { navController.popBackStack() },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.History.route) {
                ConsumptionHistoryScreen()
            }
        }
    }
}

@Composable
private fun AppBottomBar(navController: NavHostController, currentRoute: String?) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == Screen.Home.route,
            onClick = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Default.Home, contentDescription = "Início") },
            label = { Text("Início") }
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Profiles.route,
            onClick = {
                navController.navigate(Screen.Profiles.route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfis") },
            label = { Text("Perfis") }
        )
        NavigationBarItem(
            selected = currentRoute == Screen.History.route,
            onClick = {
                navController.navigate(Screen.History.route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Default.History, contentDescription = "Histórico") },
            label = { Text("Histórico") }
        )
    }
}