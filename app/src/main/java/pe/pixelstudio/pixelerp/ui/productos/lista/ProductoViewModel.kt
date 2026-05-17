package pe.pixelstudio.pixelerp.ui.productos.lista

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pe.pixelstudio.pixelerp.data.model.*
import pe.pixelstudio.pixelerp.data.repository.ProductoRepository

class ProductoViewModel(private val repository: ProductoRepository) : ViewModel() {

    private val _busqueda = MutableStateFlow("")
    val busqueda = _busqueda.asStateFlow()

    private val _filtroGrupo = MutableStateFlow<Int?>(null)
    val filtroGrupo = _filtroGrupo.asStateFlow()

    val productos: StateFlow<List<Producto>> = combine(
        repository.todosLosProductos,
        _busqueda,
        _filtroGrupo
    ) { lista, query, grupoId ->
        lista.filter { producto ->
            (query.isBlank() || producto.nombre.contains(query, ignoreCase = true)) &&
            (grupoId == null || producto.grupoProductoId == grupoId)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val grupos: StateFlow<List<GrupoProducto>> = repository.todosLosGrupos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Formulario de Producto
    var selectedGrupoId by mutableStateOf<Int?>(null)
    var nombreProducto by mutableStateOf("")
    var precioVenta by mutableStateOf("")
    var imagenUri by mutableStateOf<String?>(null)

    // Valores dinámicos del formulario
    val valoresDinamicos = mutableStateMapOf<Int, Any>()

    fun onBusquedaChange(nuevaBusqueda: String) {
        _busqueda.value = nuevaBusqueda
    }

    fun onFiltroGrupoChange(grupoId: Int?) {
        _filtroGrupo.value = grupoId
    }

    fun obtenerCamposActivos(grupoId: Int): Flow<List<CampoProducto>> {
        return repository.obtenerCamposPorGrupo(grupoId).map { campos ->
            campos.filter { it.activo }
        }
    }

    fun guardarProducto(onSuccess: () -> Unit) {
        val grupoId = selectedGrupoId ?: return
        val precio = precioVenta.toDoubleOrNull() ?: 0.0

        viewModelScope.launch {
            val producto = Producto(
                grupoProductoId = grupoId,
                nombre = nombreProducto,
                precioVenta = precio,
                imagenUri = imagenUri
            )

            val valores = valoresDinamicos.map { (campoId, valor) ->
                // Mapeo básico de valor según el tipo de dato sería ideal aquí
                // Por simplicidad en esta fase, guardaremos según el tipo detectado
                when (valor) {
                    is String -> ValorCampoProducto(productoId = 0, campoProductoId = campoId, valorTexto = valor)
                    is Int -> ValorCampoProducto(productoId = 0, campoProductoId = campoId, valorEntero = valor)
                    is Double -> ValorCampoProducto(productoId = 0, campoProductoId = campoId, valorDecimal = valor)
                    is Long -> ValorCampoProducto(productoId = 0, campoProductoId = campoId, valorFecha = valor)
                    is Boolean -> ValorCampoProducto(productoId = 0, campoProductoId = campoId, valorBooleano = valor)
                    else -> ValorCampoProducto(productoId = 0, campoProductoId = campoId, valorTexto = valor.toString())
                }
            }

            repository.guardarProductoCompleto(producto, valores)
            resetForm()
            onSuccess()
        }
    }

    private fun resetForm() {
        selectedGrupoId = null
        nombreProducto = ""
        precioVenta = ""
        imagenUri = null
        valoresDinamicos.clear()
    }
}
