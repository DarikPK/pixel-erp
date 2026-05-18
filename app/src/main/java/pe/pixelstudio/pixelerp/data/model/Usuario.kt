package pe.pixelstudio.pixelerp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val usuario: String,
    val contrasena: String,
    val rol: Rol,
    val activo: Boolean = true,
    val fechaCreacion: Long = System.currentTimeMillis()
)
