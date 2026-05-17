package pe.pixelstudio.pixelerp.ui.productos.grupos

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pe.pixelstudio.pixelerp.data.model.CampoProducto
import pe.pixelstudio.pixelerp.data.model.GrupoProducto
import pe.pixelstudio.pixelerp.data.repository.ProductoRepository

class GrupoViewModel(private val repository: ProductoRepository) : ViewModel() {

    val grupos: StateFlow<List<GrupoProducto>> = repository.todosLosGrupos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var nombreGrupo by mutableStateOf("")
    var descripcionGrupo by mutableStateOf("")
    var grupoActivo by mutableStateOf(true)

    fun guardarGrupo(onSuccess: () -> Unit) {
        if (nombreGrupo.isBlank()) return

        viewModelScope.launch {
            repository.insertarGrupo(
                GrupoProducto(
                    nombre = nombreGrupo,
                    descripcion = descripcionGrupo,
                    activo = grupoActivo
                )
            )
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
