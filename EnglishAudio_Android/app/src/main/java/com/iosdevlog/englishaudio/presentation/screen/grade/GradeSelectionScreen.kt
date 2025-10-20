package com.iosdevlog.englishaudio.presentation.screen.grade

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iosdevlog.englishaudio.data.model.Grade
import com.iosdevlog.englishaudio.ui.theme.EnglishAudioTheme

/**
 * Grade Selection Screen
 * Requirements: 1.1, 1.2, 1.3, 1.4, 8.1, 8.2
 */
@Composable
fun GradeSelectionScreen(
    viewModel: GradeViewModel,
    onGradeSelected: (Grade) -> Unit,
    modifier: Modifier = Modifier
) {
    val grades by viewModel.grades.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "选择年级",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(grades) { grade ->
                GradeCard(
                    grade = grade,
                    onClick = { onGradeSelected(grade) }
                )
            }
        }
    }
}

/**
 * Grade Card Component
 * Requirements: 1.3, 1.4, 8.1, 8.2
 */
@Composable
fun GradeCard(
    grade: Grade,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(
                onClick = onClick,
                onClickLabel = "选择${grade.displayName}"
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = grade.displayName,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GradeCardPreview() {
    EnglishAudioTheme {
        GradeCard(
            grade = Grade.GRADE_1,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GradeSelectionScreenPreview() {
    EnglishAudioTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "选择年级",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(Grade.values().toList()) { grade ->
                    GradeCard(
                        grade = grade,
                        onClick = {}
                    )
                }
            }
        }
    }
}
