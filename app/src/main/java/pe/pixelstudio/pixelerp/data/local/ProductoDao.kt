package pe.pixelstudio.pixelerp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pe.pixelstudio.pixelerp.data.model.*

@Dao
interface ProductoDao {
    // Grupos
    @Query("SELECT * FROM grupos_productos ORDER BY nombre ASC")
    fun obtenerTodosLosGrupos(): Flow<List<GrupoProducto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarGrupo(grupo: GrupoProducto): Long

    @Update
    suspend fun actualizarGrupo(grupo: GrupoProducto)

    // Campos
    @Query("SELECT * FROM campos_producto WHERE grupoProductoId = :grupoId ORDER BY orden ASC")
    fun obtenerCamposPorGrupo(grupoId: Int): Flow<List<CampoProducto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCampo(campo: CampoProducto)

    @Update
    suspend fun actualizarCampo(campo: CampoProducto)

    @Delete
    suspend fun eliminarCampo(campo: CampoProducto)

    // Productos
    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun obtenerTodosLosProductos(): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE grupoProductoId = :grupoId ORDER BY nombre ASC")
    fun obtenerProductosPorGrupo(grupoId: Int): Flow<List<Producto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProducto(producto: Producto): Long

    @Update
    suspend fun actualizarProducto(producto: Producto)

    // Valores dinámicos
    @Query("SELECT * FROM valores_campo_producto WHERE productoId = :productoId")
    suspend fun obtenerValoresPorProducto(productoId: Int): List<ValorCampoProducto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarValorCampo(valor: ValorCampoProducto)
}
