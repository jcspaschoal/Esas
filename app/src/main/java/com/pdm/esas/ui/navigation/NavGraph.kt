// NavGraph.kt
package com.pdm.esas.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pdm.esas.ui.home.HomeScreen
import com.pdm.esas.ui.login.LoginView
import com.pdm.esas.ui.login.LoginViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Destination.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Destination.Login.route) {
            val viewModel: LoginViewModel = hiltViewModel()
            LoginView(
                viewModel = viewModel,
                modifier = modifier
                    .fillMaxSize()
                    .imePadding()
            ) {
                navController.navigate(Destination.Home.route) {
                    popUpTo(Destination.Login.route) { inclusive = true }
                }
            }
        }

        composable(Destination.Home.route) {
            HomeScreen(modifier = modifier)
        }
    }
}
