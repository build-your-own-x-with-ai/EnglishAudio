package com.iosdevlog.englishaudio.presentation.screen.unit

import com.iosdevlog.englishaudio.data.model.AudioFile
import com.iosdevlog.englishaudio.data.model.Category
import com.iosdevlog.englishaudio.data.model.Grade
import com.iosdevlog.englishaudio.domain.usecase.GetUnitsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for UnitViewModel
 * Requirements: 3.1
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UnitViewModelTest {

    private lateinit var getUnitsUseCase: GetUnitsUseCase
    private lateinit var viewModel: UnitViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val sampleUnits = listOf(
        AudioFile(
            id = "GRADE_1_TEXTBOOK_1",
            grade = Grade.GRADE_1,
            category = Category.TEXTBOOK,
            unitNumber = 1,
            unitName = "Unit 1",
            fileName = "unit 1.mp3",
            filePath = "path/unit 1.mp3"
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
            id = "GRADE_1_TEXTBOOK_3",
            grade = Grade.GRADE_1,
            category = Category.TEXTBOOK,
            unitNumber = 3,
            unitName = "Unit 3",
            fileName = "unit 3.mp3",
            filePath = "path/unit 3.mp3"
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getUnitsUseCase = mockk()
        viewModel = UnitViewModel(getUnitsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Test UnitViewModel loads units for selected grade and category
     * Requirements: 3.1
     */
    @Test
    fun loadUnits_withValidGradeAndCategory_loadsUnits() = runTest {
        // Mock use case
        coEvery { getUnitsUseCase(Grade.GRADE_1, Category.TEXTBOOK) } returns sampleUnits
        
        viewModel.loadUnits(Grade.GRADE_1, Category.TEXTBOOK)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val units = viewModel.units.first()
        assertEquals(3, units.size)
        assertEquals(sampleUnits, units)
    }

    @Test
    fun loadUnits_setsLoadingState() = runTest {
        // Mock use case
        coEvery { getUnitsUseCase(Grade.GRADE_1, Category.TEXTBOOK) } returns sampleUnits
        
        viewModel.loadUnits(Grade.GRADE_1, Category.TEXTBOOK)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // After completion, should not be loading
        assertFalse(viewModel.isLoading.value)
        
        // Verify units were loaded
        assertEquals(3, viewModel.units.value.size)
    }

    @Test
    fun loadUnits_withEmptyResult_setsErrorMessage() = runTest {
        // Mock use case to return empty list
        coEvery { getUnitsUseCase(Grade.GRADE_2, Category.VOCABULARY) } returns emptyList()
        
        viewModel.loadUnits(Grade.GRADE_2, Category.VOCABULARY)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val units = viewModel.units.first()
        assertTrue(units.isEmpty())
        
        val errorMessage = viewModel.errorMessage.first()
        assertEquals("没有找到音频文件", errorMessage)
    }

    @Test
    fun loadUnits_withException_setsErrorState() = runTest {
        // Mock use case to throw exception
        coEvery { getUnitsUseCase(Grade.GRADE_1, Category.TEXTBOOK) } throws RuntimeException("Test error")
        
        viewModel.loadUnits(Grade.GRADE_1, Category.TEXTBOOK)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val units = viewModel.units.first()
        assertTrue(units.isEmpty())
        
        val errorMessage = viewModel.errorMessage.first()
        assertTrue(errorMessage?.contains("加载失败") == true)
    }

    @Test
    fun loadUnits_clearsErrorMessage() = runTest {
        // Mock use case
        coEvery { getUnitsUseCase(Grade.GRADE_1, Category.TEXTBOOK) } returns sampleUnits
        
        viewModel.loadUnits(Grade.GRADE_1, Category.TEXTBOOK)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val errorMessage = viewModel.errorMessage.first()
        assertNull(errorMessage)
    }

    @Test
    fun clearError_resetsErrorMessage() = runTest {
        // Mock use case to return empty
        coEvery { getUnitsUseCase(Grade.GRADE_1, Category.TEXTBOOK) } returns emptyList()
        
        viewModel.loadUnits(Grade.GRADE_1, Category.TEXTBOOK)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Should have error message
        assertNotNull(viewModel.errorMessage.first())
        
        // Clear error
        viewModel.clearError()
        
        // Error should be null
        assertNull(viewModel.errorMessage.first())
    }

    @Test
    fun units_initiallyEmpty() {
        val units = viewModel.units.value
        assertTrue(units.isEmpty())
    }

    @Test
    fun isLoading_initiallyFalse() {
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun errorMessage_initiallyNull() {
        assertNull(viewModel.errorMessage.value)
    }
}
