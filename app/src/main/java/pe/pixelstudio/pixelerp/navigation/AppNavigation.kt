package pe.pixelstudio.pixelerp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pe.pixelstudio.pixelerp.ui.dashboard.DashboardScreen
import pe.pixelstudio.pixelerp.ui.login.LoginScreen
import pe.pixelstudio.pixelerp.ui.login.LoginViewModel
import pe.pixelstudio.pixelerp.ui.productos.grupos.GrupoViewModel
import pe.pixelstudio.pixelerp.ui.productos.lista.ProductoViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object ProductosMenu : Screen("productos_menu")
    object GruposLista : Screen("grupos_lista")
    object GruposForm : Screen("grupos_form")
    object GrupoDetalle : Screen("grupo_detalle/{grupoId}") {
        fun createRoute(grupoId: Int) = "grupo_detalle/$grupoId"
    }
    object ProductosLista : Screen("productos_lista")
    object ProductoForm : Screen("producto_form")
}

@Composable
fun AppNavigation(
    loginViewModel: LoginViewModel,
    grupoViewModel: GrupoViewModel,
    productoViewModel: ProductoViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToProductos = { navController.navigate(Screen.ProductosMenu.route) }
            )
        }
        composable(Screen.ProductosMenu.route) {
            pe.pixelstudio.pixelerp.ui.productos.ProductosMenuScreen(
                onNavigateToGrupos = { navController.navigate(Screen.GruposLista.route) },
                onNavigateToLista = { navController.navigate(Screen.ProductosLista.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.GruposLista.route) {
            pe.pixelstudio.pixelerp.ui.productos.grupos.GrupoListScreen(
                viewModel = grupoViewModel,
                onAddGrupo = { navController.navigate(Screen.GruposForm.route) },
                onConfigurarCampos = { grupoId -> navController.navigate(Screen.GrupoDetalle.createRoute(grupoId)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.GruposForm.route) {
            pe.pixelstudio.pixelerp.ui.productos.grupos.GrupoFormScreen(
                viewModel = grupoViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            Screen.GrupoDetalle.route,
            arguments = listOf(navArgument("grupoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val grupoId = backStackEntry.arguments?.getInt("grupoId") ?: 0
            pe.pixelstudio.pixelerp.ui.productos.grupos.ConfiguracionCamposScreen(
                grupoId = grupoId,
                viewModel = grupoViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.ProductosLista.route) {
            pe.pixelstudio.pixelerp.ui.productos.lista.ProductoListScreen(
                viewModel = productoViewModel,
                onAddProducto = { navController.navigate(Screen.ProductoForm.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.ProductoForm.route) {
            pe.pixelstudio.pixelerp.ui.productos.lista.ProductoFormScreen(
                viewModel = productoViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
