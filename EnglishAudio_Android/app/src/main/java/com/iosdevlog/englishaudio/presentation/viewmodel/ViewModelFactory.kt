package com.iosdevlog.englishaudio.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.iosdevlog.englishaudio.data.repository.AudioRepositoryImpl
import com.iosdevlog.englishaudio.data.source.LocalAudioDataSource
import com.iosdevlog.englishaudio.domain.usecase.GetCategoriesUseCase
import com.iosdevlog.englishaudio.domain.usecase.GetGradesUseCase
import com.iosdevlog.englishaudio.domain.usecase.GetUnitsUseCase
import com.iosdevlog.englishaudio.presentation.screen.category.CategoryViewModel
import com.iosdevlog.englishaudio.presentation.screen.grade.GradeViewModel
import com.iosdevlog.englishaudio.presentation.screen.player.AudioPlayerViewModel
import com.iosdevlog.englishaudio.presentation.screen.unit.UnitViewModel
import com.iosdevlog.englishaudio.service.AudioPlayerService

/**
 * Factory for creating ViewModels with dependencies
 * This provides manual dependency injection for the app
 */
class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    
    // Lazy initialization of dependencies
    private val localDataSource by lazy { LocalAudioDataSource(context) }
    private val audioRepository by lazy { AudioRepositoryImpl(localDataSource) }
    private val audioPlayerService by lazy { AudioPlayerService(context) }
    
    // Use cases
    private val getGradesUseCase by lazy { GetGradesUseCase(audioRepository) }
    private val getCategoriesUseCase by lazy { GetCategoriesUseCase(audioRepository) }
    private val getUnitsUseCase by lazy { GetUnitsUseCase(audioRepository) }
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(GradeViewModel::class.java) -> {
                GradeViewModel(getGradesUseCase) as T
            }
            modelClass.isAssignableFrom(CategoryViewModel::class.java) -> {
                CategoryViewModel(getCategoriesUseCase) as T
            }
            modelClass.isAssignableFrom(UnitViewModel::class.java) -> {
                UnitViewModel(getUnitsUseCase) as T
            }
            modelClass.isAssignableFrom(AudioPlayerViewModel::class.java) -> {
                AudioPlayerViewModel(audioPlayerService) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
