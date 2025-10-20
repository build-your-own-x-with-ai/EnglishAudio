package com.iosdevlog.englishaudio.domain.model

import com.iosdevlog.englishaudio.data.model.AudioFile

sealed class PlaybackState {
    object Idle : PlaybackState()
    
    data class Playing(
        val audioFile: AudioFile,
        val currentPosition: Long,
        val duration: Long
    ) : PlaybackState()
    
    data class Paused(
        val audioFile: AudioFile,
        val currentPosition: Long,
        val duration: Long
    ) : PlaybackState()
    
    data class Error(val message: String) : PlaybackState()
}
