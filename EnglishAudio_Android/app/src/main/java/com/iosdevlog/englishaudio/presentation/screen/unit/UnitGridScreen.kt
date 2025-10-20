package com.iosdevlog.englishaudio.presentation.screen.unit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iosdevlog.englishaudio.data.model.AudioFile
import com.iosdevlog.englishaudio.data.model.Category
import com.iosdevlog.englishaudio.data.model.Grade
import com.iosdevlog.englishaudio.ui.theme.EnglishAudioTheme

/**
 * Unit Grid Screen
 * Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 4.1
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitGridScreen(
    grade: Grade,
    category: Category,
    viewModel: UnitViewModel,
    onUnitSelected: (AudioFile) -> Unit,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val units by viewModel.units.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Load units when screen is displayed
    LaunchedEffect(grade, category) {
        viewModel.loadUnits(grade, category)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${grade.displayName} - ${category.displayName}") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    // Show loading indicator
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                errorMessage != null -> {
                    // Show error message
                    Text(
                        text = errorMessage ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                units.isEmpty() -> {
                    // Show empty state
                    Text(
                        text = "没有找到音频文件",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                else -> {
                    // Show unit grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(units) { audioFile ->
                            UnitCard(
                                audioFile = audioFile,
                                onClick = { onUnitSelected(audioFile) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Unit Card Component
 * Requirements: 3.4, 8.1, 8.2
 */
@Composable
fun UnitCard(
    audioFile: AudioFile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(
                onClick = onClick,
                onClickLabel = "播放${audioFile.unitName}"
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = audioFile.unitName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UnitCardPreview() {
    EnglishAudioTheme {
        UnitCard(
            audioFile = AudioFile(
                id = "1",
                grade = Grade.GRADE_1,
                category = Category.TEXTBOOK,
                unitNumber = 1,
                unitName = "Unit 1",
                fileName = "unit1.mp3",
                filePath = "path/to/unit1.mp3"
            ),
            onClick = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun UnitGridScreenPreview() {
    EnglishAudioTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("一年级 - 课本") },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "返回"
                            )
                        }
                    }
                )
            }
        ) { padding ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(12) { index ->
                    UnitCard(
                        audioFile = AudioFile(
                            id = "$index",
                            grade = Grade.GRADE_1,
                            category = Category.TEXTBOOK,
                            unitNumber = index + 1,
                            unitName = "Unit ${index + 1}",
                            fileName = "unit${index + 1}.mp3",
                            filePath = "path/to/unit${index + 1}.mp3"
                        ),
                        onClick = {}
                    )
                }
            }
        }
    }
}
