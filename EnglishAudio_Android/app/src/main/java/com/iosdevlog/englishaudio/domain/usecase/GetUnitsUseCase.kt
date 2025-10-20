package com.iosdevlog.englishaudio.domain.usecase

import com.iosdevlog.englishaudio.data.model.AudioFile
import com.iosdevlog.englishaudio.data.model.Category
import com.iosdevlog.englishaudio.data.model.Grade
import com.iosdevlog.englishaudio.data.repository.AudioRepository

/**
 * Use case to retrieve units for a specific grade and category
 * Requirements: 3.1
 */
class GetUnitsUseCase(
    private val repository: AudioRepository
) {
    /**
     * Get all available units (audio files) for a specific grade and category
     * @param grade The grade to get units for
     * @param category The category (课本 or 单词) to get units for
     * @return List of AudioFile objects sorted by unit number
     */
    suspend operator fun invoke(grade: Grade, category: Category): List<AudioFile> {
        return repository.getUnits(grade, category)
    }
}
