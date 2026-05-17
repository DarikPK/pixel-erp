package pe.pixelstudio.pixelerp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pe.pixelstudio.pixelerp.data.model.Usuario

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios WHERE usuario = :usuario LIMIT 1")
    suspend fun obtenerPorUsuario(usuario: String): Usuario?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(usuario: Usuario)

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contarUsuarios(): Int
}
