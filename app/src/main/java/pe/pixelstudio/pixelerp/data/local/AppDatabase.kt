package pe.pixelstudio.pixelerp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pe.pixelstudio.pixelerp.data.model.*

@Database(
    entities = [
        Usuario::class,
        GrupoProducto::class,
        CampoProducto::class,
        Producto::class,
        ValorCampoProducto::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun productoDao(): ProductoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pixel_erp_db"
                )
                .fallbackToDestructiveMigration()
                .build()

                INSTANCE = instance

                // Asegurar usuario admin al obtener la base de datos
                CoroutineScope(Dispatchers.IO).launch {
                    val usuarioDao = instance.usuarioDao()
                    if (usuarioDao.contarUsuarios() == 0) {
                        usuarioDao.insertar(
                            Usuario(
                                nombre = "Administrador",
                                usuario = "admin",
                                contrasena = "admin123",
                                rol = Rol.ADMINISTRADOR,
                                activo = true
                            )
                        )
                    }
                }

                instance
            }
        }
    }
}
