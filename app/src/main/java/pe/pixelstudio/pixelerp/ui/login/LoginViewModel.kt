package pe.pixelstudio.pixelerp.ui.login

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pe.pixelstudio.pixelerp.data.repository.UsuarioRepository

class LoginViewModel(
    private val repository: UsuarioRepository,
    private val prefs: SharedPreferences
) : ViewModel() {

    var usuario by mutableStateOf("")
    var contrasena by mutableStateOf("")
    var recordarSesion by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var estaCargando by mutableStateOf(false)
    var loginExitoso by mutableStateOf(false)

    init {
        usuario = prefs.getString("saved_user", "") ?: ""
        contrasena = prefs.getString("saved_pass", "") ?: ""
        recordarSesion = prefs.getBoolean("remember_me", false)
    }

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
                if (recordarSesion) {
                    prefs.edit().apply {
                        putString("saved_user", usuario)
                        putString("saved_pass", contrasena)
                        putBoolean("remember_me", true)
                        apply()
                    }
                } else {
                    prefs.edit().clear().apply()
                }
                loginExitoso = true
            }

            estaCargando = false
        }
    }

    fun resetLoginStatus() {
        loginExitoso = false
    }
}
