package pe.pixelstudio.pixelerp.ui.superadmin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import pe.pixelstudio.pixelerp.data.model.Negocio
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NegocioFormScreen(
    viewModel: SuperAdminViewModel,
    negocioAEditar: Negocio? = null,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    var nombreComercial by remember { mutableStateOf(negocioAEditar?.nombreComercial ?: "") }
    var razonSocial by remember { mutableStateOf(negocioAEditar?.razonSocial ?: "") }
    var ruc by remember { mutableStateOf(negocioAEditar?.ruc ?: "") }
    var direccion by remember { mutableStateOf(negocioAEditar?.direccion ?: "") }
    var celular by remember { mutableStateOf(negocioAEditar?.celular ?: "") }
    var correo by remember { mutableStateOf(negocioAEditar?.correo ?: "") }

    val planes = listOf("BASIC", "STANDARD", "PREMIUM", "PRO")
    var expanded by remember { mutableStateOf(false) }
    var planSeleccionado by remember { mutableStateOf(negocioAEditar?.plan ?: planes[0]) }

    var fechaInicio by remember { mutableStateOf(negocioAEditar?.fechaInicioSuscripcion ?: System.currentTimeMillis()) }
    var fechaFin by remember { mutableStateOf(negocioAEditar?.fechaFinSuscripcion ?: (System.currentTimeMillis() + 2592000000L)) }

    var adminUser by remember { mutableStateOf("") }
    var adminPass by remember { mutableStateOf("") }

    var errorLocal by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (negocioAEditar == null) "Crear Negocio" else "Editar Negocio") },
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = nombreComercial, onValueChange = { nombreComercial = it }, label = { Text("Nombre Comercial") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = razonSocial, onValueChange = { razonSocial = it }, label = { Text("Razón Social") }, modifier = Modifier.fillMaxWidth())

            OutlinedTextField(
                value = ruc,
                onValueChange = { if (it.length <= 11 && it.all { char -> char.isDigit() }) ruc = it },
                label = { Text("RUC (11 dígitos)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())

            OutlinedTextField(
                value = celular,
                onValueChange = { if (it.length <= 9 && it.all { char -> char.isDigit() }) celular = it },
                label = { Text("Celular (9 dígitos)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(value = correo, onValueChange = { correo = it }, label = { Text("Correo") }, modifier = Modifier.fillMaxWidth())

            // Dropdown para Planes
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = planSeleccionado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Plan") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    planes.forEach { plan ->
                        DropdownMenuItem(
                            text = { Text(plan) },
                            onClick = {
                                planSeleccionado = plan
                                expanded = false
                            }
                        )
                    }
                }
            }

            if (negocioAEditar == null) {
                HorizontalDivider()
                Text("Usuario Administrador Inicial", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(value = adminUser, onValueChange = { adminUser = it }, label = { Text("Usuario Admin") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = adminPass, onValueChange = { adminPass = it }, label = { Text("Contraseña Admin") }, modifier = Modifier.fillMaxWidth())
            }

            if (errorLocal != null) {
                Text(errorLocal!!, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    if (ruc.length != 11) {
                        errorLocal = "El RUC debe tener 11 dígitos"
                        return@Button
                    }
                    if (celular.length != 9) {
                        errorLocal = "El celular debe tener 9 dígitos"
                        return@Button
                    }

                    val n = Negocio(
                        id = negocioAEditar?.id ?: "",
                        nombreComercial = nombreComercial,
                        razonSocial = razonSocial,
                        ruc = ruc,
                        direccion = direccion,
                        celular = celular,
                        correo = correo,
                        plan = planSeleccionado,
                        fechaInicioSuscripcion = fechaInicio,
                        fechaFinSuscripcion = fechaFin,
                        activo = negocioAEditar?.activo ?: true
                    )
                    if (negocioAEditar == null) {
                        viewModel.crearNegocio(n, adminUser, adminPass, onBack)
                    } else {
                        viewModel.actualizarNegocio(n, onBack)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewModel.estaCargando
            ) {
                Text(if (negocioAEditar == null) "Crear" else "Guardar")
            }
        }
    }
}
