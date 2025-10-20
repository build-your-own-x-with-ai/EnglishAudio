# Design Document

## Overview

本设计文档描述了一个基于 Jetpack Compose 的 Android 英语音频播放应用的架构和实现细节。该应用采用 MVVM 架构模式，使用 Material 3 设计系统，并针对 6-12 岁儿童进行了 UI/UX 优化。

### Key Design Principles

1. **儿童友好**: 大按钮、明亮色彩、简单导航
2. **离线优先**: 所有音频文件存储在本地，无需网络连接
3. **简单直观**: 最多 3 层导航（年级 → 类型 → 单元）
4. **即时反馈**: 点击单元立即播放，无需额外确认

## Architecture

### Overall Architecture Pattern

采用 **MVVM (Model-View-ViewModel)** 架构模式：

```
┌─────────────────────────────────────────┐
│           Presentation Layer            │
│  (Composable Screens + ViewModels)     │
├─────────────────────────────────────────┤
│            Domain Layer                 │
│     (Use Cases + Domain Models)         │
├─────────────────────────────────────────┤
│             Data Layer                  │
│  (Repositories + Data Sources)          │
└─────────────────────────────────────────┘
```

### Navigation Architecture

使用 Jetpack Compose Navigation 实现三层导航结构：

```
GradeSelectionScreen
    ↓ (选择年级)
CategorySelectionScreen
    ↓ (选择课本/单词)
UnitGridScreen
    ↓ (选择单元)
AudioPlayerScreen (底部播放控制栏)
```

### Directory Structure

```
app/src/main/java/com/iosdevlog/englishaudio/
├── data/
│   ├── model/
│   │   ├── AudioFile.kt          # 音频文件数据模型
│   │   ├── Grade.kt              # 年级枚举
│   │   └── Category.kt           # 类型枚举
│   ├── repository/
│   │   └── AudioRepository.kt    # 音频文件仓库
│   └── source/
│       └── LocalAudioDataSource.kt # 本地文件系统数据源
├── domain/
│   ├── usecase/
│   │   ├── GetGradesUseCase.kt
│   │   ├── GetCategoriesUseCase.kt
│   │   ├── GetUnitsUseCase.kt
│   │   └── PlayAudioUseCase.kt
│   └── model/
│       └── PlaybackState.kt      # 播放状态领域模型
├── presentation/
│   ├── screen/
│   │   ├── grade/
│   │   │   ├── GradeSelectionScreen.kt
│   │   │   └── GradeViewModel.kt
│   │   ├── category/
│   │   │   ├── CategorySelectionScreen.kt
│   │   │   └── CategoryViewModel.kt
│   │   ├── unit/
│   │   │   ├── UnitGridScreen.kt
│   │   │   └── UnitViewModel.kt
│   │   └── player/
│   │       ├── AudioPlayerBar.kt
│   │       └── AudioPlayerViewModel.kt
│   ├── navigation/
│   │   └── AppNavigation.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── service/
│   └── AudioPlayerService.kt     # 音频播放服务
└── MainActivity.kt
```

## Components and Interfaces

### 1. Data Layer

#### AudioFile Data Model

```kotlin
data class AudioFile(
    val id: String,              // 唯一标识符
    val grade: Grade,            // 年级
    val category: Category,      // 类型（课本/单词）
    val unitNumber: Int,         // 单元号
    val unitName: String,        // 单元名称（如 "Unit 1", "Project 2"）
    val fileName: String,        // 文件名
    val filePath: String,        // 完整文件路径
    val duration: Long = 0L      // 时长（毫秒）
)

enum class Grade(val displayName: String, val folderPrefix: String) {
    GRADE_1("一年级", "一年级上册"),
    GRADE_2("二年级", "二年级上册"),
    GRADE_3("三年级", "三年级上册"),
    GRADE_4("四年级", "四年级上册"),
    GRADE_5("五年级", "五年级上册"),
    GRADE_6("六年级", "六年级上册")
}

enum class Category(val displayName: String, val folderSuffix: String) {
    TEXTBOOK("课本", "课本"),
    VOCABULARY("单词", "单词")
}
```

#### AudioRepository Interface

```kotlin
interface AudioRepository {
    suspend fun scanAudioFiles(): Result<Unit>
    suspend fun getGrades(): List<Grade>
    suspend fun getCategories(grade: Grade): List<Category>
    suspend fun getUnits(grade: Grade, category: Category): List<AudioFile>
    suspend fun getAudioFile(grade: Grade, category: Category, unitNumber: Int): AudioFile?
}
```

#### LocalAudioDataSource

负责从 assets 目录加载预打包的音频文件：

