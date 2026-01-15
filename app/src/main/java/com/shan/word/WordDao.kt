package com.shan.word
import androidx.room.*

@Dao
interface WordDao {
    // Word methods
    @Query("SELECT * FROM words")
    fun getAllWords(): kotlinx.coroutines.flow.Flow<List<Word>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(words: List<Word>)

    @Query("DELETE FROM words")
    suspend fun deleteAllWords()

    @Query("SELECT * FROM words WHERE filenameId = :filenameId")
    fun getWordsForFilenameId(filenameId: Int): kotlinx.coroutines.flow.Flow<List<Word>>

    // Filename methods
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFilename(filename: Filename): Long

    @Query("SELECT * FROM filenames")
    fun getAllFilenames(): kotlinx.coroutines.flow.Flow<List<Filename>>

    @Query("SELECT * FROM filenames WHERE name = :name LIMIT 1")
    suspend fun getFilenameByName(name: String): Filename?
} 