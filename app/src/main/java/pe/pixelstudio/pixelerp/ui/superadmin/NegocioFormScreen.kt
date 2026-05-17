package pe.pixelstudio.pixelerp.ui.superadmin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.pixelstudio.pixelerp.data.model.Negocio
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NegocioFormScreen(
    viewModel: SuperAdminViewModel,
    negocioAEditar: Negocio? = null,
    onBack: () -> Unit
) {
    var nombreComercial by remember { mutableStateOf(negocioAEditar?.nombreComercial ?: "") }
    var razonSocial by remember { mutableStateOf(negocioAEditar?.razonSocial ?: "") }
    var ruc by remember { mutableStateOf(negocioAEditar?.ruc ?: "") }
    var direccion by remember { mutableStateOf(negocioAEditar?.direccion ?: "") }
    var telefono by remember { mutableStateOf(negocioAEditar?.telefono ?: "") }
    var correo by remember { mutableStateOf(negocioAEditar?.correo ?: "") }
    var plan by remember { mutableStateOf(negocioAEditar?.plan ?: "BASIC") }

    // Simplificando fechas para el ejemplo (en prod usar DatePicker)
    var fechaInicio by remember { mutableStateOf(negocioAEditar?.fechaInicioSuscripcion ?: System.currentTimeMillis()) }
    var fechaFin by remember { mutableStateOf(negocioAEditar?.fechaFinSuscripcion ?: (System.currentTimeMillis() + 2592000000L)) } // +30 dias

    var adminUser by remember { mutableStateOf("") }
    var adminPass by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (negocioAEditar == null) "Crear Negocio" else "Editar Negocio") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
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
            OutlinedTextField(value = ruc, onValueChange = { ruc = it }, label = { Text("RUC") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = correo, onValueChange = { correo = it }, label = { Text("Correo") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = plan, onValueChange = { plan = it }, label = { Text("Plan") }, modifier = Modifier.fillMaxWidth())

            if (negocioAEditar == null) {
                Divider()
                Text("Usuario Administrador Inicial", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(value = adminUser, onValueChange = { adminUser = it }, label = { Text("Usuario Admin") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = adminPass, onValueChange = { adminPass = it }, label = { Text("Contraseña Admin") }, modifier = Modifier.fillMaxWidth())
            }

            Button(
                onClick = {
                    val n = Negocio(
                        id = negocioAEditar?.id ?: "",
                        nombreComercial = nombreComercial,
                        razonSocial = razonSocial,
                        ruc = ruc,
                        direccion = direccion,
                        telefono = telefono,
                        correo = correo,
                        plan = plan,
                        fechaInicioSuscripcion = fechaInicio,
                        fechaFinSuscripcion = fechaFin,
                        activo = negocioAEditar?.activo ?: true
                    )
                    if (negocioAEditar == null) {
                        viewModel.crearNegocio(n, adminUser, adminPass, onBack)
                    } else {
                        // Implementar editar si es necesario, por ahora crear
                        onBack()
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
