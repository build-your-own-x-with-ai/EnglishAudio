package com.iosdevlog.englishaudio.presentation.screen.grade

import com.iosdevlog.englishaudio.data.model.Grade
import com.iosdevlog.englishaudio.domain.usecase.GetGradesUseCase
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
 * Unit tests for GradeViewModel
 * Requirements: 1.1
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GradeViewModelTest {

    private lateinit var getGradesUseCase: GetGradesUseCase
    private lateinit var viewModel: GradeViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getGradesUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Test GradeViewModel loads grades correctly
     * Requirements: 1.1
     */
    @Test
    fun init_loadsGrades() = runTest {
        // Mock use case to return all grades
        val expectedGrades = listOf(
            Grade.GRADE_1,
            Grade.GRADE_2,
            Grade.GRADE_3,
            Grade.GRADE_4,
            Grade.GRADE_5,
            Grade.GRADE_6
        )
        coEvery { getGradesUseCase() } returns expectedGrades
        
        // Create ViewModel (init block will load grades)
        viewModel = GradeViewModel(getGradesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verify grades are loaded
        val grades = viewModel.grades.first()
        assertEquals(6, grades.size)
        assertEquals(expectedGrades, grades)
    }

    @Test
    fun init_withPartialGrades_loadsAvailableGrades() = runTest {
        // Mock use case to return only some grades
        val expectedGrades = listOf(
            Grade.GRADE_1,
            Grade.GRADE_3,
            Grade.GRADE_5
        )
        coEvery { getGradesUseCase() } returns expectedGrades
        
        viewModel = GradeViewModel(getGradesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val grades = viewModel.grades.first()
        assertEquals(3, grades.size)
        assertEquals(expectedGrades, grades)
    }

    @Test
    fun init_withEmptyGrades_loadsEmptyList() = runTest {
        // Mock use case to return empty list
        coEvery { getGradesUseCase() } returns emptyList()
        
        viewModel = GradeViewModel(getGradesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val grades = viewModel.grades.first()
        assertTrue(grades.isEmpty())
    }

    @Test
    fun grades_initiallyEmpty() = runTest {
        // Mock use case
        coEvery { getGradesUseCase() } returns listOf(Grade.GRADE_1)
        
        viewModel = GradeViewModel(getGradesUseCase)
        
        // Before coroutine completes, grades should be empty
        val initialGrades = viewModel.grades.value
        assertTrue(initialGrades.isEmpty())
    }
}
