package pe.pixelstudio.pixelerp.ui.superadmin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pe.pixelstudio.pixelerp.data.model.Negocio
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NegocioListScreen(
    viewModel: SuperAdminViewModel,
    onCrearNegocio: () -> Unit,
    onEditarNegocio: (Negocio) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.cargarNegocios()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Super Admin - Negocios") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCrearNegocio) {
                Icon(Icons.Default.Add, contentDescription = "Crear Negocio")
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
                items(viewModel.negocios) { negocio ->
                    NegocioItem(
                        negocio = negocio,
                        onToggleActivo = { viewModel.conmutarEstadoNegocio(negocio) },
                        onEdit = { onEditarNegocio(negocio) }
                    )
                }
            }
        }
    }
}

@Composable
fun NegocioItem(
    negocio: Negocio,
    onToggleActivo: () -> Unit,
    onEdit: () -> Unit
) {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onEdit
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = negocio.nombreComercial, style = MaterialTheme.typography.titleLarge)
                    Text(text = "RUC: ${negocio.ruc}", style = MaterialTheme.typography.bodyMedium)
                }
                Switch(checked = negocio.activo, onCheckedChange = { onToggleActivo() })
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Suscripción: ${negocio.fechaInicioSuscripcion?.let { sdf.format(Date(it)) } ?: "N/A"} - ${negocio.fechaFinSuscripcion?.let { sdf.format(Date(it)) } ?: "N/A"}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Plan: ${negocio.plan}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
