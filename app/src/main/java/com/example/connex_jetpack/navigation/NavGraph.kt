package com.example.connex_jetpack.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.connex_jetpack.ui.components.BottomBar
import com.example.connex_jetpack.ui.components.OfertaCard
import com.example.connex_jetpack.ui.screens.*
import com.example.connex_jetpack.ui.theme.*

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController,
        startDestination = "bienvenidos"
    ) { // Cambiamos la pantalla login por bienvenidos

        composable("login") { LoginScreen(navController) }
        composable("registro") { RegisterScreen(navController) }
        composable("registro_trabajador") { RegisterTrabajadorScreen(navController) }
        composable("registro_empresa") { RegisterEmpresaScreen(navController) }
        composable("profiletrabajador") { ProfileTrabajador(navController) }
        composable("profileempresa") { ProfileEmpresa(navController) }
        composable("cards_trabajador") { SwipeOfertasScreen(navController) }
        composable("cards_empresa") { TrabajadorCardScreen(navController) }
        composable("crear_oferta") { CrearOferta(navController) }
        composable("oferta_publicada") { OfertaPublicadaScreen(navController) }
        composable("registro_completado") { RegistroCompletadoScreen(navController) }
        composable("filtros_trabajador") { FiltrosTrabajadorScreen(navController) }
        composable("filtros_empresa") { FiltrosEmpresaScreen(navController) }
        composable("login_telefono") { LoginTelefonoScreen(navController) }
        composable("pantalla_match") { PantallaMatch(navController) }
            composable("bienvenidos") { PantallaBienvenidaScreen(navController) }
            composable("cards_filtro_empresa/{idOferta}") { backStackEntry ->
                val idOferta = backStackEntry.arguments?.getString("idOferta")
                idOferta?.let {
                    CardsFiltradasEmpresa(navController, it)
                }

            }
        }
    }





