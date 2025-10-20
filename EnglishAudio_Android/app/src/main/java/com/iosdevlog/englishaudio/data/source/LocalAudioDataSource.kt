package com.iosdevlog.englishaudio.data.source

import android.content.Context
import android.content.res.AssetFileDescriptor
import com.iosdevlog.englishaudio.data.model.AudioFile
import com.iosdevlog.englishaudio.data.model.Category
import com.iosdevlog.englishaudio.data.model.Grade
import com.iosdevlog.englishaudio.util.getFolderPath
import com.iosdevlog.englishaudio.util.logDebug
import com.iosdevlog.englishaudio.util.logWarning

class LocalAudioDataSource(private val context: Context) {
    
    private val assetManager = context.assets
    
    /**
     * Load all audio files from the assets directory
     * Scans through all grade and category combinations
     */
    fun loadAudioFiles(): List<AudioFile> {
        val audioFiles = mutableListOf<AudioFile>()
        
        logDebug("Starting to load audio files from assets")
        
        // Iterate through all grades
        Grade.values().forEach { grade ->
            // Iterate through all categories
            Category.values().forEach { category ->
                val folderPath = grade.getFolderPath(category)
                
                try {
                    // List all files in this directory
                    val files = assetManager.list(folderPath) ?: emptyArray()
                    logDebug("Found ${files.size} files in $folderPath")
                    
                    files.filter { it.endsWith(".mp3") }.forEach { fileName ->
                        // Parse file name to extract unit information
                        parseUnitFromFileName(fileName)?.let { (unitNumber, unitName) ->
                            audioFiles.add(
                                AudioFile(
                                    id = "${grade.name}_${category.name}_$unitNumber",
                                    grade = grade,
                                    category = category,
                                    unitNumber = unitNumber,
                                    unitName = unitName,
                                    fileName = fileName,
                                    filePath = "$folderPath/$fileName"
                                )
                            )
                            logDebug("Loaded audio file: $unitName from $fileName")
                        }
                    }
                } catch (e: Exception) {
                    // Directory doesn't exist or cannot be accessed, skip
                    logWarning("Failed to load from $folderPath: ${e.message}", e)
                }
            }
        }
        
        logDebug("Loaded ${audioFiles.size} audio files in total")
        
        return audioFiles.sortedWith(
            compareBy({ it.grade.ordinal }, { it.category.ordinal }, { it.unitNumber })
        )
    }
    
    /**
     * Parse file name to extract unit information
     * Matches patterns like "unit 1", "Unit 10", "Project 2", etc.
     * 
     * @param fileName The name of the audio file
     * @return Pair of (unitNumber, unitName) or null if pattern doesn't match
     */
    fun parseUnitFromFileName(fileName: String): Pair<Int, String>? {
        // Match "unit 1", "Unit 10", "Project 2", etc.
        val unitPattern = Regex("(?i)(unit|project)\\s*(\\d+)", RegexOption.IGNORE_CASE)
        val match = unitPattern.find(fileName) ?: return null
        
        val unitType = match.groupValues[1].replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase() else it.toString() 
        }
        val unitNumber = match.groupValues[2].toIntOrNull() ?: return null
        val unitName = "$unitType $unitNumber"
        
        return Pair(unitNumber, unitName)
    }
    
    /**
     * Get AssetFileDescriptor for an audio file
     * Used by MediaPlayer to play audio from assets
     * 
     * @param filePath The path to the audio file in assets
     * @return AssetFileDescriptor for the audio file
     */
    fun getAudioFileDescriptor(filePath: String): AssetFileDescriptor {
        return assetManager.openFd(filePath)
    }
}
