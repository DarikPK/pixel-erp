package pe.pixelstudio.pixelerp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "valores_campo_producto",
    foreignKeys = [
        ForeignKey(
            entity = Producto::class,
            parentColumns = ["id"],
            childColumns = ["productoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CampoProducto::class,
            parentColumns = ["id"],
            childColumns = ["campoProductoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("productoId"), Index("campoProductoId")]
)
data class ValorCampoProducto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productoId: Int,
    val campoProductoId: Int,
    val valorTexto: String? = null,
    val valorEntero: Int? = null,
    val valorDecimal: Double? = null,
    val valorFecha: Long? = null,
    val valorBooleano: Boolean? = null,
    val valorMoneda: Double? = null
)
