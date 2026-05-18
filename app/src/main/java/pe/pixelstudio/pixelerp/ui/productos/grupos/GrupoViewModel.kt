package pe.pixelstudio.pixelerp.ui.productos.grupos

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pe.pixelstudio.pixelerp.data.model.*
import pe.pixelstudio.pixelerp.data.repository.ProductoRepository

class GrupoViewModel(private val repository: ProductoRepository) : ViewModel() {

    val grupos: StateFlow<List<GrupoProducto>> = repository.todosLosGrupos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var nombreGrupo by mutableStateOf("")
    var descripcionGrupo by mutableStateOf("")
    var grupoActivo by mutableStateOf(true)

    // Lista temporal de campos para la creación del grupo
    val camposTemporales = mutableStateListOf<CampoProducto>()

    fun agregarCampoTemporal(campo: CampoProducto) {
        camposTemporales.add(campo.copy(orden = camposTemporales.size))
    }

    fun editarCampoTemporal(index: Int, campo: CampoProducto) {
        if (index in camposTemporales.indices) {
            camposTemporales[index] = campo
        }
    }

    fun removerCampoTemporal(index: Int) {
        if (index in camposTemporales.indices) {
            camposTemporales.removeAt(index)
            // Reordenar
            val listaNueva = camposTemporales.mapIndexed { i, c -> c.copy(orden = i) }
            camposTemporales.clear()
            camposTemporales.addAll(listaNueva)
        }
    }

    fun moverCampoTemporal(index: Int, arriba: Boolean) {
        val targetIndex = if (arriba) index - 1 else index + 1
        if (index in camposTemporales.indices && targetIndex in camposTemporales.indices) {
            val item = camposTemporales.removeAt(index)
            camposTemporales.add(targetIndex, item)
            // Actualizar ordenes
            val listaNueva = camposTemporales.mapIndexed { i, c -> c.copy(orden = i) }
            camposTemporales.clear()
            camposTemporales.addAll(listaNueva)
        }
    }

    fun guardarGrupo(onSuccess: () -> Unit) {
        if (nombreGrupo.isBlank()) return

        viewModelScope.launch {
            val grupo = GrupoProducto(
                nombre = nombreGrupo,
                descripcion = descripcionGrupo,
                activo = grupoActivo
            )
            repository.guardarGrupoConCampos(grupo, camposTemporales.toList())
            resetForm()
            onSuccess()
        }
    }

    fun obtenerCampos(grupoId: Int): StateFlow<List<CampoProducto>> {
        return repository.obtenerCamposPorGrupo(grupoId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun agregarCampo(campo: CampoProducto) {
        viewModelScope.launch {
            repository.insertarCampo(campo)
        }
    }

    fun actualizarCampo(campo: CampoProducto) {
        viewModelScope.launch {
            repository.actualizarCampo(campo)
        }
    }

    fun eliminarCampo(campo: CampoProducto) {
        viewModelScope.launch {
            repository.eliminarCampo(campo)
        }
    }

    fun moverCampo(grupoId: Int, campo: CampoProducto, arriba: Boolean, listaActual: List<CampoProducto>) {
        val index = listaActual.indexOfFirst { it.id == campo.id }
        val targetIndex = if (arriba) index - 1 else index + 1

        if (index != -1 && targetIndex in listaActual.indices) {
            viewModelScope.launch {
                val targetCampo = listaActual[targetIndex]
                repository.actualizarCampo(campo.copy(orden = targetCampo.orden))
                repository.actualizarCampo(targetCampo.copy(orden = campo.orden))
            }
        }
    }

    fun toggleEstadoGrupo(grupo: GrupoProducto) {
        viewModelScope.launch {
            repository.actualizarGrupo(grupo.copy(activo = !grupo.activo))
        }
    }

    private fun resetForm() {
        nombreGrupo = ""
        descripcionGrupo = ""
        grupoActivo = true
    }
}
