package com.shan.word.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "words",
    foreignKeys = [
        ForeignKey(
            entity = FilenameEntity::class,
            parentColumns = ["id"],
            childColumns = ["filenameId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("filenameId")]
)
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val word: String,
    val filenameId: Int
) 