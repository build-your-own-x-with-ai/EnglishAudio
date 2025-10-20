package com.iosdevlog.englishaudio.util

import android.util.Log

/**
 * Logging utilities for debugging
 * Requirements: 5.4, 6.2
 */

/**
 * Application-wide logger with consistent tag prefix
 */
object AppLogger {
    private const val TAG_PREFIX = "EnglishAudio"
    
    /**
     * Log debug message
     */
    fun d(tag: String, message: String) {
        Log.d("$TAG_PREFIX:$tag", message)
    }
    
    /**
     * Log info message
     */
    fun i(tag: String, message: String) {
        Log.i("$TAG_PREFIX:$tag", message)
    }
    
    /**
     * Log warning message
     */
    fun w(tag: String, message: String) {
        Log.w("$TAG_PREFIX:$tag", message)
    }
    
    /**
     * Log warning message with throwable
     */
    fun w(tag: String, message: String, throwable: Throwable) {
        Log.w("$TAG_PREFIX:$tag", message, throwable)
    }
    
    /**
     * Log error message
     */
    fun e(tag: String, message: String) {
        Log.e("$TAG_PREFIX:$tag", message)
    }
    
    /**
     * Log error message with throwable
     */
    fun e(tag: String, message: String, throwable: Throwable) {
        Log.e("$TAG_PREFIX:$tag", message, throwable)
    }
    
    /**
     * Log verbose message
     */
    fun v(tag: String, message: String) {
        Log.v("$TAG_PREFIX:$tag", message)
    }
}

/**
 * Extension function for easy logging from any class
 */
inline fun <reified T> T.logDebug(message: String) {
    AppLogger.d(T::class.java.simpleName, message)
}

/**
 * Extension function for logging info messages
 */
inline fun <reified T> T.logInfo(message: String) {
    AppLogger.i(T::class.java.simpleName, message)
}

/**
 * Extension function for logging warnings
 */
inline fun <reified T> T.logWarning(message: String, throwable: Throwable? = null) {
    if (throwable != null) {
        AppLogger.w(T::class.java.simpleName, message, throwable)
    } else {
        AppLogger.w(T::class.java.simpleName, message)
    }
}

/**
 * Extension function for logging errors
 */
inline fun <reified T> T.logError(message: String, throwable: Throwable? = null) {
    if (throwable != null) {
        AppLogger.e(T::class.java.simpleName, message, throwable)
    } else {
        AppLogger.e(T::class.java.simpleName, message)
    }
}

/**
 * Extension function for logging verbose messages
 */
inline fun <reified T> T.logVerbose(message: String) {
    AppLogger.v(T::class.java.simpleName, message)
}
