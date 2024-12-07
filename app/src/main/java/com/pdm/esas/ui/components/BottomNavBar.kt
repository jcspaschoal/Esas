package com.pdm.esas.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.pdm.esas.ui.navigation.Destination


data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: ImageVector
)


@Composable
fun BottomNavigationBar(
    navController: NavController,
    isAdmin: Boolean
) {
    val items = mutableListOf(
        BottomNavItem(
            name = "Tarefas",
            route = Destination.Task.route,
            icon = Icons.AutoMirrored.Filled.List
        ),
        BottomNavItem(
            name = "Relatórios",
            route = Destination.Report.route,
            icon = Icons.Default.Description
        )
    )

    if (isAdmin) {
        items.add(
            0,
            BottomNavItem(
                name = "Calendário",
                route = Destination.Calendar.route,
                icon = Icons.Default.CalendarToday
            )
        )
    }

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.name) },
                label = { Text(item.name) },
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                        }
                    }
                }
            )
        }
    }
}
