package com.shan.word
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(tableName = "filenames")
data class Filename(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)

@Entity(
    tableName = "words",
    foreignKeys = [
        ForeignKey(
            entity = Filename::class,
            parentColumns = ["id"],
            childColumns = ["filenameId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Word(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val word: String,
    val filenameId: Int
) 