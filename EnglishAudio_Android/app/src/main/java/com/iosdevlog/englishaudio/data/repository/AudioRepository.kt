package com.iosdevlog.englishaudio.data.repository

import com.iosdevlog.englishaudio.data.model.AudioFile
import com.iosdevlog.englishaudio.data.model.Category
import com.iosdevlog.englishaudio.data.model.Grade
import com.iosdevlog.englishaudio.data.source.LocalAudioDataSource

/**
 * Repository interface for audio file operations
 */
interface AudioRepository {
    /**
     * Scan and load all audio files from the data source
     */
    suspend fun scanAudioFiles(): Result<Unit>
    
    /**
     * Get all available grades
     */
    suspend fun getGrades(): List<Grade>
    
    /**
     * Get all categories for a specific grade
     */
    suspend fun getCategories(grade: Grade): List<Category>
    
    /**
     * Get all units (audio files) for a specific grade and category
     */
    suspend fun getUnits(grade: Grade, category: Category): List<AudioFile>
    
    /**
     * Get a specific audio file by grade, category, and unit number
     */
    suspend fun getAudioFile(grade: Grade, category: Category, unitNumber: Int): AudioFile?
}

/**
 * Implementation of AudioRepository with caching strategy
 */
class AudioRepositoryImpl(
    private val localDataSource: LocalAudioDataSource
) : AudioRepository {
    
    // Cache for loaded audio files
    private var audioFilesCache: List<AudioFile>? = null
    
    override suspend fun scanAudioFiles(): Result<Unit> {
        return try {
            audioFilesCache = localDataSource.loadAudioFiles()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getGrades(): List<Grade> {
        // Ensure audio files are loaded
        if (audioFilesCache == null) {
            scanAudioFiles()
        }
        
        // Return grades that have at least one audio file
        return audioFilesCache
            ?.map { it.grade }
            ?.distinct()
            ?.sortedBy { it.ordinal }
            ?: Grade.values().toList()
    }
    
    override suspend fun getCategories(grade: Grade): List<Category> {
        // Ensure audio files are loaded
        if (audioFilesCache == null) {
            scanAudioFiles()
        }
        
        // Return categories that have at least one audio file for this grade
        return audioFilesCache
            ?.filter { it.grade == grade }
            ?.map { it.category }
            ?.distinct()
            ?.sortedBy { it.ordinal }
            ?: emptyList()
    }
    
    override suspend fun getUnits(grade: Grade, category: Category): List<AudioFile> {
        // Ensure audio files are loaded
        if (audioFilesCache == null) {
            scanAudioFiles()
        }
        
        // Filter and return audio files for the specified grade and category
        return audioFilesCache
            ?.filter { it.grade == grade && it.category == category }
            ?.sortedBy { it.unitNumber }
            ?: emptyList()
    }
    
    override suspend fun getAudioFile(
        grade: Grade,
        category: Category,
        unitNumber: Int
    ): AudioFile? {
        // Ensure audio files are loaded
        if (audioFilesCache == null) {
            scanAudioFiles()
        }
        
        // Find and return the specific audio file
        return audioFilesCache?.find {
            it.grade == grade && it.category == category && it.unitNumber == unitNumber
        }
    }
}
