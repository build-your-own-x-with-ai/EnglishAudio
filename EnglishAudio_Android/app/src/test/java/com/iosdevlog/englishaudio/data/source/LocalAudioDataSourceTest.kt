package com.iosdevlog.englishaudio.data.source

import android.content.Context
import android.content.res.AssetManager
import com.iosdevlog.englishaudio.data.model.Category
import com.iosdevlog.englishaudio.data.model.Grade
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for LocalAudioDataSource
 * Requirements: 7.4, 7.5
 */
class LocalAudioDataSourceTest {

    private lateinit var context: Context
    private lateinit var assetManager: AssetManager
    private lateinit var dataSource: LocalAudioDataSource

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        assetManager = mockk(relaxed = true)
        every { context.assets } returns assetManager
        dataSource = LocalAudioDataSource(context)
    }

    /**
     * Test parseUnitFromFileName with various file name formats
     * Requirements: 7.4, 7.5
     */
    @Test
    fun parseUnitFromFileName_withLowercaseUnit_returnsCorrectPair() {
        // Test "unit 1" format
        val result = dataSource.parseUnitFromFileName("一上英课本unit 1.mp3")
        
        assertNotNull(result)
        assertEquals(1, result?.first)
        assertEquals("Unit 1", result?.second)
    }

    @Test
    fun parseUnitFromFileName_withUppercaseUnit_returnsCorrectPair() {
        // Test "Unit 10" format
        val result = dataSource.parseUnitFromFileName("三上英课本Unit 10.mp3")
        
        assertNotNull(result)
        assertEquals(10, result?.first)
        assertEquals("Unit 10", result?.second)
    }

    @Test
    fun parseUnitFromFileName_withProject_returnsCorrectPair() {
        // Test "Project 2" format
        val result = dataSource.parseUnitFromFileName("一上英课本Project 2.mp3")
        
        assertNotNull(result)
        assertEquals(2, result?.first)
        assertEquals("Project 2", result?.second)
    }

    @Test
    fun parseUnitFromFileName_withLowercaseProject_returnsCorrectPair() {
        // Test "project 3" format
        val result = dataSource.parseUnitFromFileName("二上英课本project 3.mp3")
        
        assertNotNull(result)
        assertEquals(3, result?.first)
        assertEquals("Project 3", result?.second)
    }

    @Test
    fun parseUnitFromFileName_withVocabularyFormat_returnsCorrectPair() {
        // Test vocabulary file format
        val result = dataSource.parseUnitFromFileName("【单词】一年级上册Unit 1.mp3")
        
        assertNotNull(result)
        assertEquals(1, result?.first)
        assertEquals("Unit 1", result?.second)
    }

    @Test
    fun parseUnitFromFileName_withDoubleDigitUnit_returnsCorrectPair() {
        // Test double digit unit number
        val result = dataSource.parseUnitFromFileName("【单词】五年级上册Unit 12.mp3")
        
        assertNotNull(result)
        assertEquals(12, result?.first)
        assertEquals("Unit 12", result?.second)
    }

    @Test
    fun parseUnitFromFileName_withInvalidFormat_returnsNull() {
        // Test invalid format without unit/project
        val result = dataSource.parseUnitFromFileName("invalid_file.mp3")
        
        assertNull(result)
    }

    @Test
    fun parseUnitFromFileName_withNoNumber_returnsNull() {
        // Test format with unit but no number
        val result = dataSource.parseUnitFromFileName("unit.mp3")
        
        assertNull(result)
    }

    // Note: loadAudioFiles() tests require Android Context and AssetManager which are difficult
    // to mock properly in unit tests. These are better tested as integration tests.
    // The core parsing logic is tested above through parseUnitFromFileName tests.
}
