package pe.pixelstudio.pixelerp.data.repository

import kotlinx.coroutines.flow.Flow
import pe.pixelstudio.pixelerp.data.local.ProductoDao
import pe.pixelstudio.pixelerp.data.model.*

class ProductoRepository(private val productoDao: ProductoDao) {

    // Grupos
    val todosLosGrupos: Flow<List<GrupoProducto>> = productoDao.obtenerTodosLosGrupos()

    suspend fun insertarGrupo(grupo: GrupoProducto): Long {
        return productoDao.insertarGrupo(grupo)
    }

    suspend fun actualizarGrupo(grupo: GrupoProducto) {
        productoDao.actualizarGrupo(grupo)
    }

    suspend fun guardarGrupoConCampos(grupo: GrupoProducto, campos: List<CampoProducto>) {
        val grupoId = productoDao.insertarGrupo(grupo).toInt()
        campos.forEach { campo ->
            productoDao.insertarCampo(campo.copy(grupoProductoId = grupoId))
        }
    }

    // Campos
    fun obtenerCamposPorGrupo(grupoId: Int): Flow<List<CampoProducto>> {
        return productoDao.obtenerCamposPorGrupo(grupoId)
    }

    suspend fun insertarCampo(campo: CampoProducto) {
        productoDao.insertarCampo(campo)
    }

    suspend fun actualizarCampo(campo: CampoProducto) {
        productoDao.actualizarCampo(campo)
    }

    // Productos
    val todosLosProductos: Flow<List<Producto>> = productoDao.obtenerTodosLosProductos()

    fun obtenerProductosPorGrupo(grupoId: Int): Flow<List<Producto>> {
        return productoDao.obtenerProductosPorGrupo(grupoId)
    }

    suspend fun guardarProductoCompleto(producto: Producto, valores: List<ValorCampoProducto>) {
        val productoId = productoDao.insertarProducto(producto).toInt()
        valores.forEach { valor ->
            productoDao.insertarValorCampo(valor.copy(productoId = if (valor.productoId == 0) productoId else valor.productoId))
        }
    }

    suspend fun obtenerValoresPorProducto(productoId: Int): List<ValorCampoProducto> {
        return productoDao.obtenerValoresPorProducto(productoId)
    }
}
