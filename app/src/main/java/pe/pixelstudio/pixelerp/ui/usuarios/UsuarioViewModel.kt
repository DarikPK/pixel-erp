package pe.pixelstudio.pixelerp.ui.usuarios

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pe.pixelstudio.pixelerp.data.model.Rol
import pe.pixelstudio.pixelerp.data.model.Usuario
import pe.pixelstudio.pixelerp.data.remote.FirebaseRepository

class UsuarioViewModel(private val repository: FirebaseRepository) : ViewModel() {
    var usuarios by mutableStateOf<List<Usuario>>(emptyList())
    var estaCargando by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    // Form state
    var nombre by mutableStateOf("")
    var usuario by mutableStateOf("")
    var contrasena by mutableStateOf("")
    var rolSeleccionado by mutableStateOf(Rol.ALMACEN)

    var usuarioSeleccionado by mutableStateOf<Usuario?>(null)

    fun cargarUsuarios(negocioId: String) {
        viewModelScope.launch {
            estaCargando = true
            try {
                usuarios = repository.obtenerUsuariosPorNegocio(negocioId)
            } catch (e: Exception) {
                error = e.message
            } finally {
                estaCargando = false
            }
        }
    }

    fun guardarUsuario(negocioId: String, onExito: () -> Unit) {
        if (nombre.isBlank() || usuario.isBlank() || (usuarioSeleccionado == null && contrasena.isBlank())) {
            error = "Por favor completa todos los campos"
            return
        }

        viewModelScope.launch {
            estaCargando = true
            try {
                val passHash = if (contrasena.isNotEmpty()) repository.hashPassword(contrasena) else usuarioSeleccionado?.passwordHash ?: ""

                val user = Usuario(
                    id = usuarioSeleccionado?.id ?: "",
                    nombre = nombre,
                    usuario = usuario,
                    passwordHash = passHash,
                    rol = rolSeleccionado,
                    activo = usuarioSeleccionado?.activo ?: true
                )

                if (usuarioSeleccionado == null) {
                    repository.crearUsuarioEnNegocio(negocioId, user)
                } else {
                    repository.actualizarUsuarioEnNegocio(negocioId, user)
                }

                limpiarFormulario()
                onExito()
            } catch (e: Exception) {
                error = e.message
            } finally {
                estaCargando = false
            }
        }
    }

    fun conmutarEstadoUsuario(negocioId: String, usuario: Usuario) {
        viewModelScope.launch {
            try {
                val actualizado = usuario.copy(activo = !usuario.activo)
                repository.actualizarUsuarioEnNegocio(negocioId, actualizado)
                cargarUsuarios(negocioId)
            } catch (e: Exception) {
                error = e.message
            }
        }
    }

    fun prepararEdicion(u: Usuario) {
        usuarioSeleccionado = u
        nombre = u.nombre
        usuario = u.usuario
        rolSeleccionado = u.rol
        contrasena = ""
    }

    fun limpiarFormulario() {
        usuarioSeleccionado = null
        nombre = ""
        usuario = ""
        contrasena = ""
        rolSeleccionado = Rol.ALMACEN
        error = null
    }
}
