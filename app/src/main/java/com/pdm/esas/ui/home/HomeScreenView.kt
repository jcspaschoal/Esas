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
import com.pdm.esas.ui.calendar.AddTaskView
import com.pdm.esas.ui.calendar.CalendarView
import com.pdm.esas.ui.calendar.EditTaskView
import com.pdm.esas.ui.calendar.PresenceView
import com.pdm.esas.ui.components.BottomNavigationBar
import com.pdm.esas.ui.navigation.Destination
import com.pdm.esas.ui.report.ReportView
import com.pdm.esas.ui.tasks.TaskView
import com.pdm.esas.ui.visitors.VisitorView

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = hiltViewModel()
    val userRoles by homeViewModel.userRoles.collectAsState()

    if (userRoles.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
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
                        navController = navController,
                        userRoles = userRoles
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Destination.TaskDetail.route,
                modifier = modifier.padding(innerPadding)
            ) {
                Destination.Calendar
                    .takeIf { Destination.hasAccess(it.requiredRoles, userRoles) }
                    ?.let { calendarDest ->
                        composable(calendarDest.route) {
                            val isAdmin = userRoles.contains("admin")

                            // Exemplo 1: Callback extra (onViewPresence) para navegar para Presence
                            CalendarView(
                                modifier = Modifier.fillMaxSize(),
                                onBackClick = { navController.popBackStack() },
                                onAddTaskClick = {
                                    navController.navigate(Destination.Calendar.Add.route)
                                },
                                isAdmin = isAdmin,
                                viewModel = hiltViewModel(),
                                onEditTaskClick = { taskId ->
                                    navController.navigate(
                                        Destination.Calendar.TaskDetails.dynamicRoute(taskId)
                                    )
                                },
                                onViewDetailsClick = { taskId ->
                                    navController.navigate(
                                        Destination.Calendar.Presence.dynamicRoute(taskId)
                                    )
                                }
                            )
                        }
                    }


                Destination.Calendar.TaskDetails.takeIf {
                    Destination.hasAccess(it.requiredRoles, userRoles)
                }?.let { taskDetailsDest ->
                    composable(
                        route = taskDetailsDest.route,
                        arguments = taskDetailsDest.navArguments
                    ) { backStackEntry ->
                        val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                        EditTaskView(
                            onBackClick = { navController.popBackStack() },
                            taskId = taskId
                        )
                    }
                }


                Destination.Calendar.Presence.takeIf {
                    Destination.hasAccess(it.requiredRoles, userRoles)
                }?.let { presenceDest ->
                    composable(
                        route = presenceDest.route,
                        arguments = presenceDest.navArguments
                    ) { backStackEntry ->
                        val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                        PresenceView(
                            taskId = taskId,
                            onBackClick = { navController.popBackStack() },
                        )
                    }
                }

                Destination.Calendar.Add.takeIf {
                    Destination.hasAccess(it.requiredRoles, userRoles)
                }?.let {
                    composable(Destination.Calendar.Add.route) {
                        AddTaskView(
                            adminId = homeViewModel.userId,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }



                Destination.TaskDetail.takeIf {
                    Destination.hasAccess(it.requiredRoles, userRoles)
                }?.let {
                    composable(it.route) {
                        TaskView(modifier = Modifier.fillMaxSize())
                    }
                }

                Destination.Visitors.takeIf {
                    Destination.hasAccess(it.requiredRoles, userRoles)
                }?.let {
                    composable(it.route) {
                        VisitorView(modifier = Modifier.fillMaxSize())
                    }
                }



                Destination.Report.takeIf {
                    Destination.hasAccess(it.requiredRoles, userRoles)
                }?.let {
                    composable(it.route) {
                        ReportView(modifier = Modifier.fillMaxSize())
                    }
                }


            }
        }
    }
}
