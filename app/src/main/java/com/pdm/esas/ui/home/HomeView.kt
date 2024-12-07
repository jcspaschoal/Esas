package com.pdm.esas.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.*
import com.pdm.esas.ui.calendar.CalendarView
import com.pdm.esas.ui.components.BottomNavigationBar
import com.pdm.esas.ui.navigation.Destination
import com.pdm.esas.ui.report.ReportView
import com.pdm.esas.ui.tasks.TaskView

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = hiltViewModel()
    val isAdmin by homeViewModel.isAdmin.collectAsState()

    if (isAdmin == null) {
        // Exibe um indicador de carregamento enquanto verifica as roles
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(
            bottomBar = { BottomNavigationBar(navController = navController, isAdmin = isAdmin!!) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = if (isAdmin == true) Destination.Calendar.route else Destination.Task.route,
                modifier = modifier.padding(innerPadding)
            ) {
                if (isAdmin == true) {
                    composable(Destination.Calendar.route) {
                        CalendarView(modifier = Modifier.fillMaxSize())
                    }
                }
                composable(Destination.Task.route) {
                    TaskView(modifier = Modifier.fillMaxSize())
                }
                composable(Destination.Report.route) {
                    ReportView(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}
