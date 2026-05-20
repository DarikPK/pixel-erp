package pe.pixelstudio.pixelerp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pe.pixelstudio.pixelerp.data.local.AppDatabase
import pe.pixelstudio.pixelerp.data.remote.FirebaseRepository
import pe.pixelstudio.pixelerp.data.repository.ProductoRepository
import pe.pixelstudio.pixelerp.data.repository.UsuarioRepository
import pe.pixelstudio.pixelerp.navigation.AppNavigation
import pe.pixelstudio.pixelerp.ui.login.LoginViewModel
import pe.pixelstudio.pixelerp.ui.productos.grupos.GrupoViewModel
import pe.pixelstudio.pixelerp.ui.productos.lista.ProductoViewModel
import pe.pixelstudio.pixelerp.ui.superadmin.SuperAdminViewModel
import pe.pixelstudio.pixelerp.ui.usuarios.UsuarioViewModel
import pe.pixelstudio.pixelerp.ui.theme.PixelERPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val productoRepository = ProductoRepository(database.productoDao())
        val firebaseRepository = FirebaseRepository()
        val sharedPrefs = getSharedPreferences("pixel_erp_prefs", Context.MODE_PRIVATE)

        @Suppress("UNCHECKED_CAST")
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return when {
                    modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                        LoginViewModel(firebaseRepository, sharedPrefs) as T
                    modelClass.isAssignableFrom(SuperAdminViewModel::class.java) ->
                        SuperAdminViewModel(firebaseRepository) as T
                    modelClass.isAssignableFrom(GrupoViewModel::class.java) ->
                        GrupoViewModel(productoRepository) as T
                    modelClass.isAssignableFrom(ProductoViewModel::class.java) ->
                        ProductoViewModel(productoRepository) as T
                    modelClass.isAssignableFrom(UsuarioViewModel::class.java) ->
                        UsuarioViewModel(firebaseRepository) as T
                    else -> throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }

        val loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
        val superAdminViewModel = ViewModelProvider(this, factory)[SuperAdminViewModel::class.java]
        val grupoViewModel = ViewModelProvider(this, factory)[GrupoViewModel::class.java]
        val productoViewModel = ViewModelProvider(this, factory)[ProductoViewModel::class.java]
        val usuarioViewModel = ViewModelProvider(this, factory)[UsuarioViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            PixelERPTheme {
                AppNavigation(
                    loginViewModel = loginViewModel,
                    superAdminViewModel = superAdminViewModel,
                    grupoViewModel = grupoViewModel,
                    productoViewModel = productoViewModel,
                    usuarioViewModel = usuarioViewModel
                )
            }
        }
    }
}
