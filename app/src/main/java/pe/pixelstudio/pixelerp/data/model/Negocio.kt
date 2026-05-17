package pe.pixelstudio.pixelerp.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Negocio(
    val id: String = "",
    val nombreComercial: String = "",
    val razonSocial: String = "",
    val ruc: String = "",
    val direccion: String = "",
    val telefono: String = "",
    val correo: String = "",
    val logoUrl: String = "",
    val colorPrincipal: String = "#6200EE",
    val colorSecundario: String = "#03DAC6",
    val activo: Boolean = true,
    val fechaInicioSuscripcion: Long? = null,
    val fechaFinSuscripcion: Long? = null,
    val plan: String = "BASIC",
    @ServerTimestamp
    val creadoEn: Date? = null
)
