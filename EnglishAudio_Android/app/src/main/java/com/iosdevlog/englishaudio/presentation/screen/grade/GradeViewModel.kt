package com.iosdevlog.englishaudio.presentation.screen.grade

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iosdevlog.englishaudio.data.model.Grade
import com.iosdevlog.englishaudio.domain.usecase.GetGradesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Grade Selection Screen
 * Requirements: 1.1
 */
class GradeViewModel(
    private val getGradesUseCase: GetGradesUseCase
) : ViewModel() {

    private val _grades = MutableStateFlow<List<Grade>>(emptyList())
    val grades: StateFlow<List<Grade>> = _grades.asStateFlow()

    init {
        loadGrades()
    }

    private fun loadGrades() {
        viewModelScope.launch {
            _grades.value = getGradesUseCase()
        }
    }
}
