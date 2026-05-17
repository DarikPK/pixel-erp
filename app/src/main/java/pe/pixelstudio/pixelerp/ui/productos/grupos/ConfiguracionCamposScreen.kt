package pe.pixelstudio.pixelerp.ui.productos.grupos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.pixelstudio.pixelerp.data.model.CampoProducto
import pe.pixelstudio.pixelerp.data.model.TipoDato
import pe.pixelstudio.pixelerp.data.model.Moneda

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionCamposScreen(
    grupoId: Int,
    viewModel: GrupoViewModel,
    onBack: () -> Unit
) {
    val campos by viewModel.obtenerCampos(grupoId).collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurar Campos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Campo")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            Text(
                text = "Campos Obligatorios Base",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "• Nombre\n• Precio de venta",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text(
                text = "Campos del Grupo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (campos.isEmpty()) {
                Text("No hay campos adicionales configurados")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(campos) { campo ->
                        CampoItem(campo)
                    }
                }
            }
        }

        if (showDialog) {
            AgregarCampoDialog(
                onDismiss = { showDialog = false },
                onConfirm = { nuevoCampo ->
                    viewModel.agregarCampo(nuevoCampo.copy(grupoProductoId = grupoId))
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun CampoItem(campo: CampoProducto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = campo.nombreCampo, fontWeight = FontWeight.Bold)
                Text(text = "${campo.tipoDato} • ${if (campo.obligatorio) "Obligatorio" else "Opcional"}", style = MaterialTheme.typography.labelSmall)
            }
            if (campo.esPredefinido) {
                SuggestionChip(onClick = {}, label = { Text("Predefinido") })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarCampoDialog(
    onDismiss: () -> Unit,
    onConfirm: (CampoProducto) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var tipoDato by remember { mutableStateOf(TipoDato.TEXTO) }
    var obligatorio by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Campo") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del campo") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Tipo de dato", style = MaterialTheme.typography.labelLarge)
                TipoDatoSelector(selected = tipoDato, onSelected = { tipoDato = it })

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = obligatorio, onCheckedChange = { obligatorio = it })
                    Text("¿Es obligatorio?")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(
                    CampoProducto(
                        grupoProductoId = 0,
                        nombreCampo = nombre,
                        tipoDato = tipoDato,
                        obligatorio = obligatorio,
                        orden = 0,
                        esPredefinido = false
                    )
                )
            }) { Text("Agregar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun TipoDatoSelector(selected: TipoDato, onSelected: (TipoDato) -> Unit) {
    // Versión simple con chips para esta fase
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        TipoDato.values().take(4).forEach { tipo ->
            FilterChip(
                selected = selected == tipo,
                onClick = { onSelected(tipo) },
                label = { Text(tipo.name.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 10.sp) }
            )
        }
    }
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        TipoDato.values().drop(4).forEach { tipo ->
            FilterChip(
                selected = selected == tipo,
                onClick = { onSelected(tipo) },
                label = { Text(tipo.name.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 10.sp) }
            )
        }
    }
}

import androidx.compose.ui.unit.sp
