package pe.pixelstudio.pixelerp.ui.productos.grupos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.pixelstudio.pixelerp.data.model.GrupoProducto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrupoListScreen(
    viewModel: GrupoViewModel,
    onAddGrupo: () -> Unit,
    onConfigurarCampos: (Int) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val grupos by viewModel.grupos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grupos de Productos") },
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
            FloatingActionButton(onClick = onAddGrupo) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Grupo")
            }
        }
    ) { paddingValues ->
        if (grupos.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay grupos creados", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(grupos) { grupo ->
                    GrupoItem(
                        grupo = grupo,
                        onToggleEstado = { viewModel.toggleEstadoGrupo(grupo) },
                        onConfigurar = { onConfigurarCampos(grupo.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun GrupoItem(
    grupo: GrupoProducto,
    onToggleEstado: () -> Unit,
    onConfigurar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (grupo.activo) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = grupo.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (grupo.activo) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    text = grupo.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = if (grupo.activo) "Activo" else "Inactivo",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (grupo.activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
            Row {
                IconButton(onClick = onConfigurar) {
                    Icon(Icons.Default.Settings, contentDescription = "Configurar Campos")
                }
                Switch(
                    checked = grupo.activo,
                    onCheckedChange = { onToggleEstado() }
                )
            }
        }
    }
}
