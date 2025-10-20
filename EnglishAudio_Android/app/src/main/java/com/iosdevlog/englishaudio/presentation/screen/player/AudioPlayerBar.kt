package com.iosdevlog.englishaudio.presentation.screen.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.iosdevlog.englishaudio.domain.model.PlaybackState
import com.iosdevlog.englishaudio.util.formatTime

/**
 * Audio player bar composable that displays at the bottom of screens
 * Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 6.1, 6.2, 6.3, 8.1, 8.2
 */
@Composable
fun AudioPlayerBar(
    viewModel: AudioPlayerViewModel,
    modifier: Modifier = Modifier
) {
    val playbackState by viewModel.playbackState.collectAsState()
    
    // Only show the bar when playing or paused
    when (val state = playbackState) {
        is PlaybackState.Playing, is PlaybackState.Paused -> {
            Surface(
                modifier = modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Audio file information
                    // Requirements: 6.1, 6.2
                    Text(
                        text = when (state) {
                            is PlaybackState.Playing -> state.audioFile.fileName
                            is PlaybackState.Paused -> state.audioFile.fileName
                            else -> ""
                        },
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Playback progress slider
                    // Requirements: 5.5
                    val currentPosition = when (state) {
                        is PlaybackState.Playing -> state.currentPosition
                        is PlaybackState.Paused -> state.currentPosition
                        else -> 0L
                    }
                    
                    val duration = when (state) {
                        is PlaybackState.Playing -> state.duration
                        is PlaybackState.Paused -> state.duration
                        else -> 100L
                    }
                    
                    Slider(
                        value = currentPosition.toFloat(),
                        onValueChange = { viewModel.seekTo(it.toLong()) },
                        valueRange = 0f..duration.toFloat(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Time display (current position / total duration)
                    // Requirements: 5.4, 6.2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTime(currentPosition),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = formatTime(duration),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Playback control buttons
                    // Requirements: 5.1, 5.2, 5.3, 6.3, 8.1, 8.2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Stop button
                        // Requirements: 5.3, 8.2
                        Button(
                            onClick = { viewModel.stop() },
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text("停止")
                        }
                        
                        Spacer(modifier = Modifier.width(24.dp))
                        
                        // Play/Pause button (large touch target)
                        // Requirements: 5.1, 5.2, 8.2
                        Button(
                            onClick = { viewModel.togglePlayPause() },
                            modifier = Modifier.height(64.dp)
                        ) {
                            Text(
                                text = if (state is PlaybackState.Playing) "暂停" else "播放",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }
            }
        }
        else -> {
            // Don't show the bar for Idle or Error states
        }
    }
}
