package com.iosdevlog.englishaudio.presentation.screen.player

import androidx.lifecycle.ViewModel
import com.iosdevlog.englishaudio.data.model.AudioFile
import com.iosdevlog.englishaudio.domain.model.PlaybackState
import com.iosdevlog.englishaudio.service.AudioPlayerService
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel for audio player UI
 * Requirements: 5.1, 5.2, 5.3, 5.4, 5.5
 */
class AudioPlayerViewModel(
    private val audioPlayerService: AudioPlayerService
) : ViewModel() {
    
    /**
     * Expose playback state as StateFlow
     * Requirements: 5.1, 5.2, 5.3, 5.4, 5.5
     */
    val playbackState: StateFlow<PlaybackState> = audioPlayerService.playbackState
    
    /**
     * Play an audio file
     * Requirements: 4.1
     */
    fun play(audioFile: AudioFile) {
        audioPlayerService.play(audioFile)
    }
    
    /**
     * Toggle between play and pause
     * Requirements: 5.1, 5.2
     */
    fun togglePlayPause() {
        when (playbackState.value) {
            is PlaybackState.Playing -> audioPlayerService.pause()
            is PlaybackState.Paused -> audioPlayerService.resume()
            else -> { /* Do nothing for Idle or Error states */ }
        }
    }
    
    /**
     * Stop playback
     * Requirements: 5.3
     */
    fun stop() {
        audioPlayerService.stop()
    }
    
    /**
     * Seek to a specific position
     * Requirements: 5.5
     */
    fun seekTo(position: Long) {
        audioPlayerService.seekTo(position)
    }
    
    /**
     * Release resources when ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        audioPlayerService.release()
    }
}
