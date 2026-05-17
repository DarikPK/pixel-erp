package pe.pixelstudio.pixelerp.ui.productos.lista

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.pixelstudio.pixelerp.data.model.CampoProducto
import pe.pixelstudio.pixelerp.data.model.TipoDato

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoFormScreen(
    viewModel: ProductoViewModel,
    onBack: () -> Unit
) {
    val grupos by viewModel.grupos.collectAsState()
    val camposActivos by if (viewModel.selectedGrupoId != null) {
        viewModel.obtenerCamposActivos(viewModel.selectedGrupoId!!).collectAsState(emptyList())
    } else {
        remember { mutableStateOf(emptyList<CampoProducto>()) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Producto") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Información Básica", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            // Selector de Grupo
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = grupos.find { it.id == viewModel.selectedGrupoId }?.nombre ?: "Seleccionar Grupo",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Grupo de Producto") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    grupos.forEach { grupo ->
                        DropdownMenuItem(
                            text = { Text(grupo.nombre) },
                            onClick = {
                                viewModel.selectedGrupoId = grupo.id
                                expanded = false
                            }
                        )
                    }
                }
            }

            if (viewModel.selectedGrupoId != null) {
                OutlinedTextField(
                    value = viewModel.nombreProducto,
                    onValueChange = { viewModel.nombreProducto = it },
                    label = { Text("Nombre del Producto") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = viewModel.precioVenta,
                    onValueChange = { viewModel.precioVenta = it },
                    label = { Text("Precio de Venta") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (camposActivos.isNotEmpty()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Detalles Específicos (${grupos.find { it.id == viewModel.selectedGrupoId }?.nombre})",
                        style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    camposActivos.forEach { campo ->
                        DynamicFormField(
                            campo = campo,
                            valor = viewModel.valoresDinamicos[campo.id] ?: "",
                            onValueChange = { viewModel.valoresDinamicos[campo.id] = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.guardarProducto(onBack) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar Producto")
                }
            } else {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("Por favor, selecciona un grupo para continuar", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}

@Composable
fun DynamicFormField(
    campo: CampoProducto,
    valor: Any,
    onValueChange: (Any) -> Unit
) {
    // Implementación simplificada para esta fase
    OutlinedTextField(
        value = valor.toString(),
        onValueChange = { onValueChange(it) },
        label = { Text("${campo.nombreCampo}${if (campo.obligatorio) " *" else ""}") },
        modifier = Modifier.fillMaxWidth(),
        supportingText = { if (campo.obligatorio) Text("Obligatorio") }
    )
}
