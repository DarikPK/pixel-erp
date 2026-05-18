package pe.pixelstudio.pixelerp.ui.productos.grupos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.pixelstudio.pixelerp.data.model.CampoProducto
import pe.pixelstudio.pixelerp.data.model.TipoDato
import pe.pixelstudio.pixelerp.data.model.Moneda

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionCamposScreen(
    grupoId: Int,
    viewModel: GrupoViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val campos by viewModel.obtenerCampos(grupoId).collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var campoAEditar by remember { mutableStateOf<CampoProducto?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurar Campos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                campoAEditar = null
                showDialog = true
            }) {
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
                        CampoItem(
                            campo = campo,
                            onEdit = {
                                campoAEditar = campo
                                showDialog = true
                            },
                            onDelete = { viewModel.eliminarCampo(campo) },
                            onMoveUp = { viewModel.moverCampo(grupoId, campo, true, campos) },
                            onMoveDown = { viewModel.moverCampo(grupoId, campo, false, campos) }
                        )
                    }
                }
            }
        }

        if (showDialog) {
            AgregarCampoDialog(
                campoExistente = campoAEditar,
                onDismiss = { showDialog = false },
                onConfirm = { campo ->
                    if (campo.id == 0) {
                        viewModel.agregarCampo(campo.copy(grupoProductoId = grupoId, orden = campos.size))
                    } else {
                        viewModel.actualizarCampo(campo)
                    }
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun CampoItem(
    campo: CampoProducto,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = campo.nombreCampo, fontWeight = FontWeight.Bold)
                Text(text = "${campo.tipoDato} • ${if (campo.obligatorio) "Obligatorio" else "Opcional"}", style = MaterialTheme.typography.labelSmall)
            }

            Row {
                IconButton(onClick = onMoveUp) {
                    Icon(Icons.Default.ArrowUpward, contentDescription = "Subir", modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onMoveDown) {
                    Icon(Icons.Default.ArrowDownward, contentDescription = "Bajar", modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarCampoDialog(
    campoExistente: CampoProducto? = null,
    onDismiss: () -> Unit,
    onConfirm: (CampoProducto) -> Unit
) {
    var nombre by remember { mutableStateOf(campoExistente?.nombreCampo ?: "") }
    var tipoDato by remember { mutableStateOf(campoExistente?.tipoDato ?: TipoDato.TEXTO) }
    var obligatorio by remember { mutableStateOf(campoExistente?.obligatorio ?: false) }
    var prefijo by remember { mutableStateOf(campoExistente?.prefijo ?: "") }
    var sufijo by remember { campoExistente?.sufijo?.let { mutableStateOf(it) } ?: mutableStateOf("") }
    var opcionesTexto by remember { mutableStateOf(campoExistente?.opcionesLista?.joinToString(", ") ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configurar Campo") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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

                HorizontalDivider()
                Text("Formato Visual (Opcional)", style = MaterialTheme.typography.labelLarge)

                OutlinedTextField(
                    value = prefijo,
                    onValueChange = { prefijo = it },
                    label = { Text("Prefijo (ej. S/ )") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = sufijo,
                    onValueChange = { sufijo = it },
                    label = { Text("Sufijo (ej. Kg)") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (tipoDato == TipoDato.LISTA || tipoDato == TipoDato.ENTERO_LISTA) {
                    HorizontalDivider()
                    Text("Opciones de la lista (separadas por comas)", style = MaterialTheme.typography.labelLarge)
                    OutlinedTextField(
                        value = opcionesTexto,
                        onValueChange = { opcionesTexto = it },
                        placeholder = { Text("ej. Mg, G, Kg o Paracetamol, Ibuprofeno") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val opciones = if (opcionesTexto.isNotBlank()) {
                    opcionesTexto.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                } else null

                onConfirm(
                    CampoProducto(
                        id = campoExistente?.id ?: 0,
                        grupoProductoId = campoExistente?.grupoProductoId ?: 0,
                        nombreCampo = nombre,
                        tipoDato = tipoDato,
                        obligatorio = obligatorio,
                        orden = campoExistente?.orden ?: 0,
                        esPredefinido = campoExistente?.esPredefinido ?: false,
                        prefijo = prefijo.ifBlank { null },
                        sufijo = sufijo.ifBlank { null },
                        opcionesLista = opciones
                    )
                )
            }) { Text(if (campoExistente == null) "Agregar" else "Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TipoDatoSelector(selected: TipoDato, onSelected: (TipoDato) -> Unit) {
    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TipoDato.values().forEach { tipo ->
            FilterChip(
                selected = selected == tipo,
                onClick = { onSelected(tipo) },
                label = {
                    Text(
                        text = tipo.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            )
        }
    }
}