```kotlin
class LocalAudioDataSource(private val context: Context) {
    
    private val assetManager = context.assets
    
    // 加载 assets 目录下的音频文件
    fun loadAudioFiles(): List<AudioFile> {
        val audioFiles = mutableListOf<AudioFile>()
        
        // 遍历所有年级
        Grade.values().forEach { grade ->
            // 遍历所有类型
            Category.values().forEach { category ->
                val folderPath = "${grade.folderPrefix}${category.folderSuffix}"
                
                try {
                    // 列出该目录下的所有文件
                    val files = assetManager.list(folderPath) ?: emptyArray()
                    
                    files.filter { it.endsWith(".mp3") }.forEach { fileName ->
                        // 解析文件名提取单元信息
                        parseUnitFromFileName(fileName)?.let { (unitNumber, unitName) ->
                            audioFiles.add(
                                AudioFile(
                                    id = "${grade.name}_${category.name}_$unitNumber",
                                    grade = grade,
                                    category = category,
                                    unitNumber = unitNumber,
                                    unitName = unitName,
                                    fileName = fileName,
                                    filePath = "$folderPath/$fileName"
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    // 目录不存在或无法访问，跳过
                    Log.w("LocalAudioDataSource", "Failed to load from $folderPath: ${e.message}")
                }
            }
        }
        
        return audioFiles.sortedWith(
            compareBy({ it.grade.ordinal }, { it.category.ordinal }, { it.unitNumber })
        )
    }
    
    // 解析文件名提取单元信息
    private fun parseUnitFromFileName(fileName: String): Pair<Int, String>? {
        // 匹配 "unit 1", "Unit 10", "Project 2" 等格式
        val unitPattern = Regex("(?i)(unit|project)\\s*(\\d+)", RegexOption.IGNORE_CASE)
        val match = unitPattern.find(fileName) ?: return null
        
        val unitType = match.groupValues[1].capitalize()
        val unitNumber = match.groupValues[2].toIntOrNull() ?: return null
        val unitName = "$unitType $unitNumber"
        
        return Pair(unitNumber, unitName)
    }
    
    // 获取音频文件的 AssetFileDescriptor（用于 MediaPlayer）
    fun getAudioFileDescriptor(filePath: String): AssetFileDescriptor {
        return assetManager.openFd(filePath)
    }
}
```

### 2. Domain Layer

#### Use Cases

```kotlin
class GetGradesUseCase(private val repository: AudioRepository) {
    suspend operator fun invoke(): List<Grade> {
        return repository.getGrades()
    }
}

class GetUnitsUseCase(private val repository: AudioRepository) {
    suspend operator fun invoke(grade: Grade, category: Category): List<AudioFile> {
        return repository.getUnits(grade, category)
    }
}

class PlayAudioUseCase(
    private val repository: AudioRepository,
    private val audioPlayer: AudioPlayerService
) {
    suspend operator fun invoke(audioFile: AudioFile) {
        audioPlayer.play(audioFile)
    }
}
```

#### PlaybackState Domain Model

```kotlin
sealed class PlaybackState {
    object Idle : PlaybackState()
    data class Playing(
        val audioFile: AudioFile,
        val currentPosition: Long,
        val duration: Long
    ) : PlaybackState()
    data class Paused(
        val audioFile: AudioFile,
        val currentPosition: Long,
        val duration: Long
    ) : PlaybackState()
    data class Error(val message: String) : PlaybackState()
}
```

### 3. Presentation Layer

#### GradeSelectionScreen

```kotlin
@Composable
fun GradeSelectionScreen(
    viewModel: GradeViewModel = viewModel(),
    onGradeSelected: (Grade) -> Unit
) {
    val grades by viewModel.grades.collectAsState()
    
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
            items(grades) { grade ->
                GradeCard(
                    grade = grade,
                    onClick = { onGradeSelected(grade) }
                )
            }
        }
    }
}

@Composable
fun GradeCard(grade: Grade, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onClick),
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
```

#### CategorySelectionScreen

```kotlin
@Composable
fun CategorySelectionScreen(
    grade: Grade,
    viewModel: CategoryViewModel = viewModel(),
    onCategorySelected: (Category) -> Unit,
    onBackPressed: () -> Unit
) {
    val categories by viewModel.categories.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(grade.displayName) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "选择类型",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                categories.forEach { category ->
                    CategoryCard(
                        category = category,
                        modifier = Modifier.weight(1f),
                        onClick = { onCategorySelected(category) }
                    )
                }
            }
        }
    }
}
```

#### UnitGridScreen

```kotlin
@Composable
fun UnitGridScreen(
    grade: Grade,
    category: Category,
    viewModel: UnitViewModel = viewModel(),
    onUnitSelected: (AudioFile) -> Unit,
    onBackPressed: () -> Unit
) {
    val units by viewModel.units.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${grade.displayName} - ${category.displayName}") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, "返回")
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
            items(units) { audioFile ->
                UnitCard(
                    audioFile = audioFile,
                    onClick = { onUnitSelected(audioFile) }
                )
            }
        }
    }
}

@Composable
fun UnitCard(audioFile: AudioFile, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
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
                textAlign = TextAlign.Center
            )
        }
    }
}
```

