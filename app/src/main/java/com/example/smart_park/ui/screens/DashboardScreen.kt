package com.example.smart_park.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smart_park.model.ParkingSession
import com.example.smart_park.ui.components.ParkingCard
import com.example.smart_park.viewmodel.ParkingViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ParkingViewModel = viewModel(),
    onAddVehicleClick: () -> Unit
) {
    val sessions by viewModel.sessions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    var showCheckoutDialog by remember { mutableStateOf<ParkingSession?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ParkSmart 🚗", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddVehicleClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (sessions.isEmpty() && !isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aucun véhicule stationné", color = MaterialTheme.colorScheme.outline)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    // RÉSUMÉ (Vue d'ensemble demandée)
                    item {
                        val totalRevenue = sessions.sumOf { it.calculateCurrentAmount() }
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Occupées", style = MaterialTheme.typography.labelMedium)
                                    Text("${sessions.size}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Total estimé", style = MaterialTheme.typography.labelMedium)
                                    Text(
                                        String.format(Locale.getDefault(), "%.2f €", totalRevenue),
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    items(sessions) { session ->
                        ParkingCard(
                            session = session,
                            onCheckout = { showCheckoutDialog = session },
                            onCancel = { session.id?.let { viewModel.cancelSession(it) } }
                        )
                    }
                }
            }

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }

        // Dialogue de sortie (Validation paiement)
        showCheckoutDialog?.let { session ->
            AlertDialog(
                onDismissRequest = { showCheckoutDialog = null },
                title = { Text("Valider la sortie") },
                text = {
                    Column {
                        Text("Plaque : ${session.license_plate}")
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "TOTAL À PAYER : ${String.format(Locale.getDefault(), "%.2f €", session.calculateCurrentAmount())}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        viewModel.checkoutVehicle(session)
                        showCheckoutDialog = null
                    }) {
                        Text("ENCAISSER ET LIBÉRER")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCheckoutDialog = null }) {
                        Text("ANNULER")
                    }
                }
            )
        }
    }
}
