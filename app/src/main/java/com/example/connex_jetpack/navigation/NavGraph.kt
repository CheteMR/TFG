package com.example.connex_jetpack.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.connex_jetpack.ui.components.BottomBar
import com.example.connex_jetpack.ui.screens.CardsFiltradasEmpresa
import com.example.connex_jetpack.ui.screens.ChatScreen
import com.example.connex_jetpack.ui.screens.ChatsListScreen
import com.example.connex_jetpack.ui.screens.CrearOferta
import com.example.connex_jetpack.ui.screens.FiltrosEmpresaScreen
import com.example.connex_jetpack.ui.screens.FiltrosTrabajadorScreen
import com.example.connex_jetpack.ui.screens.OfertaPublicadaScreen
import com.example.connex_jetpack.ui.theme.LoginScreen
import com.example.connex_jetpack.ui.theme.LoginTelefonoScreen
import com.example.connex_jetpack.ui.theme.PantallaBienvenidaScreen
import com.example.connex_jetpack.ui.screens.PantallaMatch
import com.example.connex_jetpack.ui.screens.RegistroCompletadoScreen
import com.example.connex_jetpack.ui.theme.ProfileEmpresa
import com.example.connex_jetpack.ui.theme.ProfileTrabajador
import com.example.connex_jetpack.ui.theme.RegisterEmpresaScreen
import com.example.connex_jetpack.ui.theme.RegisterScreen
import com.example.connex_jetpack.ui.theme.RegisterTrabajadorScreen
import com.example.connex_jetpack.ui.screens.SwipeCandidatosScreen
import com.example.connex_jetpack.ui.screens.SwipeOfertasScreen
import com.example.connex_jetpack.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "bienvenidos"
    ) {
        // Autenticación y Registro
        composable("login") { LoginScreen(navController) }
        composable("registro") { RegisterScreen(navController) }
        composable("registro_trabajador") { RegisterTrabajadorScreen(navController) }
        composable("registro_empresa") { RegisterEmpresaScreen(navController) }

        // Perfil
        composable("profiletrabajador") { ProfileTrabajador(navController) }
        composable("profileempresa") { ProfileEmpresa(navController) }

        // Ofertas
        composable("cards_trabajador") { SwipeOfertasScreen(navController) }
        composable(
            route = "cards_empresa/{ofertaId}/{empresaId}",
            arguments = listOf(
                navArgument("ofertaId") { type = NavType.StringType },
                navArgument("empresaId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val ofertaId = backStackEntry.arguments?.getString("ofertaId") ?: ""
            val empresaId = backStackEntry.arguments?.getString("empresaId") ?: ""
            SwipeCandidatosScreen(navController, ofertaId, empresaId)
        }

        // Crear oferta y vistas tras publicación
        composable("crear_oferta") { CrearOferta(navController) }
        composable("oferta_publicada") { OfertaPublicadaScreen(navController) }
        composable("registro_completado") { RegistroCompletadoScreen(navController) }

        // Filtros
        composable("filtros_trabajador") { FiltrosTrabajadorScreen(navController) }
        composable("filtros_empresa") { FiltrosEmpresaScreen(navController) }

        // Login por teléfono
        composable("login_telefono") { LoginTelefonoScreen(navController) }

        // Match
        composable(
            route = "pantalla_match/{ofertaId}/{trabajadorId}/{empresaId}",
            arguments = listOf(
                navArgument("ofertaId") { type = NavType.StringType },
                navArgument("trabajadorId") { type = NavType.StringType },
                navArgument("empresaId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val ofertaId = backStackEntry.arguments?.getString("ofertaId") ?: ""
            val trabajadorId = backStackEntry.arguments?.getString("trabajadorId") ?: ""
            val empresaId = backStackEntry.arguments?.getString("empresaId") ?: ""
            PantallaMatch(navController, trabajadorId, empresaId, ofertaId)
        }

        // Filtros específicos de empresa
        composable(
            route = "cards_filtro_empresa/{idOferta}",
            arguments = listOf(navArgument("idOferta") { type = NavType.StringType })
        ) { backStackEntry ->
            val idOferta = backStackEntry.arguments?.getString("idOferta") ?: ""
            CardsFiltradasEmpresa(navController, idOferta)
        }

        // Lista de chats
        composable("chats_list") { ChatsListScreen(navController) }

        // Chat individual
        composable(
            route = "chat/{chatId}",
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            ChatScreen(navController, chatId)
        }

        // Pantalla bienvenida
        composable("bienvenidos") { PantallaBienvenidaScreen(navController) }
    }
}