#### AudioPlayerBar

底部播放控制栏，在所有屏幕上可见：

```kotlin
@Composable
fun AudioPlayerBar(
    viewModel: AudioPlayerViewModel = viewModel()
) {
    val playbackState by viewModel.playbackState.collectAsState()
    
    when (val state = playbackState) {
        is PlaybackState.Playing, is PlaybackState.Paused -> {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // 音频信息
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
                    
                    // 进度条
                    Slider(
                        value = when (state) {
                            is PlaybackState.Playing -> state.currentPosition.toFloat()
                            is PlaybackState.Paused -> state.currentPosition.toFloat()
                            else -> 0f
                        },
                        onValueChange = { viewModel.seekTo(it.toLong()) },
                        valueRange = 0f..when (state) {
                            is PlaybackState.Playing -> state.duration.toFloat()
                            is PlaybackState.Paused -> state.duration.toFloat()
                            else -> 100f
                        }
                    )
                    
                    // 时间显示
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTime(
                                when (state) {
                                    is PlaybackState.Playing -> state.currentPosition
                                    is PlaybackState.Paused -> state.currentPosition
                                    else -> 0L
                                }
                            ),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = formatTime(
                                when (state) {
                                    is PlaybackState.Playing -> state.duration
                                    is PlaybackState.Paused -> state.duration
                                    else -> 0L
                                }
                            ),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 播放控制按钮
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 停止按钮
                        IconButton(
                            onClick = { viewModel.stop() },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                Icons.Default.Stop,
                                contentDescription = "停止",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(24.dp))
                        
                        // 播放/暂停按钮
                        IconButton(
                            onClick = { viewModel.togglePlayPause() },
                            modifier = Modifier.size(64.dp)
                        ) {
                            Icon(
                                imageVector = if (state is PlaybackState.Playing) {
                                    Icons.Default.Pause
                                } else {
                                    Icons.Default.PlayArrow
                                },
                                contentDescription = if (state is PlaybackState.Playing) "暂停" else "播放",
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }
            }
        }
        else -> { /* 不显示播放栏 */ }
    }
}
```

### 4. Service Layer

#### AudioPlayerService

使用 Android MediaPlayer 实现音频播放：

```kotlin
class AudioPlayerService(private val context: Context) {
    
    private var mediaPlayer: MediaPlayer? = null
    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.Idle)
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()
    
    private var updateJob: Job? = null
    
    fun play(audioFile: AudioFile) {
        try {
            // 释放之前的播放器
            release()
            
            // 从 assets 获取音频文件
            val afd = context.assets.openFd(audioFile.filePath)
            
            // 创建新的 MediaPlayer
            mediaPlayer = MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                prepare()
                start()
                
                setOnCompletionListener {
                    _playbackState.value = PlaybackState.Idle
                    updateJob?.cancel()
                }
            }
            
            // 开始更新播放状态
            startUpdatingPlaybackState(audioFile)
            
        } catch (e: Exception) {
            _playbackState.value = PlaybackState.Error("播放失败: ${e.message}")
        }
    }
    
    fun pause() {
        mediaPlayer?.pause()
        updatePlaybackState()
    }
    
    fun resume() {
        mediaPlayer?.start()
        updatePlaybackState()
    }
    
    fun stop() {
        mediaPlayer?.stop()
        release()
        _playbackState.value = PlaybackState.Idle
    }
    
    fun seekTo(position: Long) {
        mediaPlayer?.seekTo(position.toInt())
        updatePlaybackState()
    }
    
    private fun startUpdatingPlaybackState(audioFile: AudioFile) {
        updateJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                updatePlaybackState(audioFile)
                delay(100) // 每100ms更新一次
            }
        }
    }
    
    private fun updatePlaybackState(audioFile: AudioFile? = null) {
        val player = mediaPlayer ?: return
        val currentAudioFile = audioFile ?: (_playbackState.value as? PlaybackState.Playing)?.audioFile ?: return
        
        _playbackState.value = if (player.isPlaying) {
            PlaybackState.Playing(
                audioFile = currentAudioFile,
                currentPosition = player.currentPosition.toLong(),
                duration = player.duration.toLong()
            )
        } else {
            PlaybackState.Paused(
                audioFile = currentAudioFile,
                currentPosition = player.currentPosition.toLong(),
                duration = player.duration.toLong()
            )
        }
    }
    
    fun release() {
        updateJob?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
```

## Data Models

### Audio File Organization

音频文件按以下目录结构组织在 `assets` 目录中：

