package com.iosdevlog.englishaudio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.iosdevlog.englishaudio.presentation.navigation.AppNavigation
import com.iosdevlog.englishaudio.ui.theme.EnglishAudioTheme

/**
 * Main activity for the English Audio Player app.
 * Provides a child-friendly interface for learning English through audio lessons.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EnglishAudioTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AppNavigation()
                }
            }
        }
    }
}