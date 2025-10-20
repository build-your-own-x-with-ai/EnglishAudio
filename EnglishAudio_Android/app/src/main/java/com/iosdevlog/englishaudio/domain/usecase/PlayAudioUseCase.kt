package com.iosdevlog.englishaudio.domain.usecase

import com.iosdevlog.englishaudio.data.model.AudioFile
import com.iosdevlog.englishaudio.service.AudioPlayerService

/**
 * Use case to handle audio playback
 * Requirements: 4.1
 */
class PlayAudioUseCase(
    private val audioPlayerService: AudioPlayerService
) {
    /**
     * Play an audio file
     * @param audioFile The audio file to play
     */
    operator fun invoke(audioFile: AudioFile) {
        audioPlayerService.play(audioFile)
    }
}