```
assets/
├── 一年级上册课本/
│   ├── 一上英课本unit 1.mp3
│   ├── 一上英课本unit 2.mp3
│   └── ...
├── 一年级上册单词/
│   ├── 【单词】一年级上册Unit 1.mp3
│   └── ...
├── 二年级上册课本/
├── 二年级上册单词/
└── ...
```

### File Naming Convention

- 课本音频: `{年级}上英课本unit {单元号}.mp3` 或 `{年级}上英课本Project {项目号}.mp3`
- 单词音频: `【单词】{年级}上册Unit {单元号}.mp3`

## Error Handling

### Error Scenarios

1. **音频文件不存在**: 显示友好错误消息，建议检查文件
2. **播放失败**: 显示错误提示，允许重试
3. **文件格式不支持**: 记录日志，跳过该文件
4. **目录结构不匹配**: 记录警告，尝试智能解析

### Error Handling Strategy

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception, val message: String) : Result<Nothing>()
}

// 在 ViewModel 中处理错误
class UnitViewModel : ViewModel() {
    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()
    
    fun loadUnits(grade: Grade, category: Category) {
        viewModelScope.launch {
            when (val result = getUnitsUseCase(grade, category)) {
                is Result.Success -> {
                    _units.value = result.data
                    _errorState.value = null
                }
                is Result.Error -> {
                    _errorState.value = "加载失败: ${result.message}"
                }
            }
        }
    }
}
```

## Testing Strategy

### Unit Tests

1. **Data Layer Tests**
   - `LocalAudioDataSourceTest`: 测试文件扫描和解析逻辑
   - `AudioRepositoryTest`: 测试仓库数据操作

2. **Domain Layer Tests**
   - `GetUnitsUseCaseTest`: 测试获取单元列表逻辑
   - `PlayAudioUseCaseTest`: 测试播放音频逻辑

3. **ViewModel Tests**
   - `GradeViewModelTest`: 测试年级选择状态管理
   - `AudioPlayerViewModelTest`: 测试播放状态管理

### UI Tests

1. **Navigation Tests**
   - 测试从年级选择到单元网格的完整导航流程
   - 测试返回按钮功能

2. **Interaction Tests**
   - 测试点击单元卡片触发播放
   - 测试播放控制按钮（播放、暂停、停止）

### Integration Tests

1. **End-to-End Tests**
   - 测试完整的用户流程：选择年级 → 选择类型 → 选择单元 → 播放音频
   - 测试音频播放完成后的状态重置

## UI/UX Design Considerations

### Child-Friendly Design

1. **大触摸目标**: 所有按钮至少 48dp，卡片至少 80dp 高度
2. **明亮色彩**: 使用 Material 3 的鲜艳配色方案
3. **简单图标**: 使用 Material Icons 的标准图标
4. **即时反馈**: 点击时显示涟漪效果和状态变化

### Accessibility

1. **内容描述**: 所有交互元素都有 contentDescription
2. **文字大小**: 使用 Material 3 Typography，确保可读性
3. **对比度**: 确保文字和背景有足够对比度
4. **触摸目标**: 符合 Android 无障碍指南

### Performance Optimization

1. **懒加载**: 使用 LazyColumn 和 LazyVerticalGrid
2. **状态管理**: 使用 StateFlow 避免不必要的重组
3. **资源释放**: 在 ViewModel onCleared 中释放 MediaPlayer
4. **内存优化**: 避免同时加载多个音频文件

## Dependencies

### Required Libraries

```kotlin
// Jetpack Compose
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.ui:ui-tooling-preview")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.5")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// Media Player (已包含在 Android SDK 中)
// android.media.MediaPlayer
```

## Implementation Notes

1. **音频文件部署**: 
   - 将 Download.py 下载的所有音频文件夹复制到 `app/src/main/assets/` 目录
   - 保持原有目录结构：`一年级上册课本/`, `一年级上册单词/`, 等
   - 确保文件名保持不变，以便正确解析单元信息

2. **首次启动**: 
   - 应用启动时从 assets 目录加载音频文件元数据
   - 构建音频文件索引并缓存到内存

3. **缓存策略**: 
   - 将加载结果缓存到 Repository 层，避免重复读取 assets
   - 使用单例模式确保 AudioRepository 只初始化一次

4. **后台播放**: 
   - 当前设计不支持后台播放，退出应用时停止播放
   - 未来可扩展为 MediaService 实现后台播放

5. **状态保存**: 
   - 使用 SavedStateHandle 保存当前播放状态，支持配置更改
   - 保存当前选择的年级、类型和播放位置

6. **APK 大小优化**:
   - 所有音频文件将打包到 APK 中，可能导致 APK 较大
   - 考虑使用 Android App Bundle (AAB) 格式发布
   - 未来可考虑按需下载或分包策略
