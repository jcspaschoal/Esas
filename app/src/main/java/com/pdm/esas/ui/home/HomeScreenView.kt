package com.pdm.esas.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pdm.esas.ui.calendar.CalendarView
import com.pdm.esas.ui.components.BottomNavigationBar
import com.pdm.esas.ui.navigation.Destination
import com.pdm.esas.ui.report.ReportView
import com.pdm.esas.ui.tasks.TaskView

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = hiltViewModel()
    val userRoles by homeViewModel.userRoles.collectAsState()

    if (userRoles.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        val shouldShowBottomBar = currentRoute != Destination.Calendar.route

        Scaffold(
            bottomBar = {
                if (shouldShowBottomBar) {
                    BottomNavigationBar(
                        navController = navController, userRoles = userRoles
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Destination.TaskDetail.route,
                modifier = modifier.padding(innerPadding)
            ) {
                Destination.Calendar.takeIf { Destination.hasAccess(it.requiredRoles, userRoles) }
                    ?.let {
                        composable(it.route) {
                            CalendarView(
                                modifier = Modifier.fillMaxSize(),
                                onBackClick = { navController.popBackStack() } // Configuração para voltar
                            )
                        }
                    }
                Destination.TaskDetail.takeIf { Destination.hasAccess(it.requiredRoles, userRoles) }
                    ?.let {
                        composable(it.route) {
                            TaskView(modifier = Modifier.fillMaxSize())
                        }
                    }
                Destination.Report.takeIf { Destination.hasAccess(it.requiredRoles, userRoles) }
                    ?.let {
                        composable(it.route) {
                            ReportView(modifier = Modifier.fillMaxSize())
                        }
                    }
            }
        }
    }
}

