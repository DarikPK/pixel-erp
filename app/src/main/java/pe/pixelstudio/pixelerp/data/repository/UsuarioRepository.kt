package pe.pixelstudio.pixelerp.data.repository

import pe.pixelstudio.pixelerp.data.local.UsuarioDao
import pe.pixelstudio.pixelerp.data.model.Usuario

class UsuarioRepository(private val usuarioDao: UsuarioDao) {
    suspend fun obtenerUsuario(usuario: String): Usuario? {
        return usuarioDao.obtenerPorUsuario(usuario)
    }
}
