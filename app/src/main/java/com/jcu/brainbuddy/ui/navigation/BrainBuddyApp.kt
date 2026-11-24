package com.jcu.brainbuddy.ui.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jcu.brainbuddy.domain.QuizCategory
import com.jcu.brainbuddy.ui.home.HomeScreen
import com.jcu.brainbuddy.ui.quiz.QuizScreen
import com.jcu.brainbuddy.ui.result.ResultScreen
import com.jcu.brainbuddy.ui.settings.SettingsScreen
import com.jcu.brainbuddy.ui.stats.StatsScreen

private data class TopLevelDestination(val route: String, val label: String, val icon: ImageVector)

private val topLevelDestinations = listOf(
    TopLevelDestination(Routes.HOME, "Home", Icons.Filled.Home),
    TopLevelDestination(Routes.STATS, "Progress", Icons.Filled.BarChart),
    TopLevelDestination(Routes.SETTINGS, "Settings", Icons.Filled.Settings)
)

@Composable
fun BrainBuddyApp(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = topLevelDestinations.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    topLevelDestinations.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = { navController.navigateTopLevel(destination.route) },
                            icon = { Icon(destination.icon, contentDescription = null) },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(onStartQuiz = { category -> navController.navigate(Routes.quiz(category)) })
            }
            composable(Routes.STATS) { StatsScreen() }
            composable(Routes.SETTINGS) { SettingsScreen() }
            composable(
                route = Routes.QUIZ,
                arguments = listOf(navArgument(Routes.CATEGORY_ARG) { type = NavType.StringType })
            ) {
                QuizScreen(
                    onQuit = { navController.popBackStack() },
                    onFinished = { result ->
                        // Replace the quiz with its result so Back never re-opens a finished quiz.
                        navController.navigate(
                            Routes.result(result.category, result.correctAnswers, result.totalQuestions)
                        ) { popUpTo(Routes.QUIZ) { inclusive = true } }
                    }
                )
            }
            composable(
                route = Routes.RESULT,
                arguments = listOf(
                    navArgument(Routes.CATEGORY_ARG) { type = NavType.StringType },
                    navArgument(Routes.CORRECT_ARG) { type = NavType.IntType },
                    navArgument(Routes.TOTAL_ARG) { type = NavType.IntType }
                )
            ) { entry ->
                val args = entry.arguments
                val category = QuizCategory.entries.firstOrNull { it.name == args?.getString(Routes.CATEGORY_ARG) }
                    ?: QuizCategory.MATH
                ResultScreen(
                    category = category,
                    correct = args?.getInt(Routes.CORRECT_ARG) ?: 0,
                    total = args?.getInt(Routes.TOTAL_ARG) ?: 0,
                    onPlayAgain = {
                        navController.navigate(Routes.quiz(category)) {
                            popUpTo(Routes.RESULT) { inclusive = true }
                        }
                    },
                    onHome = { navController.navigateTopLevel(Routes.HOME) }
                )
            }
        }
    }
}

private fun NavHostController.navigateTopLevel(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
