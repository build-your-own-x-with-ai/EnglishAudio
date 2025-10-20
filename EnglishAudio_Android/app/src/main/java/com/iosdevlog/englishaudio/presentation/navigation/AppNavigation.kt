package com.iosdevlog.englishaudio.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.iosdevlog.englishaudio.data.model.Category
import com.iosdevlog.englishaudio.data.model.Grade
import com.iosdevlog.englishaudio.presentation.screen.category.CategorySelectionScreen
import com.iosdevlog.englishaudio.presentation.screen.category.CategoryViewModel
import com.iosdevlog.englishaudio.presentation.screen.grade.GradeSelectionScreen
import com.iosdevlog.englishaudio.presentation.screen.grade.GradeViewModel
import com.iosdevlog.englishaudio.presentation.screen.player.AudioPlayerBar
import com.iosdevlog.englishaudio.presentation.screen.player.AudioPlayerViewModel
import com.iosdevlog.englishaudio.presentation.screen.unit.UnitGridScreen
import com.iosdevlog.englishaudio.presentation.screen.unit.UnitViewModel
import com.iosdevlog.englishaudio.presentation.viewmodel.ViewModelFactory

/**
 * Navigation routes for the app
 * Requirements: 1.2, 2.2, 3.5
 */
object NavigationRoutes {
    const val GRADE_SELECTION = "grade_selection"
    const val CATEGORY_SELECTION = "category_selection/{grade}"
    const val UNIT_GRID = "unit_grid/{grade}/{category}"
    
    fun categorySelection(grade: Grade): String {
        return "category_selection/${grade.name}"
    }
    
    fun unitGrid(grade: Grade, category: Category): String {
        return "unit_grid/${grade.name}/${category.name}"
    }
}

/**
 * Main navigation composable for the app
 * Integrates AudioPlayerBar as bottom bar across all screens
 * Requirements: 1.2, 2.2, 3.5, 8.1, 8.2
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val factory = ViewModelFactory(context)
    val audioPlayerViewModel: AudioPlayerViewModel = viewModel(factory = factory)
    Scaffold(
        bottomBar = {
            // AudioPlayerBar is displayed across all screens
            // Requirements: 8.1, 8.2
            AudioPlayerBar(viewModel = audioPlayerViewModel)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = NavigationRoutes.GRADE_SELECTION
            ) {
                // Grade Selection Screen
                // Requirements: 1.2
                composable(NavigationRoutes.GRADE_SELECTION) {
                    val gradeViewModel: GradeViewModel = viewModel(factory = factory)
                    GradeSelectionScreen(
                        viewModel = gradeViewModel,
                        onGradeSelected = { grade ->
                            navController.navigate(NavigationRoutes.categorySelection(grade))
                        }
                    )
                }
                
                // Category Selection Screen
                // Requirements: 2.2
                composable(
                    route = NavigationRoutes.CATEGORY_SELECTION,
                    arguments = listOf(
                        navArgument("grade") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val gradeString = backStackEntry.arguments?.getString("grade")
                    val grade = gradeString?.let { 
                        try {
                            Grade.valueOf(it)
                        } catch (e: IllegalArgumentException) {
                            null
                        }
                    }
                    
                    if (grade != null) {
                        val categoryViewModel: CategoryViewModel = viewModel(factory = factory)
                        CategorySelectionScreen(
                            grade = grade,
                            viewModel = categoryViewModel,
                            onCategorySelected = { category ->
                                navController.navigate(NavigationRoutes.unitGrid(grade, category))
                            },
                            onBackPressed = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
                
                // Unit Grid Screen
                // Requirements: 3.5
                composable(
                    route = NavigationRoutes.UNIT_GRID,
                    arguments = listOf(
                        navArgument("grade") { type = NavType.StringType },
                        navArgument("category") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val gradeString = backStackEntry.arguments?.getString("grade")
                    val categoryString = backStackEntry.arguments?.getString("category")
                    
                    val grade = gradeString?.let {
                        try {
                            Grade.valueOf(it)
                        } catch (e: IllegalArgumentException) {
                            null
                        }
                    }
                    
                    val category = categoryString?.let {
                        try {
                            Category.valueOf(it)
                        } catch (e: IllegalArgumentException) {
                            null
                        }
                    }
                    
                    if (grade != null && category != null) {
                        val unitViewModel: UnitViewModel = viewModel(factory = factory)
                        UnitGridScreen(
                            grade = grade,
                            category = category,
                            viewModel = unitViewModel,
                            onUnitSelected = { audioFile ->
                                // Play the selected audio file
                                audioPlayerViewModel.play(audioFile)
                            },
                            onBackPressed = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
