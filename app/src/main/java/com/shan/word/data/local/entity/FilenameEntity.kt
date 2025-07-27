package com.shan.word.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "filenames")
data class FilenameEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
) 