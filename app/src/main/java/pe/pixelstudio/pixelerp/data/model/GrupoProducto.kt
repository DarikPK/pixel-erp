package pe.pixelstudio.pixelerp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grupos_productos")
data class GrupoProducto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val activo: Boolean = true,
    val fechaCreacion: Long = System.currentTimeMillis()
)
