package com.example.smart_park.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smart_park.model.VehicleType
import com.example.smart_park.viewmodel.ParkingViewModel
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicleScreen(
    viewModel: ParkingViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    var licensePlate by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(VehicleType.VOITURE) }
    var expanded by remember { mutableStateOf(false) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Launcher pour l'appareil photo
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { resultBitmap ->
        if (resultBitmap != null) {
            bitmap = resultBitmap
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nouvelle Entrée", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Zone de Photo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(CutCornerShape(16.dp))
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap!!.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Petit bouton pour reprendre la photo
                    IconButton(
                        onClick = { cameraLauncher.launch() },
                        modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp),
                        colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Refaire")
                    }
                } else {
                    OutlinedButton(
                        onClick = { cameraLauncher.launch() },
                        shape = CutCornerShape(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(48.dp))
                            Text("Prendre la photo du véhicule")
                        }
                    }
                }
            }

            OutlinedTextField(
                value = licensePlate,
                onValueChange = { licensePlate = it.uppercase() },
                label = { Text("Plaque d'immatriculation") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.labelLarge,
                shape = CutCornerShape(8.dp),
                placeholder = { Text("EX: AA-123-BB") }
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedType.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Type de véhicule") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    shape = CutCornerShape(8.dp)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    VehicleType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text("${type.name} (${type.hourlyRate}€/h)") },
                            onClick = {
                                selectedType = type
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (licensePlate.isNotBlank()) {
                        val byteArray = bitmap?.let {
                            val stream = ByteArrayOutputStream()
                            it.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                            stream.toByteArray()
                        }

                        viewModel.registerEntry(
                            licensePlate = licensePlate,
                            vehicleType = selectedType,
                            photoBytes = byteArray
                        )
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = CutCornerShape(12.dp),
                enabled = licensePlate.isNotBlank()
            ) {
                Text("ENREGISTRER L'ENTRÉE", fontWeight = FontWeight.Bold)
            }
        }
    }
}
