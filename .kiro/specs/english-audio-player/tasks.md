# Implementation Plan

- [x] 1. Set up project structure and data models
  - Create package structure for data, domain, presentation, and service layers
  - Define Grade enum with display names and folder prefixes
  - Define Category enum with display names and folder suffixes
  - Create AudioFile data class with all required properties
  - Create PlaybackState sealed class for domain layer
  - _Requirements: 7.2, 7.3_

- [x] 2. Implement data layer components
  - [x] 2.1 Create LocalAudioDataSource class
    - Implement loadAudioFiles() method to read from assets directory
    - Implement parseUnitFromFileName() method to extract unit information
    - Implement getAudioFileDescriptor() method for MediaPlayer
    - _Requirements: 7.1, 7.4, 7.5_
  
  - [x] 2.2 Create AudioRepository interface and implementation
    - Define repository interface with methods for getting grades, categories, and units
    - Implement repository with caching strategy
    - Add error handling for missing or invalid files
    - _Requirements: 7.2, 7.3_

- [x] 3. Implement audio playback service
  - [x] 3.1 Create AudioPlayerService class
    - Implement play() method using MediaPlayer with AssetFileDescriptor
    - Implement pause(), resume(), stop(), and seekTo() methods
    - Add StateFlow for playback state management
    - Implement automatic state updates every 100ms during playback
    - _Requirements: 4.1, 4.2, 4.3, 5.1, 5.2, 5.3_
  
  - [x] 3.2 Add playback completion and error handling
    - Implement onCompletion listener to reset state
    - Add error handling with user-friendly messages
    - Implement resource cleanup in release() method
    - _Requirements: 4.3, 4.4_

- [x] 4. Implement domain layer use cases
  - Create GetGradesUseCase to retrieve available grades
  - Create GetCategoriesUseCase to retrieve categories for a grade
  - Create GetUnitsUseCase to retrieve units for grade and category
  - Create PlayAudioUseCase to handle audio playback
  - _Requirements: 1.1, 2.1, 3.1, 4.1_

- [x] 5. Create grade selection screen
  - [x] 5.1 Implement GradeViewModel
    - Load available grades on initialization
    - Expose grades as StateFlow
    - _Requirements: 1.1_
  
  - [x] 5.2 Create GradeSelectionScreen composable
    - Display grades in LazyColumn with large, readable cards
    - Implement GradeCard composable with child-friendly design
    - Add navigation to category selection on grade tap
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 8.1, 8.2_

- [x] 6. Create category selection screen
  - [x] 6.1 Implement CategoryViewModel
    - Load categories for selected grade
    - Expose categories as StateFlow
    - _Requirements: 2.1_
  
  - [x] 6.2 Create CategorySelectionScreen composable
    - Display grade name in TopAppBar
    - Show two category cards (课本 and 单词) in a row
    - Implement back button navigation
    - Add navigation to unit grid on category tap
    - _Requirements: 2.1, 2.2, 2.3, 2.4_

- [x] 7. Create unit grid screen
  - [x] 7.1 Implement UnitViewModel
    - Load units for selected grade and category
    - Expose units as StateFlow
    - Handle empty or missing unit scenarios
    - _Requirements: 3.1, 3.2, 3.3_
  
  - [x] 7.2 Create UnitGridScreen composable
    - Display units in 3-column LazyVerticalGrid
    - Implement UnitCard composable with unit names
    - Show grade and category in TopAppBar
    - Implement back button navigation
    - Trigger audio playback on unit tap
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 4.1_

- [x] 8. Create audio player UI components
  - [x] 8.1 Implement AudioPlayerViewModel
    - Integrate with AudioPlayerService
    - Expose playback state as StateFlow
    - Implement togglePlayPause(), stop(), and seekTo() methods
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5_
  
  - [x] 8.2 Create AudioPlayerBar composable
    - Display current audio file information
    - Show playback progress with Slider
    - Display current position and total duration in MM:SS format
    - Add play/pause and stop buttons with large touch targets
    - Show/hide bar based on playback state
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 6.1, 6.2, 6.3, 8.1, 8.2_

- [x] 9. Implement navigation system
  - Create AppNavigation composable with NavHost
  - Define navigation routes for all screens
  - Pass parameters (grade, category) between screens
  - Integrate AudioPlayerBar as bottom bar across all screens
  - _Requirements: 1.2, 2.2, 3.5, 8.1, 8.2_

- [x] 10. Update MainActivity and theme
  - Update MainActivity to use AppNavigation
  - Customize theme colors for child-friendly design
  - Ensure large typography for readability
  - Add content descriptions for accessibility
  - _Requirements: 1.3, 1.4, 8.1, 8.2, 8.3, 8.4_

- [x] 11. Deploy audio files to assets
  - Copy all downloaded audio folders to app/src/main/assets/
  - Verify directory structure matches expected format
  - Ensure all file names are preserved correctly
  - Test that all audio files are accessible from assets
  - _Requirements: 7.1_

- [x] 12. Add utility functions
  - Create formatTime() function to convert milliseconds to MM:SS format
  - Add extension functions for common operations
  - Implement logging utilities for debugging
  - _Requirements: 5.4, 6.2_

- [x] 13. Write unit tests for core components
  - [x] 13.1 Test LocalAudioDataSource
    - Test loadAudioFiles() with valid directory structure
    - Test parseUnitFromFileName() with various file name formats
    - _Requirements: 7.4, 7.5_
  
  - [x] 13.2 Test AudioRepository
    - Test getGrades() returns all grades
    - Test getUnits() filters by grade and category correctly
    - _Requirements: 7.2, 7.3_
  
  - [x] 13.3 Test ViewModels
    - Test GradeViewModel loads grades correctly
    - Test UnitViewModel loads units for selected grade and category
    - Test AudioPlayerViewModel state transitions
    - _Requirements: 1.1, 3.1, 5.1, 5.2, 5.3_
