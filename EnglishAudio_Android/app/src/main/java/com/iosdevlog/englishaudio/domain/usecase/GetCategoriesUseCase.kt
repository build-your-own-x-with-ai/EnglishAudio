package com.iosdevlog.englishaudio.domain.usecase

import com.iosdevlog.englishaudio.data.model.Category
import com.iosdevlog.englishaudio.data.model.Grade
import com.iosdevlog.englishaudio.data.repository.AudioRepository

/**
 * Use case to retrieve categories for a specific grade
 * Requirements: 2.1
 */
class GetCategoriesUseCase(
    private val repository: AudioRepository
) {
    /**
     * Get all available categories for a specific grade
     * @param grade The grade to get categories for
     * @return List of Category enums (课本, 单词) that have audio files for this grade
     */
    suspend operator fun invoke(grade: Grade): List<Category> {
        return repository.getCategories(grade)
    }
}
