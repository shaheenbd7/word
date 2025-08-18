package com.shan.word.data.local.dao

import androidx.room.*
import com.shan.word.data.local.entity.WordEntity
import com.shan.word.data.local.entity.FilenameEntity
import com.shan.word.domain.entity.WordStatus
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

    @Query("SELECT * FROM words WHERE word = :word LIMIT 1")
    suspend fun getWordByText(word: String): WordEntity?

    @Update
    suspend fun updateWord(word: WordEntity)

    @Query("SELECT * FROM words WHERE isFavorite = 1")
    fun getFavoriteWords(): kotlinx.coroutines.flow.Flow<List<WordEntity>>

    @Query("UPDATE words SET isFavorite = :isFavorite WHERE word = :word")
    suspend fun updateFavoriteStatus(word: String, isFavorite: Boolean)

    @Query("SELECT * FROM words WHERE status = :status")
    suspend fun getWordsByStatus(status: WordStatus): List<WordEntity>

    @Query("UPDATE words SET status = :status WHERE word = :word")
    suspend fun updateWordStatus(word: String, status: WordStatus)
} 