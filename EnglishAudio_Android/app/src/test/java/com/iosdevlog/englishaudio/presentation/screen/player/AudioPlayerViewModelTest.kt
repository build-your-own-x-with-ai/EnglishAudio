package com.iosdevlog.englishaudio.presentation.screen.player

import com.iosdevlog.englishaudio.data.model.AudioFile
import com.iosdevlog.englishaudio.data.model.Category
import com.iosdevlog.englishaudio.data.model.Grade
import com.iosdevlog.englishaudio.domain.model.PlaybackState
import com.iosdevlog.englishaudio.service.AudioPlayerService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for AudioPlayerViewModel
 * Requirements: 5.1, 5.2, 5.3
 */
class AudioPlayerViewModelTest {

    private lateinit var audioPlayerService: AudioPlayerService
    private lateinit var viewModel: AudioPlayerViewModel
    private lateinit var playbackStateFlow: MutableStateFlow<PlaybackState>

    private val sampleAudioFile = AudioFile(
        id = "GRADE_1_TEXTBOOK_1",
        grade = Grade.GRADE_1,
        category = Category.TEXTBOOK,
        unitNumber = 1,
        unitName = "Unit 1",
        fileName = "unit 1.mp3",
        filePath = "path/unit 1.mp3"
    )

    @Before
    fun setup() {
        audioPlayerService = mockk(relaxed = true)
        playbackStateFlow = MutableStateFlow(PlaybackState.Idle)
        every { audioPlayerService.playbackState } returns playbackStateFlow
        viewModel = AudioPlayerViewModel(audioPlayerService)
    }

    /**
     * Test AudioPlayerViewModel state transitions
     * Requirements: 5.1, 5.2, 5.3
     */
    @Test
    fun playbackState_exposesServiceState() {
        // Initial state should be Idle
        assertEquals(PlaybackState.Idle, viewModel.playbackState.value)
        
        // Update service state
        playbackStateFlow.value = PlaybackState.Playing(
            audioFile = sampleAudioFile,
            currentPosition = 1000L,
            duration = 10000L
        )
        
        // ViewModel should expose the same state
        val state = viewModel.playbackState.value
        assertTrue(state is PlaybackState.Playing)
        assertEquals(1000L, (state as PlaybackState.Playing).currentPosition)
    }

    @Test
    fun play_callsServicePlay() {
        viewModel.play(sampleAudioFile)
        
        verify { audioPlayerService.play(sampleAudioFile) }
    }

    @Test
    fun togglePlayPause_whenPlaying_callsPause() {
        // Set state to Playing
        playbackStateFlow.value = PlaybackState.Playing(
            audioFile = sampleAudioFile,
            currentPosition = 1000L,
            duration = 10000L
        )
        
        viewModel.togglePlayPause()
        
        verify { audioPlayerService.pause() }
    }

    @Test
    fun togglePlayPause_whenPaused_callsResume() {
        // Set state to Paused
        playbackStateFlow.value = PlaybackState.Paused(
            audioFile = sampleAudioFile,
            currentPosition = 1000L,
            duration = 10000L
        )
        
        viewModel.togglePlayPause()
        
        verify { audioPlayerService.resume() }
    }

    @Test
    fun togglePlayPause_whenIdle_doesNothing() {
        // Set state to Idle
        playbackStateFlow.value = PlaybackState.Idle
        
        viewModel.togglePlayPause()
        
        // Should not call pause or resume
        verify(exactly = 0) { audioPlayerService.pause() }
        verify(exactly = 0) { audioPlayerService.resume() }
    }

    @Test
    fun togglePlayPause_whenError_doesNothing() {
        // Set state to Error
        playbackStateFlow.value = PlaybackState.Error("Test error")
        
        viewModel.togglePlayPause()
        
        // Should not call pause or resume
        verify(exactly = 0) { audioPlayerService.pause() }
        verify(exactly = 0) { audioPlayerService.resume() }
    }

    @Test
    fun stop_callsServiceStop() {
        viewModel.stop()
        
        verify { audioPlayerService.stop() }
    }

    @Test
    fun seekTo_callsServiceSeekTo() {
        val position = 5000L
        
        viewModel.seekTo(position)
        
        verify { audioPlayerService.seekTo(position) }
    }

    // Note: onCleared() is protected and called by framework, so we cannot test it directly
    // The release behavior is tested through integration tests

    @Test
    fun playbackState_transitionsFromIdleToPlaying() {
        // Start with Idle
        assertEquals(PlaybackState.Idle, viewModel.playbackState.value)
        
        // Transition to Playing
        playbackStateFlow.value = PlaybackState.Playing(
            audioFile = sampleAudioFile,
            currentPosition = 0L,
            duration = 10000L
        )
        
        val state = viewModel.playbackState.value
        assertTrue(state is PlaybackState.Playing)
    }

    @Test
    fun playbackState_transitionsFromPlayingToPaused() {
        // Start with Playing
        playbackStateFlow.value = PlaybackState.Playing(
            audioFile = sampleAudioFile,
            currentPosition = 1000L,
            duration = 10000L
        )
        
        // Transition to Paused
        playbackStateFlow.value = PlaybackState.Paused(
            audioFile = sampleAudioFile,
            currentPosition = 1000L,
            duration = 10000L
        )
        
        val state = viewModel.playbackState.value
        assertTrue(state is PlaybackState.Paused)
    }

    @Test
    fun playbackState_transitionsToPausedToPlaying() {
        // Start with Paused
        playbackStateFlow.value = PlaybackState.Paused(
            audioFile = sampleAudioFile,
            currentPosition = 1000L,
            duration = 10000L
        )
        
        // Transition to Playing
        playbackStateFlow.value = PlaybackState.Playing(
            audioFile = sampleAudioFile,
            currentPosition = 1000L,
            duration = 10000L
        )
        
        val state = viewModel.playbackState.value
        assertTrue(state is PlaybackState.Playing)
    }

    @Test
    fun playbackState_transitionsToError() {
        // Start with Playing
        playbackStateFlow.value = PlaybackState.Playing(
            audioFile = sampleAudioFile,
            currentPosition = 1000L,
            duration = 10000L
        )
        
        // Transition to Error
        playbackStateFlow.value = PlaybackState.Error("Playback error")
        
        val state = viewModel.playbackState.value
        assertTrue(state is PlaybackState.Error)
        assertEquals("Playback error", (state as PlaybackState.Error).message)
    }

    @Test
    fun playbackState_transitionsBackToIdle() {
        // Start with Playing
        playbackStateFlow.value = PlaybackState.Playing(
            audioFile = sampleAudioFile,
            currentPosition = 1000L,
            duration = 10000L
        )
        
        // Transition back to Idle (e.g., after completion)
        playbackStateFlow.value = PlaybackState.Idle
        
        assertEquals(PlaybackState.Idle, viewModel.playbackState.value)
    }
}
