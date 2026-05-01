package com.example.smart_park.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smart_park.ui.screens.AddVehicleScreen
import com.example.smart_park.ui.screens.DashboardScreen
import com.example.smart_park.viewmodel.ParkingViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ParkingNavigation() {
    val navController = rememberNavController()
    val viewModel: ParkingViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") {
            DashboardScreen(
                viewModel = viewModel,
                onAddVehicleClick = { navController.navigate("add_vehicle") }
            )
        }
        composable("add_vehicle") {
            AddVehicleScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
