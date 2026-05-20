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
import pe.pixelstudio.pixelerp.data.model.Moneda
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

    var logoutTriggered by mutableStateOf(false)

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
                        if (user.passwordHash.trim() != hashedPass) {
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

    fun loginMaster() {
        val originalNombreNegocio = nombreNegocio
        val originalUsuario = usuario
        val originalRecordar = recordarSesion

        nombreNegocio = "Pixel"
        usuario = "admin"
        contrasena = "admin123"

        // No guardamos estas credenciales en prefs para no borrar las anteriores
        viewModelScope.launch {
            estaCargando = true
            error = null
            try {
                var negocio = firebaseRepository.getNegocioPorNombre(nombreNegocio)
                if (negocio == null) {
                    // Si no existe el negocio maestro "Pixel", lo creamos para facilitar pruebas
                    val nuevoMaster = Negocio(
                        id = "",
                        nombreComercial = "Pixel",
                        razonSocial = "Pixel Studio SAC",
                        ruc = "20123456789",
                        direccion = "Av. Digital 123",
                        celular = "987654321",
                        correo = "admin@pixel.pe",
                        plan = "PRO",
                        fechaInicioSuscripcion = System.currentTimeMillis(),
                        fechaFinSuscripcion = System.currentTimeMillis() + 31536000000L, // 1 año
                        activo = true
                    )
                    val adminPass = "admin123"
                    val passwordHash = firebaseRepository.hashPassword(adminPass)
                    val adminUsuario = Usuario(
                        nombre = "Super Admin Pixel",
                        usuario = "admin",
                        passwordHash = passwordHash,
                        rol = Rol.SUPER_ADMIN,
                        activo = true
                    )
                    firebaseRepository.crearNegocio(nuevoMaster, adminUsuario)
                    negocio = firebaseRepository.getNegocioPorNombre("Pixel")
                }

                if (negocio != null && negocio.activo) {
                    val user = firebaseRepository.getUsuarioEnNegocio(negocio.id, usuario)
                    if (user != null && user.activo) {
                        val hashedPass = firebaseRepository.hashPassword(contrasena)
                        if (user.passwordHash.trim() == hashedPass) {
                            negocioActual = negocio
                            usuarioActual = user
                            loginExitoso = true
                        } else {
                            error = "Contraseña incorrecta"
                        }
                    } else {
                        error = "Usuario no encontrado o inactivo"
                    }
                } else {
                    error = "Negocio no existe o inactivo"
                }
            } catch (e: Exception) {
                error = "Error: ${e.message}"
            } finally {
                estaCargando = false
                // Restauramos los campos visuales a lo que estaba antes si falló
                if (!loginExitoso) {
                    nombreNegocio = originalNombreNegocio
                    usuario = originalUsuario
                    recordarSesion = originalRecordar
                }
            }
        }
    }

    fun logout() {
        usuarioActual = null
        negocioActual = null
        loginExitoso = false
        logoutTriggered = true
    }

    fun resetLogoutStatus() {
        logoutTriggered = false
    }
}
