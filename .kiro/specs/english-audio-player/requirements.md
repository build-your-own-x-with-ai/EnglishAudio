# Requirements Document

## Introduction

本文档定义了一个 Android 英语音频播放应用的需求。该应用旨在为小学生提供一个简单易用的界面，用于播放按年级、类型（课本/单词）和单元组织的英语学习音频文件。音频文件由 Python 脚本从微信公众号文章下载并按照特定目录结构组织。

## Glossary

- **AudioPlayer**: Android 应用中负责音频播放控制的系统组件
- **GradeSelector**: 年级选择界面组件，允许用户选择一年级到六年级
- **CategorySelector**: 类型选择界面组件，允许用户选择"课本"或"单词"
- **UnitGrid**: 单元网格界面组件，以网格形式展示所有可用单元
- **AudioFile**: 存储在应用本地的 MP3 音频文件
- **NavigationSystem**: 应用的导航系统，管理界面之间的跳转

## Requirements

### Requirement 1

**User Story:** 作为一名小学生用户，我想要选择我的年级，以便访问适合我年级的英语学习内容

#### Acceptance Criteria

1. WHEN the application launches, THE GradeSelector SHALL display six grade options (一年级 through 六年级) in a vertically scrollable list
2. WHEN a user taps on a grade option, THE NavigationSystem SHALL navigate to the CategorySelector screen for the selected grade
3. THE GradeSelector SHALL display each grade option with large, readable text suitable for children aged 6-12 years
4. THE GradeSelector SHALL use child-friendly colors and visual design elements

### Requirement 2

**User Story:** 作为一名小学生用户，我想要选择学习类型（课本或单词），以便访问我需要的具体学习材料

#### Acceptance Criteria

1. WHEN a user selects a grade, THE CategorySelector SHALL display two category options: "课本" and "单词"
2. WHEN a user taps on a category option, THE NavigationSystem SHALL navigate to the UnitGrid screen for the selected grade and category
3. THE CategorySelector SHALL display the selected grade name at the top of the screen
4. THE CategorySelector SHALL provide a back button to return to the GradeSelector

### Requirement 3

**User Story:** 作为一名小学生用户，我想要看到所有可用的单元，以便选择我想要学习的单元

#### Acceptance Criteria

1. WHEN a user selects a category, THE UnitGrid SHALL display all available units in a 3-column grid layout
2. THE UnitGrid SHALL load unit information from the local file system based on the selected grade and category
3. WHEN no audio files exist for a unit, THE UnitGrid SHALL display the unit as disabled or hidden
4. THE UnitGrid SHALL display unit names in the format "Unit 1", "Unit 2", etc., or "Project 2", "Project 3", etc.
5. THE UnitGrid SHALL provide a back button to return to the CategorySelector

### Requirement 4

**User Story:** 作为一名小学生用户，我想要点击单元后立即播放音频，以便快速开始学习

#### Acceptance Criteria

1. WHEN a user taps on a unit in the UnitGrid, THE AudioPlayer SHALL immediately start playing the corresponding audio file
2. WHEN an audio file is playing, THE AudioPlayer SHALL display playback controls including play/pause, stop, and seek bar
3. WHEN an audio file finishes playing, THE AudioPlayer SHALL automatically stop and reset to the beginning
4. IF an audio file fails to load, THEN THE AudioPlayer SHALL display an error message to the user

### Requirement 5

**User Story:** 作为一名小学生用户，我想要控制音频播放（暂停、继续、停止），以便根据我的学习节奏调整

#### Acceptance Criteria

1. WHILE an audio file is playing, THE AudioPlayer SHALL display a pause button that stops playback when tapped
2. WHILE an audio file is paused, THE AudioPlayer SHALL display a play button that resumes playback when tapped
3. THE AudioPlayer SHALL provide a stop button that stops playback and returns to the beginning of the audio
4. THE AudioPlayer SHALL display the current playback position and total duration in MM:SS format
5. THE AudioPlayer SHALL provide a seek bar that allows users to jump to any position in the audio

### Requirement 6

**User Story:** 作为一名小学生用户，我想要看到当前正在播放的内容信息，以便确认我在学习正确的材料

#### Acceptance Criteria

1. WHILE an audio file is playing, THE AudioPlayer SHALL display the grade, category, and unit name
2. THE AudioPlayer SHALL display the audio file name at the top of the playback screen
3. THE AudioPlayer SHALL maintain the playback controls visible throughout the playback session

### Requirement 7

**User Story:** 作为一名小学生用户，我想要应用包含所有已下载的音频文件，以便无需额外下载即可直接使用

#### Acceptance Criteria

1. THE AudioPlayer SHALL include all downloaded audio files in the APK's assets directory
2. WHEN the application starts, THE AudioPlayer SHALL load audio file metadata from the assets directory based on the predefined directory structure
3. THE AudioPlayer SHALL organize audio files by grade and category matching the directory structure: {年级上册课本|单词}/{文件名}.mp3
4. THE AudioPlayer SHALL parse file names to extract unit numbers using the patterns "unit {number}", "Unit {number}", or "Project {number}"
5. THE AudioPlayer SHALL only load audio files from the predefined directory structure, ignoring any other files

### Requirement 8

**User Story:** 作为一名小学生用户，我想要界面简单直观，以便我可以独立使用应用而不需要成人帮助

#### Acceptance Criteria

1. THE NavigationSystem SHALL use large, touch-friendly buttons with minimum 48dp touch targets
2. THE NavigationSystem SHALL provide clear visual feedback when buttons are tapped
3. THE AudioPlayer SHALL use simple, recognizable icons for playback controls
4. THE AudioPlayer SHALL minimize text and maximize visual elements suitable for early readers
