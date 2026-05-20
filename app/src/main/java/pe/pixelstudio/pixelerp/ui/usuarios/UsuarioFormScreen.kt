package pe.pixelstudio.pixelerp.ui.usuarios

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.pixelstudio.pixelerp.data.model.Rol

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuarioFormScreen(
    viewModel: UsuarioViewModel,
    negocioId: String,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val roles = Rol.values().filter { it != Rol.SUPER_ADMIN }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.usuarioSeleccionado == null) "Nuevo Trabajador" else "Editar Trabajador") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = viewModel.nombre,
                onValueChange = { viewModel.nombre = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.usuario,
                onValueChange = { viewModel.usuario = it },
                label = { Text("Nombre de Usuario") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.contrasena,
                onValueChange = { viewModel.contrasena = it },
                label = { Text(if (viewModel.usuarioSeleccionado == null) "Contraseña" else "Nueva Contraseña (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = viewModel.rolSeleccionado.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Rol") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    roles.forEach { rol ->
                        DropdownMenuItem(
                            text = { Text(rol.name) },
                            onClick = {
                                viewModel.rolSeleccionado = rol
                                expanded = false
                            }
                        )
                    }
                }
            }

            if (viewModel.error != null) {
                Text(viewModel.error!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.guardarUsuario(negocioId, onBack) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !viewModel.estaCargando
            ) {
                Text(if (viewModel.usuarioSeleccionado == null) "Crear Trabajador" else "Guardar Cambios")
            }
        }
    }
}
