package com.iosdevlog.englishaudio.data.repository

import com.iosdevlog.englishaudio.data.model.AudioFile
import com.iosdevlog.englishaudio.data.model.Category
import com.iosdevlog.englishaudio.data.model.Grade
import com.iosdevlog.englishaudio.data.source.LocalAudioDataSource
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for AudioRepository
 * Requirements: 7.2, 7.3
 */
class AudioRepositoryTest {

    private lateinit var localDataSource: LocalAudioDataSource
    private lateinit var repository: AudioRepository

    private val sampleAudioFiles = listOf(
        AudioFile(
            id = "GRADE_1_TEXTBOOK_1",
            grade = Grade.GRADE_1,
            category = Category.TEXTBOOK,
            unitNumber = 1,
            unitName = "Unit 1",
            fileName = "一上英课本unit 1.mp3",
            filePath = "一年级上册课本/一上英课本unit 1.mp3"
        ),
        AudioFile(
            id = "GRADE_1_TEXTBOOK_2",
            grade = Grade.GRADE_1,
            category = Category.TEXTBOOK,
            unitNumber = 2,
            unitName = "Unit 2",
            fileName = "一上英课本unit 2.mp3",
            filePath = "一年级上册课本/一上英课本unit 2.mp3"
        ),
        AudioFile(
            id = "GRADE_1_VOCABULARY_1",
            grade = Grade.GRADE_1,
            category = Category.VOCABULARY,
            unitNumber = 1,
            unitName = "Unit 1",
            fileName = "【单词】一年级上册Unit 1.mp3",
            filePath = "一年级上册单词/【单词】一年级上册Unit 1.mp3"
        ),
        AudioFile(
            id = "GRADE_2_TEXTBOOK_1",
            grade = Grade.GRADE_2,
            category = Category.TEXTBOOK,
            unitNumber = 1,
            unitName = "Unit 1",
            fileName = "二上英课本unit 1.mp3",
            filePath = "二年级上册课本/二上英课本unit 1.mp3"
        ),
        AudioFile(
            id = "GRADE_3_VOCABULARY_5",
            grade = Grade.GRADE_3,
            category = Category.VOCABULARY,
            unitNumber = 5,
            unitName = "Unit 5",
            fileName = "【单词】三年级上册Unit 5.mp3",
            filePath = "三年级上册单词/【单词】三年级上册Unit 5.mp3"
        )
    )

    @Before
    fun setup() {
        localDataSource = mockk(relaxed = true)
        repository = AudioRepositoryImpl(localDataSource)
    }

    /**
     * Test getGrades returns all grades
     * Requirements: 7.2
     */
    @Test
    fun getGrades_withAudioFiles_returnsAllGrades() = runTest {
        // Mock data source to return sample files
        every { localDataSource.loadAudioFiles() } returns sampleAudioFiles
        
        val grades = repository.getGrades()
        
        // Should return grades 1, 2, and 3 (from sample data)
        assertEquals(3, grades.size)
        assertTrue(grades.contains(Grade.GRADE_1))
        assertTrue(grades.contains(Grade.GRADE_2))
        assertTrue(grades.contains(Grade.GRADE_3))
    }

    @Test
    fun getGrades_withNoAudioFiles_returnsEmptyList() = runTest {
        // Mock data source to return empty list
        every { localDataSource.loadAudioFiles() } returns emptyList()
        
        val grades = repository.getGrades()
        
        // When audioFilesCache is empty list, the chain returns empty list
        assertTrue(grades.isEmpty())
    }

    @Test
    fun getGrades_returnsSortedByOrdinal() = runTest {
        // Mock data source with grades in random order
        val randomOrderFiles = listOf(
            sampleAudioFiles[3], // Grade 2
            sampleAudioFiles[4], // Grade 3
            sampleAudioFiles[0]  // Grade 1
        )
        every { localDataSource.loadAudioFiles() } returns randomOrderFiles
        
        val grades = repository.getGrades()
        
        // Should be sorted by ordinal
        assertEquals(Grade.GRADE_1, grades[0])
        assertEquals(Grade.GRADE_2, grades[1])
        assertEquals(Grade.GRADE_3, grades[2])
    }

    /**
     * Test getUnits filters by grade and category correctly
     * Requirements: 7.3
     */
    @Test
    fun getUnits_withGradeAndCategory_returnsFilteredUnits() = runTest {
        // Mock data source
        every { localDataSource.loadAudioFiles() } returns sampleAudioFiles
        
        val units = repository.getUnits(Grade.GRADE_1, Category.TEXTBOOK)
        
        // Should return only Grade 1 Textbook units
        assertEquals(2, units.size)
        assertTrue(units.all { it.grade == Grade.GRADE_1 && it.category == Category.TEXTBOOK })
        assertEquals("Unit 1", units[0].unitName)
        assertEquals("Unit 2", units[1].unitName)
    }

