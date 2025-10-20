package com.iosdevlog.englishaudio.util

import com.iosdevlog.englishaudio.data.model.AudioFile
import com.iosdevlog.englishaudio.data.model.Category
import com.iosdevlog.englishaudio.data.model.Grade

/**
 * Extension functions for common operations
 * Requirements: 5.4, 6.2
 */

/**
 * Get the full display name for an audio file
 * Format: "{Grade} - {Category} - {Unit Name}"
 */
fun AudioFile.getFullDisplayName(): String {
    return "${grade.displayName} - ${category.displayName} - $unitName"
}

/**
 * Get the folder path for a grade and category combination
 */
fun Grade.getFolderPath(category: Category): String {
    return "${this.folderPrefix}${category.folderSuffix}"
}

/**
 * Check if a string contains a valid unit number pattern
 */
fun String.containsUnitPattern(): Boolean {
    val unitPattern = Regex("(?i)(unit|project)\\s*\\d+", RegexOption.IGNORE_CASE)
    return unitPattern.containsMatchIn(this)
}

/**
 * Extract unit number from a string
 * Returns null if no valid unit number is found
 */
fun String.extractUnitNumber(): Int? {
    val unitPattern = Regex("(?i)(unit|project)\\s*(\\d+)", RegexOption.IGNORE_CASE)
    val match = unitPattern.find(this) ?: return null
    return match.groupValues[2].toIntOrNull()
}

/**
 * Check if the audio file is a project file (vs a regular unit)
 */
fun AudioFile.isProject(): Boolean {
    return unitName.contains("Project", ignoreCase = true)
}

/**
 * Get a short display name for the audio file (just the unit name)
 */
fun AudioFile.getShortDisplayName(): String {
    return unitName
}

/**
 * Convert milliseconds to seconds
 */
fun Long.toSeconds(): Long {
    return this / 1000
}

/**
 * Convert seconds to milliseconds
 */
fun Long.toMilliseconds(): Long {
    return this * 1000
}

/**
 * Check if a duration is valid (greater than 0)
 */
fun Long.isValidDuration(): Boolean {
    return this > 0
}
