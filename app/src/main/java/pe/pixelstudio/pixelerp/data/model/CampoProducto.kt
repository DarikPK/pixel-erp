package pe.pixelstudio.pixelerp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "campos_producto",
    foreignKeys = [
        ForeignKey(
            entity = GrupoProducto::class,
            parentColumns = ["id"],
            childColumns = ["grupoProductoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("grupoProductoId")]
)
data class CampoProducto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val grupoProductoId: Int,
    val nombreCampo: String,
    val tipoDato: TipoDato,
    val obligatorio: Boolean,
    val activo: Boolean = true,
    val orden: Int,
    val esPredefinido: Boolean,
    val longitudMinima: Int? = null,
    val longitudMaxima: Int? = null,
    val cantidadDecimales: Int? = null,
    val moneda: Moneda? = null,
    val opcionesLista: List<String>? = null,
    val fechaCreacion: Long = System.currentTimeMillis()
)
