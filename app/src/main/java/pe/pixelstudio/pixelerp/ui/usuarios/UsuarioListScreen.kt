package pe.pixelstudio.pixelerp.ui.usuarios

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.pixelstudio.pixelerp.data.model.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuarioListScreen(
    viewModel: UsuarioViewModel,
    negocioId: String,
    onAddUsuario: () -> Unit,
    onEditUsuario: (Usuario) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.cargarUsuarios(negocioId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Personal") },
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
            FloatingActionButton(onClick = onAddUsuario) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Agregar Personal")
            }
        }
    ) { padding ->
        if (viewModel.estaCargando) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.usuarios) { usuario ->
                    UsuarioItem(
                        usuario = usuario,
                        onToggleActivo = { viewModel.conmutarEstadoUsuario(negocioId, usuario) },
                        onEdit = { onEditUsuario(usuario) }
                    )
                }
            }
        }
    }
}

@Composable
fun UsuarioItem(
    usuario: Usuario,
    onToggleActivo: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onEdit
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = usuario.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "@${usuario.usuario}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "Rol: ${usuario.rol}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Switch(checked = usuario.activo, onCheckedChange = { onToggleActivo() })
        }
    }
}
