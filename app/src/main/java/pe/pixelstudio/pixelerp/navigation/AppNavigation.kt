package pe.pixelstudio.pixelerp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import pe.pixelstudio.pixelerp.ui.superadmin.SuperAdminViewModel
import pe.pixelstudio.pixelerp.ui.superadmin.NegocioListScreen
import pe.pixelstudio.pixelerp.ui.superadmin.NegocioFormScreen
import pe.pixelstudio.pixelerp.ui.usuarios.UsuarioViewModel
import pe.pixelstudio.pixelerp.ui.usuarios.UsuarioListScreen
import pe.pixelstudio.pixelerp.ui.usuarios.UsuarioFormScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object SuperAdminNegocios : Screen("sa_negocios")
    object SuperAdminCrearNegocio : Screen("sa_crear_negocio")
    object SuperAdminEditarNegocio : Screen("sa_editar_negocio")
    object ProductosMenu : Screen("productos_menu")
    object GruposLista : Screen("grupos_lista")
    object GruposForm : Screen("grupos_form")
    object GrupoDetalle : Screen("grupo_detalle/{grupoId}") {
        fun createRoute(grupoId: Int) = "grupo_detalle/$grupoId"
    }
    object ProductosLista : Screen("productos_lista")
    object ProductoForm : Screen("producto_form")
    object UsuariosLista : Screen("usuarios_lista")
    object UsuarioForm : Screen("usuario_form")
}

@Composable
fun AppNavigation(
    loginViewModel: LoginViewModel,
    superAdminViewModel: SuperAdminViewModel,
    grupoViewModel: GrupoViewModel,
    productoViewModel: ProductoViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    val navController = rememberNavController()

    LaunchedEffect(loginViewModel.logoutTriggered) {
        if (loginViewModel.logoutTriggered) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
            loginViewModel.resetLogoutStatus()
        }
    }

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
                negocio = loginViewModel.negocioActual,
                usuario = loginViewModel.usuarioActual,
                onNavigateToProductos = { navController.navigate(Screen.ProductosMenu.route) },
                onNavigateToSuperAdmin = { navController.navigate(Screen.SuperAdminNegocios.route) },
                onNavigateToUsuarios = { navController.navigate(Screen.UsuariosLista.route) },
                onLogout = { loginViewModel.logout() }
            )
        }

        // Super Admin
        composable(Screen.SuperAdminNegocios.route) {
            NegocioListScreen(
                viewModel = superAdminViewModel,
                onCrearNegocio = { navController.navigate(Screen.SuperAdminCrearNegocio.route) },
                onEditarNegocio = { negocio ->
                    superAdminViewModel.negocioSeleccionado = negocio
                    navController.navigate(Screen.SuperAdminEditarNegocio.route)
                },
                onIngresarNegocio = { negocio ->
                    loginViewModel.negocioActual = negocio
                    navController.navigate(Screen.Dashboard.route)
                },
                onBack = { navController.popBackStack() },
                onLogout = { loginViewModel.logout() }
            )
        }
        composable(Screen.SuperAdminEditarNegocio.route) {
            NegocioFormScreen(
                viewModel = superAdminViewModel,
                negocioAEditar = superAdminViewModel.negocioSeleccionado,
                onBack = { navController.popBackStack() },
                onLogout = { loginViewModel.logout() }
            )
        }
        composable(Screen.SuperAdminCrearNegocio.route) {
            NegocioFormScreen(
                viewModel = superAdminViewModel,
                onBack = { navController.popBackStack() },
                onLogout = { loginViewModel.logout() }
            )
        }

        composable(Screen.ProductosMenu.route) {
            pe.pixelstudio.pixelerp.ui.productos.ProductosMenuScreen(
                usuario = loginViewModel.usuarioActual,
                onNavigateToGrupos = { navController.navigate(Screen.GruposLista.route) },
                onNavigateToLista = { navController.navigate(Screen.ProductosLista.route) },
                onBack = { navController.popBackStack() },
                onLogout = { loginViewModel.logout() }
            )
        }
        composable(Screen.GruposLista.route) {
            pe.pixelstudio.pixelerp.ui.productos.grupos.GrupoListScreen(
                viewModel = grupoViewModel,
                onAddGrupo = { navController.navigate(Screen.GruposForm.route) },
                onConfigurarCampos = { grupoId -> navController.navigate(Screen.GrupoDetalle.createRoute(grupoId)) },
                onBack = { navController.popBackStack() },
                onLogout = { loginViewModel.logout() }
            )
        }
        composable(Screen.GruposForm.route) {
            pe.pixelstudio.pixelerp.ui.productos.grupos.GrupoFormScreen(
                viewModel = grupoViewModel,
                onBack = { navController.popBackStack() },
                onLogout = { loginViewModel.logout() }
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
                onBack = { navController.popBackStack() },
                onLogout = { loginViewModel.logout() }
            )
        }
        composable(Screen.ProductosLista.route) {
            pe.pixelstudio.pixelerp.ui.productos.lista.ProductoListScreen(
                viewModel = productoViewModel,
                onAddProducto = { navController.navigate(Screen.ProductoForm.route) },
                onBack = { navController.popBackStack() },
                onLogout = { loginViewModel.logout() }
            )
        }
        composable(Screen.ProductoForm.route) {
            pe.pixelstudio.pixelerp.ui.productos.lista.ProductoFormScreen(
                viewModel = productoViewModel,
                onBack = { navController.popBackStack() },
                onLogout = { loginViewModel.logout() }
            )
        }

        composable(Screen.UsuariosLista.route) {
            UsuarioListScreen(
                viewModel = usuarioViewModel,
                negocioId = loginViewModel.negocioActual?.id ?: "",
                onAddUsuario = {
                    usuarioViewModel.limpiarFormulario()
                    navController.navigate(Screen.UsuarioForm.route)
                },
                onEditUsuario = { u ->
                    usuarioViewModel.prepararEdicion(u)
                    navController.navigate(Screen.UsuarioForm.route)
                },
                onBack = { navController.popBackStack() },
                onLogout = { loginViewModel.logout() }
            )
        }

        composable(Screen.UsuarioForm.route) {
            UsuarioFormScreen(
                viewModel = usuarioViewModel,
                negocioId = loginViewModel.negocioActual?.id ?: "",
                onBack = { navController.popBackStack() },
                onLogout = { loginViewModel.logout() }
            )
        }
    }
}
