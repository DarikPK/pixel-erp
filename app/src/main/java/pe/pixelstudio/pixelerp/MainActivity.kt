package pe.pixelstudio.pixelerp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pe.pixelstudio.pixelerp.data.local.AppDatabase
import pe.pixelstudio.pixelerp.data.repository.UsuarioRepository
import pe.pixelstudio.pixelerp.navigation.AppNavigation
import pe.pixelstudio.pixelerp.ui.login.LoginViewModel
import pe.pixelstudio.pixelerp.ui.theme.PixelERPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val repository = UsuarioRepository(database.usuarioDao())

        @Suppress("UNCHECKED_CAST")
        val loginViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(repository) as T
            }
        })[LoginViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            PixelERPTheme {
                AppNavigation(loginViewModel = loginViewModel)
            }
        }
    }
}
