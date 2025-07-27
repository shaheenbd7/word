package com.shan.word.data.local.dao

import androidx.room.*
import com.shan.word.data.local.entity.WordEntity
import com.shan.word.data.local.entity.FilenameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    // Word methods
    @Query("SELECT * FROM words")
    fun getAllWords(): Flow<List<WordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(words: List<WordEntity>)

    @Query("DELETE FROM words")
    suspend fun deleteAllWords()

    @Query("SELECT * FROM words WHERE filenameId = :filenameId")
    fun getWordsForFilenameId(filenameId: Int): Flow<List<WordEntity>>

    // Filename methods
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFilename(filename: FilenameEntity): Long

    @Query("SELECT * FROM filenames")
    fun getAllFilenames(): Flow<List<FilenameEntity>>

    @Query("SELECT * FROM filenames WHERE name = :name LIMIT 1")
    suspend fun getFilenameByName(name: String): FilenameEntity?
} 