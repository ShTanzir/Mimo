package com.mimo.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.mimo.app.ui.appdetail.AppDetailScreen
import com.mimo.app.ui.applist.AppListScreen
import com.mimo.app.ui.onboarding.OnboardingScreen
import com.mimo.app.ui.permissions.PermissionsScreen
import com.mimo.app.ui.settings.SettingsScreen
import com.mimo.app.ui.stats.StatsScreen
import com.mimo.app.util.Prefs
import kotlinx.coroutines.launch

object Routes {
    const val ONBOARDING = "onboarding"
    const val PERMISSIONS = "permissions"
    const val APP_LIST = "app_list"
    const val APP_DETAIL = "app_detail/{packageName}"
    const val STATS = "stats"
    const val SETTINGS = "settings"

    fun appDetail(packageName: String) = "app_detail/$packageName"
}

@Composable
fun MimoNavGraph(startDestination: String) {
    val navController: NavHostController = rememberNavController()
    val context = LocalContext.current
    val prefs = remember { Prefs(context) }
    val scope = rememberCoroutineScope()

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(onFinished = {
                scope.launch { prefs.setOnboardingDone(true) }
                navController.navigate(Routes.PERMISSIONS) {
                    popUpTo(Routes.ONBOARDING) { inclusive = true }
                }
            })
        }
        composable(Routes.PERMISSIONS) {
            PermissionsScreen(onContinue = {
                navController.navigate(Routes.APP_LIST) {
                    popUpTo(Routes.PERMISSIONS) { inclusive = true }
                }
            })
        }
        composable(Routes.APP_LIST) {
            AppListScreen(
                onAppClick = { pkg -> navController.navigate(Routes.appDetail(pkg)) },
                onOpenStats = { navController.navigate(Routes.STATS) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }
        composable(
            Routes.APP_DETAIL,
            arguments = listOf(navArgument("packageName") { type = NavType.StringType })
        ) { backStackEntry ->
            val pkg = backStackEntry.arguments?.getString("packageName") ?: return@composable
            AppDetailScreen(packageName = pkg, onBack = { navController.popBackStack() })
        }
        composable(Routes.STATS) {
            StatsScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onOpenPermissions = { navController.navigate(Routes.PERMISSIONS) }
            )
        }
    }
}
