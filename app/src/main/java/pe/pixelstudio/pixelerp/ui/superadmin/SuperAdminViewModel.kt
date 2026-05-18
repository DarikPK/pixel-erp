package pe.pixelstudio.pixelerp.ui.superadmin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pe.pixelstudio.pixelerp.data.model.Negocio
import pe.pixelstudio.pixelerp.data.model.Rol
import pe.pixelstudio.pixelerp.data.model.Usuario
import pe.pixelstudio.pixelerp.data.remote.FirebaseRepository

class SuperAdminViewModel(private val repository: FirebaseRepository) : ViewModel() {
    var negocios by mutableStateOf<List<Negocio>>(emptyList())
    var estaCargando by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun cargarNegocios() {
        viewModelScope.launch {
            estaCargando = true
            try {
                negocios = repository.obtenerTodosLosNegocios()
            } catch (e: Exception) {
                error = e.message
            } finally {
                estaCargando = false
            }
        }
    }

    fun crearNegocio(negocio: Negocio, adminUser: String, adminPass: String, onExito: () -> Unit) {
        if (adminUser.isBlank() || adminPass.isBlank()) {
            error = "Debe proporcionar usuario y contraseña para el administrador"
            return
        }
        viewModelScope.launch {
            estaCargando = true
            try {
                val passwordHash = repository.hashPassword(adminPass)
                val adminUsuario = Usuario(
                    nombre = "Administrador Inicial",
                    usuario = adminUser,
                    passwordHash = passwordHash,
                    rol = Rol.ADMINISTRADOR,
                    activo = true
                )
                repository.crearNegocio(negocio, adminUsuario)
                cargarNegocios()
                onExito()
            } catch (e: Exception) {
                error = e.message
            } finally {
                estaCargando = false
            }
        }
    }

    fun conmutarEstadoNegocio(negocio: Negocio) {
        viewModelScope.launch {
            try {
                repository.actualizarEstadoNegocio(negocio.id, !negocio.activo)
                cargarNegocios()
            } catch (e: Exception) {
                error = e.message
            }
        }
    }
}
