package com.pdm.esas.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.pdm.esas.ui.navigation.Destination


data class BottomNavItem(
    val route: String,
    val icon: ImageVector
)

@Composable
fun BottomNavigationBar(
    navController: NavController,
    userRoles: List<String>
) {
    val navigableDestinations = listOf(
        //Destination.TaskDetail,
        Destination.Report,
        Destination.Calendar,
        //Destination.Visitors,
        Destination.Donations,
        Destination.Visit
    )

    val items = navigableDestinations.filter { destination ->
        Destination.hasAccess(destination.requiredRoles, userRoles)
    }.map { destination ->
        BottomNavItem(
            route = destination.route,
            icon = when (destination) {
                Destination.Donations -> Icons.Default.VolunteerActivism
                //Destination.Visitors -> Icons.Default.Description
                //Destination.TaskDetail -> Icons.AutoMirrored.Filled.List
                Destination.Report -> Icons.Default.QueryStats
                Destination.Calendar -> Icons.Default.CalendarMonth
                Destination.Visit -> Icons.Default.People
                else -> Icons.AutoMirrored.Filled.Help
            }
        )
    }

    NavigationBar(
        modifier = Modifier
            .padding(bottom = 0.dp)
            .height(70.dp),
        containerColor = MaterialTheme.colorScheme.surface, // Cor de fundo da barra
        contentColor = MaterialTheme.colorScheme.onSurface // Cor padrão dos ícones
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(top = 8.dp),
                        tint = if (selected) {
                            MaterialTheme.colorScheme.primary // Ícone selecionado
                        } else {
                            MaterialTheme.colorScheme.onSurface // Ícone não selecionado
                        }
                    )
                },
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
                },
                colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                alwaysShowLabel = false
            )
        }
    }
}

