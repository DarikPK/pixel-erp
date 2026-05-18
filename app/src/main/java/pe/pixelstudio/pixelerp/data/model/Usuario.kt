package pe.pixelstudio.pixelerp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey
    @DocumentId
    val id: String = "",
    val nombre: String = "",
    val usuario: String = "",
    val passwordHash: String = "",
    val rol: Rol = Rol.CAJERO,
    val activo: Boolean = true,
    @ServerTimestamp
    val creadoEn: Date? = null
)
