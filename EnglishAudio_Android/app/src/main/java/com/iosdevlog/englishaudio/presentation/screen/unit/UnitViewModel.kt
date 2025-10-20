package com.iosdevlog.englishaudio.presentation.screen.unit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iosdevlog.englishaudio.data.model.AudioFile
import com.iosdevlog.englishaudio.data.model.Category
import com.iosdevlog.englishaudio.data.model.Grade
import com.iosdevlog.englishaudio.domain.usecase.GetUnitsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Unit Grid Screen
 * Requirements: 3.1, 3.2, 3.3
 */
class UnitViewModel(
    private val getUnitsUseCase: GetUnitsUseCase
) : ViewModel() {

    private val _units = MutableStateFlow<List<AudioFile>>(emptyList())
    val units: StateFlow<List<AudioFile>> = _units.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Load units for the selected grade and category
     * Handles empty or missing unit scenarios
     */
    fun loadUnits(grade: Grade, category: Category) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val loadedUnits = getUnitsUseCase(grade, category)
                _units.value = loadedUnits
                
                // Handle empty scenario
                if (loadedUnits.isEmpty()) {
                    _errorMessage.value = "没有找到音频文件"
                }
            } catch (e: Exception) {
                _errorMessage.value = "加载失败: ${e.message}"
                _units.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
