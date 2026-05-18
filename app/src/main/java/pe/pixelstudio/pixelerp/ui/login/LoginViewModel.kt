package pe.pixelstudio.pixelerp.ui.login

import android.content.SharedPreferences
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

class LoginViewModel(
    private val firebaseRepository: FirebaseRepository,
    private val prefs: SharedPreferences
) : ViewModel() {

    var nombreNegocio by mutableStateOf("")
    var usuario by mutableStateOf("")
    var contrasena by mutableStateOf("")
    var recordarSesion by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var estaCargando by mutableStateOf(false)
    var loginExitoso by mutableStateOf(false)

    var usuarioActual by mutableStateOf<Usuario?>(null)
    var negocioActual by mutableStateOf<Negocio?>(null)

    init {
        nombreNegocio = prefs.getString("saved_business", "") ?: ""
        usuario = prefs.getString("saved_user", "") ?: ""
        recordarSesion = prefs.getBoolean("remember_me", false)
    }

    fun onLoginClick() {
        val negocioInput = nombreNegocio.trim()
        val usuarioInput = usuario.trim()
        val contrasenaInput = contrasena.trim()

        if (negocioInput.isBlank() || usuarioInput.isBlank() || contrasenaInput.isBlank()) {
            error = "Por favor, completa todos los campos"
            return
        }

        viewModelScope.launch {
            estaCargando = true
            error = null

            try {
                val negocio = firebaseRepository.getNegocioPorNombre(negocioInput)

                if (negocio == null) {
                    error = "El negocio no existe"
                } else if (!negocio.activo) {
                    error = "El negocio está inactivo"
                } else if (negocio.fechaFinSuscripcion != null && negocio.fechaFinSuscripcion < System.currentTimeMillis()) {
                    error = "La suscripción ha expirado"
                } else {
                    val user = firebaseRepository.getUsuarioEnNegocio(negocio.id, usuarioInput)

                    if (user == null) {
                        error = "Usuario no encontrado en este negocio"
                    } else if (!user.activo) {
                        error = "El usuario está inactivo"
                    } else {
                        val hashedPass = firebaseRepository.hashPassword(contrasenaInput)
                        if (user.passwordHash != hashedPass) {
                            error = "Contraseña incorrecta"
                        } else {
                            // Login Exitoso
                            negocioActual = negocio
                            usuarioActual = user

                            if (recordarSesion) {
                                prefs.edit().apply {
                                    putString("saved_business", nombreNegocio)
                                    putString("saved_user", usuario)
                                    putBoolean("remember_me", true)
                                    apply()
                                }
                            } else {
                                prefs.edit().clear().apply()
                            }
                            loginExitoso = true
                        }
                    }
                }
            } catch (e: Exception) {
                error = "Error: ${e.message}"
            } finally {
                estaCargando = false
            }
        }
    }

    fun resetLoginStatus() {
        loginExitoso = false
    }
}
