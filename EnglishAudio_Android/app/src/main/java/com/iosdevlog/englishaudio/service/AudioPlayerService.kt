package com.iosdevlog.englishaudio.service

import android.content.Context
import android.media.MediaPlayer
import com.iosdevlog.englishaudio.data.model.AudioFile
import com.iosdevlog.englishaudio.domain.model.PlaybackState
import com.iosdevlog.englishaudio.util.getFullDisplayName
import com.iosdevlog.englishaudio.util.logDebug
import com.iosdevlog.englishaudio.util.logError
import com.iosdevlog.englishaudio.util.logInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AudioPlayerService(private val context: Context) {
    
    private var mediaPlayer: MediaPlayer? = null
    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.Idle)
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()
    
    private var updateJob: Job? = null
    private var currentAudioFile: AudioFile? = null
    
    /**
     * Play an audio file from assets
     * Requirements: 4.1, 4.2, 4.3
     */
    fun play(audioFile: AudioFile) {
        try {
            logInfo("Starting playback: ${audioFile.getFullDisplayName()}")
            
            // Release previous player
            release()
            
            // Store current audio file
            currentAudioFile = audioFile
            
            // Get audio file from assets
            val afd = context.assets.openFd(audioFile.filePath)
            logDebug("Opened asset file: ${audioFile.filePath}")
            
            // Create new MediaPlayer
            mediaPlayer = MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                prepare()
                start()
                
                logDebug("MediaPlayer prepared and started")
                
                // Set completion listener
                setOnCompletionListener {
                    logInfo("Playback completed for: ${audioFile.unitName}")
                    _playbackState.value = PlaybackState.Idle
                    updateJob?.cancel()
                    currentAudioFile = null
                }
                
                // Set error listener
                setOnErrorListener { _, what, extra ->
                    logError("MediaPlayer error: what=$what, extra=$extra")
                    _playbackState.value = PlaybackState.Error(
                        "播放错误: what=$what, extra=$extra"
                    )
                    updateJob?.cancel()
                    true
                }
            }
            
            // Start updating playback state every 100ms
            startUpdatingPlaybackState(audioFile)
            
        } catch (e: Exception) {
            logError("Failed to play audio: ${audioFile.fileName}", e)
            _playbackState.value = PlaybackState.Error("播放失败: ${e.message}")
            currentAudioFile = null
        }
    }
    
    /**
     * Pause playback
     * Requirements: 5.1
     */
    fun pause() {
        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    logDebug("Pausing playback")
                    player.pause()
                    updatePlaybackState()
                }
            }
        } catch (e: Exception) {
            logError("Failed to pause playback", e)
            _playbackState.value = PlaybackState.Error("暂停失败: ${e.message}")
        }
    }
    
    /**
     * Resume playback
     * Requirements: 5.2
     */
    fun resume() {
        try {
            mediaPlayer?.let { player ->
                if (!player.isPlaying) {
                    logDebug("Resuming playback")
                    player.start()
                    currentAudioFile?.let { audioFile ->
                        startUpdatingPlaybackState(audioFile)
                    }
                }
            }
        } catch (e: Exception) {
            logError("Failed to resume playback", e)
            _playbackState.value = PlaybackState.Error("继续播放失败: ${e.message}")
        }
    }
    
    /**
     * Stop playback and reset
     * Requirements: 5.3
     */
    fun stop() {
        try {
            logInfo("Stopping playback")
            mediaPlayer?.stop()
            release()
            _playbackState.value = PlaybackState.Idle
            currentAudioFile = null
        } catch (e: Exception) {
            logError("Failed to stop playback", e)
            _playbackState.value = PlaybackState.Error("停止失败: ${e.message}")
        }
    }
    
    /**
     * Seek to a specific position
     * Requirements: 5.5
     */
    fun seekTo(position: Long) {
        try {
            logDebug("Seeking to position: $position ms")
            mediaPlayer?.seekTo(position.toInt())
            updatePlaybackState()
        } catch (e: Exception) {
            logError("Failed to seek", e)
            _playbackState.value = PlaybackState.Error("跳转失败: ${e.message}")
        }
    }
    
    /**
     * Start updating playback state every 100ms
     * Requirements: 5.1, 5.2, 5.3
     */
    private fun startUpdatingPlaybackState(audioFile: AudioFile) {
        updateJob?.cancel()
        updateJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                updatePlaybackState()
                delay(100) // Update every 100ms
            }
        }
    }
    
    /**
     * Update current playback state
     */
    private fun updatePlaybackState() {
        val player = mediaPlayer ?: return
        val audioFile = currentAudioFile ?: return
        
        try {
            _playbackState.value = if (player.isPlaying) {
                PlaybackState.Playing(
                    audioFile = audioFile,
                    currentPosition = player.currentPosition.toLong(),
                    duration = player.duration.toLong()
                )
            } else {
                PlaybackState.Paused(
                    audioFile = audioFile,
                    currentPosition = player.currentPosition.toLong(),
                    duration = player.duration.toLong()
                )
            }
        } catch (e: Exception) {
            _playbackState.value = PlaybackState.Error("状态更新失败: ${e.message}")
        }
    }
    
    /**
     * Release MediaPlayer resources
     * Requirements: 4.4
     */
    fun release() {
        logDebug("Releasing MediaPlayer resources")
        updateJob?.cancel()
        updateJob = null
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
