package com.iosdevlog.englishaudio.domain.usecase

import com.iosdevlog.englishaudio.data.model.Grade
import com.iosdevlog.englishaudio.data.repository.AudioRepository

/**
 * Use case to retrieve available grades
 * Requirements: 1.1
 */
class GetGradesUseCase(
    private val repository: AudioRepository
) {
    /**
     * Get all available grades that have audio files
     * @return List of Grade enums sorted by grade level
     */
    suspend operator fun invoke(): List<Grade> {
        return repository.getGrades()
    }
}