    @Test
    fun getUnits_withDifferentCategory_returnsCorrectUnits() = runTest {
        // Mock data source
        every { localDataSource.loadAudioFiles() } returns sampleAudioFiles
        
        val units = repository.getUnits(Grade.GRADE_1, Category.VOCABULARY)
        
        // Should return only Grade 1 Vocabulary units
        assertEquals(1, units.size)
        assertEquals(Grade.GRADE_1, units[0].grade)
        assertEquals(Category.VOCABULARY, units[0].category)
        assertEquals("Unit 1", units[0].unitName)
    }

    @Test
    fun getUnits_withNoMatchingUnits_returnsEmptyList() = runTest {
        // Mock data source
        every { localDataSource.loadAudioFiles() } returns sampleAudioFiles
        
        val units = repository.getUnits(Grade.GRADE_4, Category.TEXTBOOK)
        
        // Should return empty list when no matching units
        assertTrue(units.isEmpty())
    }

    @Test
    fun getUnits_returnsSortedByUnitNumber() = runTest {
        // Create files with units in random order
        val randomOrderFiles = listOf(
            AudioFile(
                id = "GRADE_1_TEXTBOOK_5",
                grade = Grade.GRADE_1,
                category = Category.TEXTBOOK,
                unitNumber = 5,
                unitName = "Unit 5",
                fileName = "unit 5.mp3",
                filePath = "path/unit 5.mp3"
            ),
            AudioFile(
                id = "GRADE_1_TEXTBOOK_2",
                grade = Grade.GRADE_1,
                category = Category.TEXTBOOK,
                unitNumber = 2,
                unitName = "Unit 2",
                fileName = "unit 2.mp3",
                filePath = "path/unit 2.mp3"
            ),
            AudioFile(
                id = "GRADE_1_TEXTBOOK_10",
                grade = Grade.GRADE_1,
                category = Category.TEXTBOOK,
                unitNumber = 10,
                unitName = "Unit 10",
                fileName = "unit 10.mp3",
                filePath = "path/unit 10.mp3"
            )
        )
        every { localDataSource.loadAudioFiles() } returns randomOrderFiles
        
        val units = repository.getUnits(Grade.GRADE_1, Category.TEXTBOOK)
        
        // Should be sorted by unit number
        assertEquals(3, units.size)
        assertEquals(2, units[0].unitNumber)
        assertEquals(5, units[1].unitNumber)
        assertEquals(10, units[2].unitNumber)
    }

    @Test
    fun getCategories_withGrade_returnsAvailableCategories() = runTest {
        // Mock data source
        every { localDataSource.loadAudioFiles() } returns sampleAudioFiles
        
        val categories = repository.getCategories(Grade.GRADE_1)
        
        // Grade 1 has both textbook and vocabulary
        assertEquals(2, categories.size)
        assertTrue(categories.contains(Category.TEXTBOOK))
        assertTrue(categories.contains(Category.VOCABULARY))
    }

    @Test
    fun getCategories_withGradeHavingOneCategory_returnsSingleCategory() = runTest {
        // Mock data source
        every { localDataSource.loadAudioFiles() } returns sampleAudioFiles
        
        val categories = repository.getCategories(Grade.GRADE_3)
        
        // Grade 3 only has vocabulary in sample data
        assertEquals(1, categories.size)
        assertEquals(Category.VOCABULARY, categories[0])
    }

    @Test
    fun getAudioFile_withValidParameters_returnsAudioFile() = runTest {
        // Mock data source
        every { localDataSource.loadAudioFiles() } returns sampleAudioFiles
        
        val audioFile = repository.getAudioFile(Grade.GRADE_1, Category.TEXTBOOK, 1)
        
        assertNotNull(audioFile)
        assertEquals(Grade.GRADE_1, audioFile?.grade)
        assertEquals(Category.TEXTBOOK, audioFile?.category)
        assertEquals(1, audioFile?.unitNumber)
    }

    @Test
    fun getAudioFile_withInvalidParameters_returnsNull() = runTest {
        // Mock data source
        every { localDataSource.loadAudioFiles() } returns sampleAudioFiles
        
        val audioFile = repository.getAudioFile(Grade.GRADE_6, Category.TEXTBOOK, 99)
        
        assertNull(audioFile)
    }

    @Test
    fun scanAudioFiles_cachesResults() = runTest {
        // Mock data source
        every { localDataSource.loadAudioFiles() } returns sampleAudioFiles
        
        // First call should load from data source
        val result1 = repository.scanAudioFiles()
        assertTrue(result1.isSuccess)
        
        // Subsequent calls should use cache
        val grades1 = repository.getGrades()
        val grades2 = repository.getGrades()
        
        // Both should return same results
        assertEquals(grades1, grades2)
    }

    @Test
    fun scanAudioFiles_withException_returnsFailure() = runTest {
        // Mock data source to throw exception
        every { localDataSource.loadAudioFiles() } throws RuntimeException("Test error")
        
        val result = repository.scanAudioFiles()
        
        assertTrue(result.isFailure)
    }
}
