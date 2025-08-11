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
    val filenameId: Int,
    val translation: String? = null,
    val pronunciation: String? = null,
    val synonyms: String? = null, // JSON string
    val antonyms: String? = null, // JSON string
    val examples: String? = null, // JSON string
    val definition: String? = null,
    val isFavorite: Boolean = false,
    val audioUrl: String? = null,
    val partOfSpeech: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
) 