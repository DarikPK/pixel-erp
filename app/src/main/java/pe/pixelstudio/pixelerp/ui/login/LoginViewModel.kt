package pe.pixelstudio.pixelerp.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pe.pixelstudio.pixelerp.data.repository.UsuarioRepository

class LoginViewModel(private val repository: UsuarioRepository) : ViewModel() {

    var usuario by mutableStateOf("")
    var contrasena by mutableStateOf("")
    var error by mutableStateOf<String?>(null)
    var estaCargando by mutableStateOf(false)
    var loginExitoso by mutableStateOf(false)

    fun onLoginClick() {
        if (usuario.isBlank() || contrasena.isBlank()) {
            error = "Por favor, completa todos los campos"
            return
        }

        viewModelScope.launch {
            estaCargando = true
            error = null

            val user = repository.obtenerUsuario(usuario)

            if (user == null) {
                error = "Usuario no encontrado"
            } else if (!user.activo) {
                error = "El usuario está inactivo"
            } else if (user.contrasena != contrasena) {
                error = "Contraseña incorrecta"
            } else {
                loginExitoso = true
            }

            estaCargando = false
        }
    }

    fun resetLoginStatus() {
        loginExitoso = false
    }
}
