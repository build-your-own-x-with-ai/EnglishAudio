package com.iosdevlog.englishaudio.data.model

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
