package pe.pixelstudio.pixelerp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "productos",
    foreignKeys = [
        ForeignKey(
            entity = GrupoProducto::class,
            parentColumns = ["id"],
            childColumns = ["grupoProductoId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("grupoProductoId")]
)
data class Producto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val grupoProductoId: Int,
    val nombre: String,
    val precioVenta: Double,
    val imagenUri: String? = null,
    val activo: Boolean = true,
    val fechaCreacion: Long = System.currentTimeMillis()
)
