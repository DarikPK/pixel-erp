package pe.pixelstudio.pixelerp.ui.productos.grupos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.pixelstudio.pixelerp.data.model.CampoProducto
import pe.pixelstudio.pixelerp.data.model.PlantillasCampos
import pe.pixelstudio.pixelerp.data.model.TipoDato
import pe.pixelstudio.pixelerp.data.model.Moneda

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrupoFormScreen(
    viewModel: GrupoViewModel,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showPredefinedDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Grupo de Producto") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.guardarGrupo(onBack) }) {
                        Text("GUARDAR", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Información General", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = viewModel.nombreGrupo,
                    onValueChange = { viewModel.nombreGrupo = it },
                    label = { Text("Nombre del Grupo (ej. Medicamentos)") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = viewModel.descripcionGrupo,
                    onValueChange = { viewModel.descripcionGrupo = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    minLines = 2
                )
            }

            item {
                HorizontalDivider()
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Configuración de Campos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Row {
                        IconButton(onClick = { showPredefinedDialog = true }) {
                            Icon(Icons.Default.List, contentDescription = "Campos Predefinidos", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.AddCircle, contentDescription = "Campo Personalizado", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Campos Obligatorios del Sistema:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Text("• Nombre del Producto", style = MaterialTheme.typography.bodySmall)
                        Text("• Precio de venta", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            if (viewModel.camposTemporales.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No has agregado campos específicos", color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }

            itemsIndexed(viewModel.camposTemporales) { index, campo ->
                CampoTemporalItem(
                    campo = campo,
                    onRemove = { viewModel.removerCampoTemporal(index) }
                )
            }
        }

        if (showAddDialog) {
            AgregarCampoDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { nuevoCampo ->
                    viewModel.agregarCampoTemporal(nuevoCampo)
                    showAddDialog = false
                }
            )
        }

        if (showPredefinedDialog) {
            PredefinidosDialog(
                onDismiss = { showPredefinedDialog = false },
                onSelect = { campo ->
                    viewModel.agregarCampoTemporal(campo)
                    showPredefinedDialog = false
                }
            )
        }
    }
}

@Composable
fun CampoTemporalItem(campo: CampoProducto, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = campo.nombreCampo, fontWeight = FontWeight.Bold)
                Text(
                    text = "${campo.tipoDato}${if (campo.obligatorio) " • Obligatorio" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                if (campo.prefijo != null || campo.sufijo != null) {
                    Text(
                        text = "Formato: ${campo.prefijo ?: ""}[Valor]${campo.sufijo ?: ""}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredefinidosDialog(onDismiss: () -> Unit, onSelect: (CampoProducto) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Elegir Campo Predefinido") },
        text = {
            LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                items(PlantillasCampos.predefinidos) { plantilla ->
                    ListItem(
                        headlineContent = { Text(plantilla.nombreCampo) },
                        supportingContent = { Text(plantilla.tipoDato.name.lowercase()) },
                        modifier = Modifier.clickable { onSelect(plantilla) }
                    )
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
