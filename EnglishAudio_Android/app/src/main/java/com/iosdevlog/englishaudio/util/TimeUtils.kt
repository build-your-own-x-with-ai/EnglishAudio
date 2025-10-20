package com.iosdevlog.englishaudio.util

/**
 * Utility functions for time formatting
 * Requirements: 5.4, 6.2
 */

/**
 * Format time in milliseconds to MM:SS format
 * 
 * @param milliseconds Time in milliseconds
 * @return Formatted time string in MM:SS format
 * 
 * Example:
 * - formatTime(0) -> "00:00"
 * - formatTime(65000) -> "01:05"
 * - formatTime(3661000) -> "61:01"
 */
fun formatTime(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

/**
 * Convert MM:SS format string to milliseconds
 * 
 * @param timeString Time string in MM:SS format
 * @return Time in milliseconds, or 0 if parsing fails
 */
fun parseTime(timeString: String): Long {
    return try {
        val parts = timeString.split(":")
        if (parts.size != 2) return 0L
        
        val minutes = parts[0].toLongOrNull() ?: 0L
        val seconds = parts[1].toLongOrNull() ?: 0L
        
        (minutes * 60 + seconds) * 1000
    } catch (e: Exception) {
        0L
    }
}
